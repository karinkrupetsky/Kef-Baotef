package com.example.testgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceView;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GameView extends SurfaceView implements Runnable {
    private Thread thread;
    private Object object;
    private Background background;
    private int screenX, screenY;
    public static float screenRatioX, screenRatioY;
    public static int monsterScore, rocketScore, bossScore;
    private Random random;
    private Paint paint;
    private Canvas canvas;
    private Player player;
    private List<Bullet> bullets, bulletsTrash;
    private List<Bullet> bossBullets, bossBulletsTrash;
    private List<Monster> monsters, monstersTrash;
    private List<Rocket> rockets, rocketsTrash;
    private List<Missle> kipatBarzelRockets,kipatBarzelRocketsTrash;
    private Missle missle;
    private Boss boss = null;
    /*Player's movement on the X axis*/
    private float playerX, dplayerX, bossLifeCounter;
    private Handler handler;
    /*Handles "red color" start time, duration, enemy creation time and falling speed*/
    private float timeToRedColor, redColorDuration, timeToBoss, enemyCreationTime;
    /*Timer for  game scheduling*/
    private float timeCounter;
    private int enemySpeed;
    /*Handles triggers in the game*/
    private GameState gameState;
    private boolean redColorDone = false, isGameOver = false, createEnemy = false,
            bossShootingTime = false, redColorAlert = false,kipatBarzel=false;
    private static boolean playMusic = true,muteSoundPool=false;
    private Boolean isPlaying = true;
    /*Handles score on screen and player's health*/
    private int score, health, levelNumber,rocketCounter=0;
    /*Schedules the game*/
    private Timer timer;
    /*Score and health bitmaps*/
    private Bitmap scoreBitmap, healthBitmap, pauseBitmap, playBitmap, redColorBitmap, sandBitmap,
            roadBitmap, dirtBitmap,kipatBarzelBitmap;
    /*Game's sound*/
    private SoundPool soundPool;
    private int player_shoot_sound, player_get_hurt_sound, rocket_explosion_sound, tzeva_adom_siren_sound,
            boss_explosion_sound, boss_shoot_sound, boss_voice_sound, button_sound, game_over_sound,
            high_score_sound, level_up_sound, monster_explosion_sound,rocket_launch_sound;
    private MediaPlayer mediaPlayer;

    public GameView(Object object, Context context, int screenX, int screenY, int bgResId,
                    final float enemyCreationTime, int enemySpeed, int monsterScore, int rocketScore,
                    int bossScore, final int redColorDuration, float bossLifeCounter, int levelNumber) {
        super(context);
        GameActivity activity=(GameActivity) context;
        this.object=object;
        /*Screen sizes and ratio*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width1 = displayMetrics.widthPixels;
        int height1 = displayMetrics.heightPixels;
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = width1 / screenX;
        screenRatioY = height1 / screenY;

        /*Creating score and health bitmaps*/
        /*Score*/
        scoreBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.score);
        int height = scoreBitmap.getHeight(), width = scoreBitmap.getWidth();
        scoreBitmap = Bitmap.createBitmap(scoreBitmap, 0, 0, width, height);
        scoreBitmap = Bitmap.createScaledBitmap(scoreBitmap, height / 8, width / 8, false);
        /*Health*/
        healthBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.life);
        height = healthBitmap.getHeight();
        width = healthBitmap.getWidth();
        healthBitmap = Bitmap.createBitmap(healthBitmap, 0, 0, width, height);
        healthBitmap = Bitmap.createScaledBitmap(healthBitmap, height / 18, width / 18, false);
        //pause button
        pauseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_volume_off_black_24dp);
        //play button
        playBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_volume_up_black_24dp);
        //Red color alert
        redColorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.redcolor);
        height = redColorBitmap.getHeight();
        width = redColorBitmap.getWidth();
        redColorBitmap = Bitmap.createBitmap(redColorBitmap, 0, 0, width, height);
        redColorBitmap = Bitmap.createScaledBitmap(redColorBitmap, height / 6, width / 6, false);
        //kipat barzel bitmap
        kipatBarzelBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.missle);
        height=kipatBarzelBitmap.getHeight();
        width=kipatBarzelBitmap.getWidth();
        kipatBarzelBitmap=Bitmap.createBitmap(kipatBarzelBitmap, 0, 0, width, height);
        kipatBarzelBitmap=Bitmap.createScaledBitmap(kipatBarzelBitmap, height / 24, width / 10, false);



        // Sand Bitmap
        sandBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sand_floor);
        sandBitmap = Bitmap.createScaledBitmap(sandBitmap, sandBitmap.getWidth() / 2, sandBitmap.getHeight(), false);

        // Road Bitmap
        roadBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.road_floor);
        roadBitmap = Bitmap.createScaledBitmap(roadBitmap, roadBitmap.getWidth() / 2, roadBitmap.getHeight(), false);
        // Dirt bitmap
        dirtBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dirt_floor2);
        dirtBitmap = Bitmap.createScaledBitmap(dirtBitmap, dirtBitmap.getWidth() / 2, dirtBitmap.getHeight(), false);

        /*Sets the score which the player earns for each enemy's killing*/
        this.monsterScore = monsterScore;
        this.rocketScore = rocketScore;
        this.bossScore = bossScore;

        /*Initial game state*/
        gameState = GameState.NORMAL;


        random = new Random();
        /*Generates time to red color between 35 secs to 50 secs*/
        timeToRedColor =3; //random.nextInt(16) + 35;
        /*Generates time to boss between 20 secs to 40 secs after red color is done*/
        timeToBoss = random.nextInt(21) + 20;

        this.enemyCreationTime = enemyCreationTime;
        this.redColorDuration = redColorDuration;
        this.enemySpeed = enemySpeed;
        this.health = 3 + 2 * levelNumber / 3;
        this.score = 0;
        //TODO:Delete that!!
        score=1900;
        this.timeCounter = 0f;
        this.bossLifeCounter = bossLifeCounter;
        this.levelNumber = levelNumber;

        background = new Background(screenX, screenY, getResources(), bgResId);

        player = new Player(this, screenY, getResources());

        boss = new Boss(getResources(), bossLifeCounter, this.levelNumber - 1, this);
        boss.x = screenX / 2;

        /*Manage arm supply for player's shooting*/
        bullets = new ArrayList<>();
        bulletsTrash = new ArrayList<>();
        /*Monsters' management on the screen*/
        monsters = new ArrayList<>();
        monstersTrash = new ArrayList<>();
        /*Rockets' management on the screen when red color is on*/
        rockets = new ArrayList<>();
        rocketsTrash = new ArrayList<>();
        /*Manage arm supply for boss's shooting*/
        bossBullets = new ArrayList<>();
        bossBulletsTrash = new ArrayList<>();
        /*Manage kipat barzel rockets*/
        kipatBarzelRockets = new ArrayList<>();
        kipatBarzelRocketsTrash = new ArrayList<>();


        paint = new Paint();

        handler = new Handler();

        timer = new Timer();

        /*Sound Construction*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(13)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(13, AudioManager.STREAM_MUSIC, 0);
        }

        player_shoot_sound = soundPool.load(context, R.raw.player_shoot, 1);
        player_get_hurt_sound = soundPool.load(context, R.raw.player_get_hurt, 1);
        rocket_explosion_sound = soundPool.load(context, R.raw.rocket_explosion, 1);
        tzeva_adom_siren_sound = soundPool.load(context, R.raw.tzeva_adom_siren, 1);
        boss_explosion_sound = soundPool.load(context, R.raw.boss_explosion, 1);
        boss_shoot_sound = soundPool.load(context, R.raw.boss_shoot, 1);
        boss_voice_sound = soundPool.load(context, R.raw.boss_voice, 1);
        button_sound = soundPool.load(context, R.raw.button_sound, 1);
        game_over_sound = soundPool.load(context, R.raw.game_over_sound, 1);
        high_score_sound = soundPool.load(context, R.raw.high_score_sound, 1);
        level_up_sound = soundPool.load(context, R.raw.level_up_sound, 1);
        monster_explosion_sound = soundPool.load(context, R.raw.monster_explosion, 1);
        rocket_launch_sound=soundPool.load(context, R.raw.rocket_launch, 1);
        /*Plays the level's music in loops*/
        mediaPlayer = MediaPlayer.create(context, R.raw.levels_music);
        mediaPlayer.setLooping(true);
        if(playMusic)
            mediaPlayer.start();

    }

    @Override
    public void run() {
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                timeCounter += 0.1f;
                DecimalFormat oneDigit = new DecimalFormat("#,##0.0");
                timeCounter = Float.valueOf(oneDigit.format(timeCounter));
                /*Enemy creation time - periodic*/
                double x = (Math.round((timeCounter % enemyCreationTime) * 10) / 10.0);
                if ((x * 10) % (enemyCreationTime * 10) == 0.0) {
                    createEnemy = true;
                    /*Time scheduling for boss's shootings*/
                    if (gameState == GameState.BOSS)
                        bossShootingTime = true;
                    try {
                        Thread.sleep((long) enemyCreationTime * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                /*Red color time*/
                if ((int) timeCounter == (int) timeToRedColor) {
                    //popup red color alert
                    redColorAlert = true;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    redColorAlert = false;
                    //Ensures that we won't get here more than once
                    if(!muteSoundPool)
                        soundPool.play(tzeva_adom_siren_sound, 1, 1, 0, 1, 1);
                    timeToRedColor = -1;
                    gameState = GameState.RED_COLOR;
                    timeCounter = 0f;
                }
                /*Red color duration*/
                else if ((int) timeCounter == redColorDuration && gameState == GameState.RED_COLOR) {
                    //Ensures that we won't get here more than once
                    GameView.this.redColorDuration = -1;
                    gameState = GameState.NORMAL;
                    redColorDone = true;
                    timeCounter = 0f;
                }
                /*Boss time*/
                else if ((int) timeCounter == (int) timeToBoss && redColorDone) {
                    if(!muteSoundPool)
                        soundPool.play(boss_voice_sound, 1, 1, 0, 0, 1);
                    timeToBoss = -1;
                    gameState = GameState.BOSS;
                    timeCounter = 0f;
                }
            }


        }, 0, 100);
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        switch (gameState) {
            case NORMAL: {
                /*Ensures that no rocket left on the screen*/
                if (rockets.size() > 0)
                    updateRockets();
                if(kipatBarzelRockets.size()>0)
                    updateKipatBarzelRockets();

                /*Monster creation*/
                if (createEnemy) {
                    int index = random.nextInt(enemyCreationIndex());
                    Monster monster = new Monster(getResources(), (enemySpeed + extraSpeed()) * (int) screenRatioY, index);
                    monster.x = random.nextInt(4 * screenX / 5);
                    createEnemy = false;
                    monsters.add(monster);
                }
                updateMonsters();

            }
            break;
            case RED_COLOR: {
                /*Ensures that no monster left on the screen*/
                if (monsters.size() > 0)
                    updateMonsters();
                /*Rocket creation*/
                if (createEnemy) {
                    int index = random.nextInt(rocketCreationIndex());
                    Rocket rocket = new Rocket(getResources(), (enemySpeed + extraSpeed()) * (int) screenRatioY, index);
                    rocket.x = random.nextInt(4 * screenX / 5);
                    createEnemy = false;
                    int x=rocket.x;
                    rockets.add(rocket);
                    /*Activates kipat barzel*/
                    if(levelNumber>=5&&kipatBarzel&&score>=1100&&rocketCounter<2*redColorDuration/3) {
                        missle = new Missle(getResources(), x, (2*enemySpeed) * (int) screenRatioY);
                        missle.y = screenY;
                        kipatBarzelRockets.add(missle);
                        soundPool.play(rocket_launch_sound, 1, 1, 0, 0, 1);
                        rocketCounter++;
                    }
                }
                updateRockets();
                updateKipatBarzelRockets();
            }
            break;
            case BOSS: {
                /*Ensures that no monster left on the screen*/
                if (monsters.size() > 0)
                    updateMonsters();
                updateBossMovement();
                updateBossBullets();

            }
            break;
        }
        updateBullets();
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            canvas = getHolder().lockCanvas();

            /*Draw background*/
            canvas.drawBitmap(background.background, background.x, background.y, paint);

            /*Draw the ground which the player stands on*/
            if (levelNumber == 1 || levelNumber == 5)
                canvas.drawBitmap(sandBitmap, 0, player.y + 50 * screenRatioY, paint);
            else if (levelNumber == 2 || levelNumber == 3 || levelNumber == 4 || levelNumber == 6 || levelNumber == 7)
                canvas.drawBitmap(roadBitmap, 0, player.y + 50 * screenRatioY, paint);
            else if (levelNumber == 8)
                canvas.drawBitmap(dirtBitmap, 0, player.y + 50 * screenRatioY, paint);

            /*Draw the player*/
            canvas.drawBitmap(player.getPlayer(), player.x, player.y, paint);

            switch (gameState) {
                case NORMAL: {
                    /*Draw the last monsters left*/
                    if (rockets.size() > 0) {
                        for (Rocket rocket : rockets)
                            if (rocket.wasIntercepted)
                                canvas.drawBitmap(rocket.getExplosion(), rocket.x - rocket.width, rocket.y + rocket.height / 4, paint);
                            else
                                canvas.drawBitmap(rocket.getRocket(), rocket.x, rocket.y, paint);
                    }
                    if(kipatBarzelRockets.size()>0)
                        for(Missle missle:kipatBarzelRockets)
                            canvas.drawBitmap(missle.missle,missle.x,missle.y,paint);
                    /*Draw the monsters*/
                    for (Monster monster : monsters) {
                        if (monster.wasShot)
                            canvas.drawBitmap(Monster.getExplosion(), monster.x - monster.width / 3, monster.y, paint);
                        else
                            canvas.drawBitmap(monster.getMonster(), monster.x, monster.y, paint);
                    }
                }
                break;

                case RED_COLOR: {
                    /*Draw the last monsters left*/
                    if (monsters.size() > 0)
                        for (Monster monster : monsters) {
                            if (monster.wasShot)
                                canvas.drawBitmap(Monster.getExplosion(), monster.x - monster.width / 2, monster.y, paint);
                            else
                                canvas.drawBitmap(monster.getMonster(), monster.x, monster.y, paint);
                        }
                    /*Draw kipat barzel rockets*/
                    if(kipatBarzel)
                        for(Missle missle:kipatBarzelRockets)
                            canvas.drawBitmap(missle.missle,missle.x,missle.y,null);
                    /*Draw the rockets*/
                    for (Rocket rocket : rockets) {
                        if (rocket.wasIntercepted)
                            canvas.drawBitmap(Rocket.getExplosion(), rocket.x - rocket.width, rocket.y + rocket.height / 4, paint);
                        else
                            canvas.drawBitmap(rocket.getRocket(), rocket.x, rocket.y, paint);
                    }

                }
                break;
                case BOSS: {
                    /*Draw the last monsters left*/
                    if (monsters.size() > 0)
                        for (Monster monster : monsters)
                            if (monster.wasShot)
                                canvas.drawBitmap(Monster.getExplosion(), monster.x - monster.width / 2, monster.y, paint);
                            else
                                canvas.drawBitmap(monster.getMonster(), monster.x, monster.y, paint);
                    if (boss != null) {
                        if (boss.wasKilled)
                            canvas.drawBitmap(Boss.getExplosion(), boss.x - boss.width / 2, boss.y, paint);
                        else
                            canvas.drawBitmap(boss.getBoss(), boss.x, boss.y, paint);
                    }
                    for (Bullet bullet : bossBullets)
                        if (player.wasHurt) {
                            canvas.drawBitmap(Player.getHurt(), bullet.x - bullet.width / 2, bullet.y, paint);
                            player.wasHurt = false;
                        } else
                            canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);
                }
                break;
            }
            /*Draw score and health state*/
            paint.setColor(Color.BLACK);
            paint.setTextSize(70);
            /*Draw Score*/
            if (score < 0)
                score = 0;
            canvas.drawBitmap(scoreBitmap, 50 * screenRatioX, 50 * screenRatioY, paint);
            canvas.drawText(score + "", 170 * screenRatioX, 120 * screenRatioY, paint);
            /*Draw Health*/
            canvas.drawBitmap(healthBitmap, 50 * screenRatioX, 170 * screenRatioY, paint);
            canvas.drawText(health + "", 170 * screenRatioX, 250 * screenRatioY, paint);

            /*Draw play/pause music*/
            if (playMusic)
                canvas.drawBitmap(playBitmap, 9 * screenX / 10, 50 * screenRatioY, null);
            else
                canvas.drawBitmap(pauseBitmap, 9 * screenX / 10, 50 * screenRatioY, null);

            /*Draw play/pause sound effects*/
            if (muteSoundPool)
                canvas.drawBitmap(pauseBitmap, 9 * screenX / 10, 150 * screenRatioY, paint);
            else
                canvas.drawBitmap(playBitmap, 9 * screenX / 10, 150 * screenRatioY, paint);

            /*Draw kipat barzel option if needed*/
            if(gameState==GameState.RED_COLOR&&!kipatBarzel&&score>=1100&&levelNumber>=5)
                canvas.drawBitmap(kipatBarzelBitmap,9 * screenX / 10-20*screenRatioX,250 * screenRatioY,paint);

            /*Draw red color alert*/
            if (redColorAlert)
                canvas.drawBitmap(redColorBitmap, screenX / 2 - redColorBitmap.getWidth() / 2, screenY / 2 - redColorBitmap.getHeight(), null);

            /*Draw the bullets*/
            for (Bullet bullet : bullets)
                if (boss.wasShot) {
                    canvas.drawBitmap(Player.getHurt(), boss.x, boss.y, paint);
                    boss.wasShot = false;
                } else
                    canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
        if (isGameOver) {
            isPlaying = false;
            synchronized (object) {
                object.notify();
            }
        }

        /*Ensures that every object which is not inside the screen will be removed*/
        kipatBarzelRockets.removeAll(kipatBarzelRocketsTrash);
        bossBullets.removeAll(bossBulletsTrash);
        monsters.removeAll(monstersTrash);
        rockets.removeAll(rocketsTrash);
        bullets.removeAll(bulletsTrash);
    }

    private void sleep() {
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        timer = new Timer();
        thread.start();
        if(playMusic)
            mediaPlayer.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
            if (timer != null)
                timer.cancel();
            timer = null;
            if (mediaPlayer != null)
                mediaPlayer.pause();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int amount = event.getPointerCount();
        int maskedAction = event.getActionMasked();
        float x1 = event.getX(), y1 = event.getY();
        for (int i = 0; i < amount; i++) {
            float x = event.getX(i), y = event.getY(i);
            /*Check interception of rockets with touch*/
            checkRocketInterception((int) x, (int) y);

            if (maskedAction == MotionEvent.ACTION_DOWN) {
                playerX = event.getX();
                dplayerX = playerX - player.x;
            }

            if (maskedAction == MotionEvent.ACTION_MOVE) {
                /*Sets the character's position on move*/
                if (y < ((player.playerAnim[0].getHeight() * 5 + 520) * screenRatioY))
                    player.x = event.getX(i) - dplayerX;
                /*Makes sure that the user doesn't cross the screen borders*/
                checkAndSetScreenBorders();
            }
            if ((y > player.y + player.playerAnim[0].getHeight()) && (maskedAction == MotionEvent.ACTION_UP || maskedAction == MotionEvent.ACTION_POINTER_UP)) {
                player.toShoot++;
                if(!muteSoundPool)
                    soundPool.play(player_shoot_sound, 1, 1, 0, 0, 1);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            /*Check if the player plays/pauses the music*/
            if (new Rect(9 * screenX / 10, 50 * (int) screenRatioY,
                    9 * screenX / 10 + pauseBitmap.getWidth(),
                    50 * (int) screenRatioY + pauseBitmap.getHeight()).contains((int) x1, (int) y1))
                if (playMusic) {
                    playMusic = false;
                    mediaPlayer.pause();
                } else {
                    playMusic = true;
                    mediaPlayer.start();
                }
            /*Check if the player plays/pauses the sound effects*/
            if (new Rect(9 * screenX / 10, 150 * (int) screenRatioY,
                    9 * screenX / 10 + pauseBitmap.getWidth(),
                    150 * (int) screenRatioY + pauseBitmap.getHeight()).contains((int) x1, (int) y1))
                muteSoundPool=(muteSoundPool)?false:true;

            /*Check if the player activated kipat barzel system*/
            if (new Rect(9 * screenX / 10, 250 * (int) screenRatioY,
                    9 * screenX / 10 + kipatBarzelBitmap.getWidth(),
                    250 * (int) screenRatioY + kipatBarzelBitmap.getHeight()).contains((int) x1, (int) y1))
                kipatBarzel=true;
        }
        return true;
    }

    /*Loads new bullet for player or boss*/
    public void newBullet(boolean isPlayerShooting) {
        Bullet bullet;
        if (isPlayerShooting) {
            bullet = new Bullet(getResources(), R.drawable.bullet);
            bullet.x = (int) (player.x + player.playerAnim[0].getWidth() / 2);
            bullet.y = (int) ((player.y - player.playerAnim[0].getHeight()));
            bullets.add(bullet);
        } else {
            bullet = new Bullet(getResources(), R.drawable.boss_bullet);
            bullet.x = random.nextInt(boss.width + 1) + boss.x;
            bullet.y = boss.y + boss.height;
            bossBullets.add(bullet);
        }

    }

    /*Checks and sets character's position at the screen borders*/
    private void checkAndSetScreenBorders() {
        if (player.x < 0) {
            player.x = 0;
        }
        if (screenX - player.playerAnim[0].getWidth() < player.x) {
            player.x = screenX - player.playerAnim[0].getWidth();
        }
    }

    /*Bullets' update*/
    private void updateBullets() {
        /*Bullets' Management*/
        for (final Bullet bullet : bullets) {
            if (bullet.y < 0) {
                score -= 5;
                bulletsTrash.add(bullet);
            }
            bullet.y -= 70 * screenRatioY;
            /*boss's  hit check*/
            if (boss != null) {
                if (Rect.intersects(bullet.getCollisionShape(), boss.getCollisionShape()) && (gameState == GameState.BOSS)) {
                    boss.wasShot = true;
                    boss.lifeCounter--;
                    /*Makes the boss bleeding*/
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bullet.y = -200;
                            bullet.x = -100;
                        }
                    }, 100);
                    /*Killing the boss and finishing level*/
                    if (boss.lifeCounter == 0f) {
                        score += boss.getScore();
                        boss.wasKilled = true;
                        /*Make an explosion*/
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                boss.y = -boss.height;
                                gameState = GameState.END_OF_LEVEL;
                                isGameOver = true;
                            }
                        }, 200);
                        if(!muteSoundPool)
                            soundPool.play(boss_explosion_sound, 1, 1, 0, 0, 1);
                        bullet.y = -200;
                        bullet.x = -100;

                    }
                    bulletsTrash.add(bullet);
                }
            }
            /*Monsters' bullet's hit check*/
            for (final Monster monster : monsters) {
                if (Rect.intersects(bullet.getCollisionShape(), monster.getCollisionShape())) {
                    score += (monster.getScore()+monster.extraScore);
                    monster.wasShot = true;
                    /*Make an explosion*/
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            monster.y = -monster.height;
                        }
                    }, 100);
                    if(!muteSoundPool)
                        soundPool.play(monster_explosion_sound, 1, 1, 0, 0, 1);
                    monstersTrash.add(monster);
                    bullet.y = -200;
                    bullet.x = -100;
                    bulletsTrash.add(bullet);
                }
            }
        }
    }

    /*Rockets' update*/
    private void updateRockets() {
        /*Rockets' management*/
        for (final Rocket rocket : rockets) {
            rocket.y += rocket.speed;
            /*Check if the rocket has reached to the ground*/
            if (rocket.y + rocket.height > player.playerAnim[0].getHeight() * 5 + 520 * screenRatioY) {
                score -= (rocket.getScore()+rocket.extraScore);
                if(score<0) score=0;
                rocket.wasIntercepted = true;
                if(!muteSoundPool)
                    soundPool.play(rocket_explosion_sound, 1, 1, 0, 0, 1);
                /*Make an explosion*/
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rocket.y = -rocket.height;
                    }
                }, 100);

                rocketsTrash.add(rocket);
            }
            /*Check if the rocket hits the player*/
            if (Rect.intersects(player.getCollisionShape(), rocket.getCollisionShape())) {
                health--;
                if (health == 0)
                    isGameOver = true;
                rocket.wasIntercepted = true;
                /*Make an explosion*/
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rocket.y = -rocket.height;
                    }
                }, 100);
                if(!muteSoundPool) {
                    soundPool.play(rocket_explosion_sound, 0.6f, 0.6f, 0, 0, 1);
                    soundPool.play(player_get_hurt_sound, 1, 1, 0, 0, 1);
                }
                rocketsTrash.add(rocket);
            }
        }
    }

    /*Check rocket's Interception*/
    private void checkRocketInterception(int x, int y) {
        for (final Rocket rocket : rockets) {
            /*Check if the rocket has intercepted*/
            if (rocket.getCollisionShape().contains(x, y)) {
                score += (rocket.getScore()+rocket.extraScore);
                rocket.wasIntercepted = true;
                /*Make an explosion*/
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rocket.y = -rocket.height;
                        if(!muteSoundPool)
                            soundPool.play(rocket_explosion_sound, 1, 1, 0, 0, 1);
                    }
                }, 100);
                rocketsTrash.add(rocket);
            }
        }
    }

    /*Monsters' update*/
    private void updateMonsters() {
        /*Monsters' management*/
        for (final Monster monster : monsters) {
            monster.y += monster.speed;
            /*Check if the monster has reached to the ground*/
            if (monster.y + monster.height > player.playerAnim[0].getHeight() * 5 + 520 * screenRatioY) {
                score -= (monster.getScore()+monster.extraScore);
                if(score<0) score=0;
                monster.wasShot = true;
                /*Make an explosion*/
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        monster.y = -monster.height;
                    }
                }, 100);
                if(!muteSoundPool)
                    soundPool.play(monster_explosion_sound, 1, 1, 0, 0, 1);
                monstersTrash.add(monster);
            }
            /*Check if the monster hits the player*/
            if (Rect.intersects(monster.getCollisionShape(), player.getCollisionShape())) {
                health--;
                if (health == 0)
                    isGameOver = true;
                monster.wasShot = true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        monster.y = -monster.height;
                    }
                }, 100);
                if(!muteSoundPool) {
                    soundPool.play(monster_explosion_sound, 0.6f, 0.6f, 0, 0, 1);
                    soundPool.play(player_get_hurt_sound, 1, 1, 0, 0, 1);
                }
                monstersTrash.add(monster);
            }
        }
    }

    /*Boss's update*/
    private void updateBossMovement() {
        /*boss moves down until it gets to good enough height*/
        if (boss.y < boss.height / 2)
            boss.y += 5 * screenRatioY;
        /*Movement left and right*/
        //Move left
        if (boss.moveLeft) {
            boss.x -= 10 * screenRatioX;
            if (boss.x < 0) {
                boss.x = 0;
                boss.moveLeft = false;
            }
        }
        //Move right
        else {
            boss.x += 10 * screenRatioX;
            if (screenX - ((levelNumber == 4 || levelNumber == 7) ? boss.width * 2 : boss.width) < boss.x) {
                boss.x = screenX - ((levelNumber == 4 || levelNumber == 7) ? boss.width * 2 : boss.width);
                boss.moveLeft = true;
            }
        }

    }

    /*Boss's bullets' update*/
    private void updateBossBullets() {
        if (bossShootingTime) {
            if(!muteSoundPool)
                soundPool.play(boss_shoot_sound, 1, 1, 0, 0, 1);
            boss.toShoot++;
            bossShootingTime = false;
        }
        for (final Bullet bullet : bossBullets) {
            bullet.y += 40 * screenRatioY;
            /*Check if the bullet has reached to the ground*/
            if (bullet.y + bullet.height > (player.playerAnim[0].getHeight() * 5 + 520) * screenRatioY) {
                bullet.y = -200;
                bullet.x = -100;
                bossBulletsTrash.add(bullet);
            }
            /*Check if the bullet hits the player*/
            if (Rect.intersects(player.getCollisionShape(), bullet.getCollisionShape())) {
                player.wasHurt = true;
                health--;
                /*Makes the player bleeding*/
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bullet.y = -200 ;
                        bullet.x = -100;
                    }
                }, 100);
                if(!muteSoundPool)
                    soundPool.play(player_get_hurt_sound, 1, 1, 0, 0, 1);
                if (health == 0) {
                    isGameOver = true;
                }

                bossBulletsTrash.add(bullet);
            }
        }
    }

    /*Kipat Barzel Rockets' update*/
    private void updateKipatBarzelRockets(){
        for(Missle missle: kipatBarzelRockets){
            missle.y-=missle.speed;
            /*Check if the missle misses target and cross the screen border*/
            if(missle.y<0){
                kipatBarzelRocketsTrash.add(missle);
            }
            /*Check if the missle hits a rocket*/
            for(final Rocket rocket:rockets){
                if(Rect.intersects(rocket.getCollisionShape(),missle.getCollisionShape())){
                    score += (rocket.getScore()+rocket.extraScore);
                    rocket.wasIntercepted = true;
                    /*Make an explosion*/
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rocket.y = -rocket.height;
                            if(!muteSoundPool)
                                soundPool.play(rocket_explosion_sound, 1, 1, 0, 0, 1);
                        }
                    }, 100);
                    missle.y=screenY+missle.height;
                    rocketsTrash.add(rocket);
                    kipatBarzelRocketsTrash.add(missle);
                }
            }
        }
    }

    /*Game state management:
     * NORMAL - regular monsters fall from the sky
     * RED_COLOR - rockets fall from the sky
     * BOSS - boss time and end of the level
     * ENF_OF_LEVEL - when killing the boss and complete the level*/
    public enum GameState {
        NORMAL,
        RED_COLOR,
        BOSS,
        END_OF_LEVEL
    }

    /*Creates the varied monsters and rockets by the progress of levels */
    private int enemyCreationIndex() {
        if (levelNumber == 1)
            return 1;
        else if (levelNumber == 2 || levelNumber == 3)
            return 2;
        else if (levelNumber == 4 || levelNumber == 5)
            return 3;
        else if (levelNumber == 6 || levelNumber == 7)
            return 4;
        else
            return 5;
    }

    private int rocketCreationIndex() {
        if (levelNumber == 1)
            return 1;
        else if (levelNumber == 2 || levelNumber == 3)
            return 2;
        else if (levelNumber == 4 || levelNumber == 5)
            return 3;
        else
            return 4;
    }

    /*Add extra speed randomly for each enemy*/
    private int extraSpeed() {
        return random.nextInt(5)+2;
    }

    public int getScore() {
        return score;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public GameState getGameState() {
        return gameState;
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }

    public int getGame_over_sound() {
        return game_over_sound;
    }

    public int getHigh_score_sound() {
        return high_score_sound;
    }

    public int getLevel_up_sound() {
        return level_up_sound;
    }

    public boolean isMuteSoundPool() {
        return muteSoundPool;
    }

    public void stopMediaPlayerAndSoundPool() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        soundPool.release();
        soundPool = null;
    }

    public void stopTimer() {
        timer.cancel();
        timer = null;
    }
}

