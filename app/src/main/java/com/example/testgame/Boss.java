package com.example.testgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import java.util.HashMap;

import static com.example.testgame.GameView.bossScore;
import static com.example.testgame.GameView.screenRatioX;
import static com.example.testgame.GameView.screenRatioY;

public class Boss {

    public boolean wasKilled = false, moveLeft = true, isFirst = true,wasShot=false;
    public  boolean bossCreation = true;
    public int x = 0, y, width, height, toShoot = 0;
    public float lifeCounter;
    private int bossIndexNumber, bossCounter = 0,score = bossScore;
    private GameView gameView;
    private static Bitmap[] bossAnim;
    public static Bitmap explosion;
    /*Organizing the bosses by key as index number and value as bitmap array list for animation*/
    private final static HashMap<Integer, Bitmap[]> bosses = new HashMap<>();

    public Boss(Resources res, float lifeCounter, int indexNumber, GameView gameView) {
        /*Creating the bosses only once*/
        if (bossCreation) {
            for (int i = 0; i < 8; i++) {
                Bitmap boss;
                if (indexNumber == 0)
                    boss = BitmapFactory.decodeResource(res, R.drawable.boss1_anim);
                else if (i == 1)
                    boss = BitmapFactory.decodeResource(res, R.drawable.boss2_anim);
                else if (i == 2)
                    boss = BitmapFactory.decodeResource(res, R.drawable.boss3_anim);
                else if (i == 3)
                    boss = BitmapFactory.decodeResource(res, R.drawable.boss4_anim);
                else if (i == 4)
                    boss = BitmapFactory.decodeResource(res, R.drawable.boss5_anim);
                else if (i == 5)
                    boss = BitmapFactory.decodeResource(res, R.drawable.boss6_anim);
                else if (i == 6)
                    boss = BitmapFactory.decodeResource(res, R.drawable.boss7_anim);
                else
                    boss = BitmapFactory.decodeResource(res, R.drawable.boss8_anim);

                width = boss.getWidth() / 6;
                height = boss.getHeight() / 4;
                bossAnim = new Bitmap[24];
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < 6; k++) {
                        bossAnim[j * 6 + k] = Bitmap.createBitmap(boss, k * width, j + height, width, height);
                        bossAnim[j * 6 + k] = Bitmap.createScaledBitmap(bossAnim[j * 6 + k], width , height, false);
                    }
                }
                bosses.put(i, bossAnim);
                if (i != 7)
                    bossAnim = null;
            }

            explosion = BitmapFactory.decodeResource(res, R.drawable.boss_explosion1);
            width = explosion.getWidth();
            height = explosion.getHeight();
            explosion = Bitmap.createBitmap(explosion, 0, 0, width, height);
            explosion = Bitmap.createScaledBitmap(explosion, width / 2, height / 2, false);
            width = bossAnim[0].getWidth() * (int) screenRatioX;
            height = bossAnim[0].getHeight() * (int) screenRatioY;
            bossCreation = false;
        }

        this.gameView = gameView;
        width = bossAnim[0].getWidth() * (int) screenRatioX;
        height = bossAnim[0].getHeight() * (int) screenRatioY;
        this.lifeCounter = lifeCounter;
        this.bossIndexNumber = indexNumber;

        y = -height;
    }

    public Bitmap getBoss() {
        /*Handles boss shootings*/
        if (toShoot != 0) {
            toShoot--;
            gameView.newBullet(false);
        }
        if (isFirst) {
            isFirst = false;
            return bosses.get(bossIndexNumber)[(bossCounter++) % 24];
        } else {
            isFirst = true;
            return bosses.get(bossIndexNumber)[(bossCounter) % 24];
        }
    }

    public Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }

    public static Bitmap getExplosion() {
        return explosion;
    }

    public int getScore() {
        return score;
    }
}
