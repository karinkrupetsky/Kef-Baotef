package com.example.testgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import java.util.HashMap;
import static com.example.testgame.GameView.rocketScore;
import static com.example.testgame.GameView.screenRatioX;
import static com.example.testgame.GameView.screenRatioY;

public class Rocket {

    public boolean wasIntercepted = false;
    public int x = 0, y, width, height, speed,extraScore;
    private int rocketIndexNumber, rocketCounter = 0,score = rocketScore;
    private static boolean rocketCreation = true;
    private static Bitmap[] rocketAnim;
    private static Bitmap explosion;
    private Resources res;

    /*Organizing the rockets by key as index number and value as bitmap array list for animation*/
    private final static HashMap<Integer, Bitmap[]> rockets = new HashMap<>();

    public Rocket(Resources res, int speed, int indexNumber) {
        /*Creating the rockets only once*/
        if (rocketCreation) {
            for (int i = 0; i < 4; i++) {
                Bitmap rocket;
                if (i == 0)
                    rocket = BitmapFactory.decodeResource(res, R.drawable.rocket_red_anim12);
                else if (i == 1)
                    rocket = BitmapFactory.decodeResource(res, R.drawable.rocket_purple_anim34);
                else if (i == 2)
                    rocket = BitmapFactory.decodeResource(res, R.drawable.rocket_green_anim56);
                else
                    rocket = BitmapFactory.decodeResource(res, R.drawable.rocket_blue_anim78);

                width = rocket.getWidth() / 6;
                height = rocket.getHeight();
                rocketAnim = new Bitmap[6];
                for (int j = 0; j < 6; j++) {
                    rocketAnim[j] = Bitmap.createBitmap(rocket, j * width, 0, width, height);
                    rocketAnim[j] = Bitmap.createScaledBitmap(rocketAnim[j], width / 2, height / 2, false);
                }
                rockets.put(i, rocketAnim);
                if (i != 3)
                    rocketAnim = null;
            }
            explosion = BitmapFactory.decodeResource(res, R.drawable.explosion);
            width = explosion.getWidth();
            height = explosion.getHeight();
            explosion = Bitmap.createBitmap(explosion, 0, 0, width, height);
            explosion = Bitmap.createScaledBitmap(explosion, width / 7, height / 7, false);
            width = rocketAnim[0].getWidth() * (int) screenRatioX;
            height = rocketAnim[0].getHeight() * (int) screenRatioY;
            this.res = res;
            rocketCreation = false;
        }


        width = rocketAnim[0].getWidth() * (int) screenRatioX;
        height = rocketAnim[0].getHeight() * (int) screenRatioY;
        this.res = res;
        this.speed = speed;
        this.rocketIndexNumber = indexNumber;
        /*Sets extra custom score for each rocket*/
        switch (indexNumber){
            case 0:{
                extraScore=2;
            }
            break;
            case 1:{
                extraScore=4;
            }
            break;
            case 2:{
                extraScore=6;
            }
            break;
            case 3:{
                extraScore=8;
            }
            break;
        }

        y = -height;
    }

    public Bitmap getRocket() {
        return rockets.get(rocketIndexNumber)[(rocketCounter++) % 6];
    }

    public Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }

    public int getScore() {
        return score;
    }


    public static Bitmap getExplosion() {
        return explosion;
    }
}
