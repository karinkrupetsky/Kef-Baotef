package com.example.testgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class LevelActivity extends AppCompatActivity {

    private final int[] levels = new int[8];
    private final TextView[] tvLevelsArr = new TextView[8];
    SharedPreferences preferences,spSound;
    private MediaPlayer mediaPlayer;
    private boolean mute;

//TODO:fix the volume option here

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.levels_activity);

        preferences = getSharedPreferences("open levels", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        spSound=getSharedPreferences("sound",MODE_PRIVATE);
        mute=spSound.getBoolean("mute",false);
        final SharedPreferences.Editor soundEditor=spSound.edit();

        for (int i = 0; i < 8; i++) {
            /*Unlocks the first level and locks the rest for the first time played*/
            if (!preferences.getBoolean("has played already", false)) {
                editor.putInt("level" + (i + 1), (i == 0) ? 1 : 0);
                editor.commit();
            }
            levels[i] = preferences.getInt("level" + (i + 1), 0);
        }

        // Back Button
        TextView backBtn = findViewById(R.id.backBtn_tv);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvLevelsArr[0] = findViewById(R.id.ivLevel1);
        tvLevelsArr[1] = findViewById(R.id.ivLevel2);
        tvLevelsArr[2] = findViewById(R.id.ivLevel3);
        tvLevelsArr[3] = findViewById(R.id.ivLevel4);
        tvLevelsArr[4] = findViewById(R.id.ivLevel5);
        tvLevelsArr[5] = findViewById(R.id.ivLevel6);
        tvLevelsArr[6] = findViewById(R.id.ivLevel7);
        tvLevelsArr[7] = findViewById(R.id.ivLevel8);


        /*Unlocks the levels that the player passed already*/
        for (int i = 0; i < levels.length; i++) {
            if (levels[i] != 0) {
                tvLevelsArr[i].setText(Integer.toString(i + 1));
                tvLevelsArr[i].setClickable(true);

            } else {
                tvLevelsArr[i].setBackgroundResource(R.drawable.locked_level);
                tvLevelsArr[i].setText("");
                tvLevelsArr[i].setClickable(false);
            }
        }



        // Volume control
        final ImageView volumeIv = findViewById(R.id.volume_iv_levels);
        volumeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mute) {
                    mute = true;
                    mediaPlayer.pause();
                    soundEditor.putBoolean("mute",true);
                    volumeIv.setImageResource(R.drawable.ic_volume_off_black_24dp);
                } else { // when muted
                    mute = false;
                    mediaPlayer.start();
                    soundEditor.putBoolean("mute",false);
                    volumeIv.setImageResource(R.drawable.ic_volume_up_black_24dp);
                }
                soundEditor.commit();
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.menus_music);
        mediaPlayer.setLooping(true);
        /*Plays the level's music in loops*/
        if(!spSound.getBoolean("mute",false))
            mediaPlayer.start();
        else
            volumeIv.setImageResource(R.drawable.ic_volume_off_black_24dp);
    }



    public void onClick(final View view) {
        final Intent intent = new Intent(LevelActivity.this, GameActivity.class);
        switch (view.getId()) {
            case R.id.ivLevel1: // Level 1
                intent.putExtra("bgResId", R.drawable.tel_aviv_beach);
                intent.putExtra("enemyCreationTime", 1.3f);
                intent.putExtra("enemySpeed", 15);
                intent.putExtra("monsterScore", 10);
                intent.putExtra("rocketScore", 15);
                intent.putExtra("bossScore", 70);
                intent.putExtra("redColorDuration", 5);
                intent.putExtra("bossLifeCounter", 50);
                intent.putExtra("level", 1);
                break;

            case R.id.ivLevel2: // Level 2
                intent.putExtra("bgResId", R.drawable.rishon_bg);
                intent.putExtra("enemyCreationTime", 1.2f);
                intent.putExtra("enemySpeed", 16);
                intent.putExtra("monsterScore", 15);
                intent.putExtra("rocketScore", 20);
                intent.putExtra("bossScore", 90);
                intent.putExtra("redColorDuration", 15);
                intent.putExtra("bossLifeCounter", 60);
                intent.putExtra("level", 2);
                break;

            case R.id.ivLevel3: // Level 3
                intent.putExtra("bgResId", R.drawable.jerusalem_bg);
                intent.putExtra("enemyCreationTime", 1.1f);
                intent.putExtra("enemySpeed", 17);
                intent.putExtra("monsterScore", 20);
                intent.putExtra("rocketScore", 25);
                intent.putExtra("bossScore", 110);
                intent.putExtra("redColorDuration", 20);
                intent.putExtra("bossLifeCounter", 70);
                intent.putExtra("level", 3);
                break;

            case R.id.ivLevel4: // Level 4
                intent.putExtra("bgResId", R.drawable.yavne_bg);
                intent.putExtra("enemyCreationTime", 1f);
                intent.putExtra("enemySpeed", 18);
                intent.putExtra("monsterScore", 25);
                intent.putExtra("rocketScore", 30);
                intent.putExtra("bossScore", 130);
                intent.putExtra("redColorDuration", 25);
                intent.putExtra("bossLifeCounter", 80);
                intent.putExtra("level", 4);
                break;

            case R.id.ivLevel5: // Level 5
                intent.putExtra("bgResId", R.drawable.ashdod_bg);
                intent.putExtra("enemyCreationTime", 0.9f);
                intent.putExtra("enemySpeed", 19);
                intent.putExtra("monsterScore", 30);
                intent.putExtra("rocketScore", 35);
                intent.putExtra("bossScore", 150);
                intent.putExtra("redColorDuration", 30);
                intent.putExtra("bossLifeCounter", 85);
                intent.putExtra("level", 5);
                break;

            case R.id.ivLevel6: // Level 6
                intent.putExtra("bgResId", R.drawable.ashkelon_bg);
                intent.putExtra("enemyCreationTime", 0.8f);
                intent.putExtra("enemySpeed", 20);
                intent.putExtra("monsterScore", 35);
                intent.putExtra("rocketScore", 40);
                intent.putExtra("bossScore", 170);
                intent.putExtra("redColorDuration", 35);
                intent.putExtra("bossLifeCounter", 90);
                intent.putExtra("level", 6);
                break;

            case R.id.ivLevel7: // Level 7
                intent.putExtra("bgResId", R.drawable.sderot_bg);
                intent.putExtra("enemyCreationTime", 0.7f);
                intent.putExtra("enemySpeed", 21);
                intent.putExtra("monsterScore", 40);
                intent.putExtra("rocketScore", 45);
                intent.putExtra("bossScore", 200);
                intent.putExtra("redColorDuration", 40);
                intent.putExtra("bossLifeCounter", 95);
                intent.putExtra("level", 7);
                break;

            case R.id.ivLevel8: // Level 8
                intent.putExtra("bgResId", R.drawable.otef_bg);
                intent.putExtra("enemyCreationTime", 0.5f);
                intent.putExtra("enemySpeed", 24);
                intent.putExtra("monsterScore", 50);
                intent.putExtra("rocketScore", 70);
                intent.putExtra("bossScore", 250);
                intent.putExtra("redColorDuration", 50);
                intent.putExtra("bossLifeCounter", 100);
                intent.putExtra("level", 8);
                break;
        }

        // Object animation
        animateLevel(view);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.pause();
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    private void animateLevel(View view) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotationY", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(0);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!spSound.getBoolean("mute",false))
            mediaPlayer.start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LevelActivity.this , MainActivity.class);
        startActivity(intent);
        finish();
    }

}

