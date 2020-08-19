package com.example.testgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.example.testgame.GameView.monsterScore;
import static com.example.testgame.GameView.screenRatioX;
import static com.example.testgame.GameView.screenRatioY;

public class Monster {
    public boolean wasShot = false, isFirst = true;
    /*Ensures Monsters' creation only once*/
    private static boolean monsterCreation = true;
    public int x, y, width, height, speed,extraScore;
    private int monsterIndexNumber, monsterCounter = 1,score = monsterScore;
    private static Bitmap[] monsterAnim;
    private static Bitmap explosion;
    private Resources res;
    /*Organizing the monsters by key as index number and value as bitmap array list for animation*/
    private final static HashMap<Integer, Bitmap[]> monsters = new HashMap<>();

    public Monster(Resources res, int speed, int indexNumber) {
        /*Creating the monsters only once*/
        if (monsterCreation) {
            for (int i = 0; i < 5; i++) {
                Bitmap monster;
                if (i == 0)
                    monster = BitmapFactory.decodeResource(res, R.drawable.monster1);
                else if (i == 1)
                    monster = BitmapFactory.decodeResource(res, R.drawable.monster2);
                else if (i == 2)
                    monster = BitmapFactory.decodeResource(res, R.drawable.monster3);
                else if (i == 3)
                    monster = BitmapFactory.decodeResource(res, R.drawable.monster4);
                else
                    monster = BitmapFactory.decodeResource(res, R.drawable.monster5);

                width = monster.getWidth() / 8;
                height = monster.getHeight() / 3;
                monsterAnim = new Bitmap[24];
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 8; k++) {
                        monsterAnim[j * 8 + k] = Bitmap.createBitmap(monster, k * width, j + height, width, height);
                        monsterAnim[j * 8 + k] = Bitmap.createScaledBitmap(monsterAnim[j * 8 + k], 2*width/3 , 2*height/3 , false);
                    }
                }
                monsters.put(i, monsterAnim);
                if (i != 4)
                    monsterAnim = null;
            }
            explosion = BitmapFactory.decodeResource(res, R.drawable.explosion2);
            width = explosion.getWidth();
            height = explosion.getHeight();
            explosion = Bitmap.createBitmap(explosion, 0, 0, width, height);
            explosion = Bitmap.createScaledBitmap(explosion, width / 5, height / 5, false);
            monsterCreation = false;
        }
        width = monsterAnim[0].getWidth() * (int) screenRatioX;
        height = monsterAnim[0].getHeight() * (int) screenRatioY;
        this.res = res;
        this.speed = speed;
        this.monsterIndexNumber = indexNumber;
        /*Sets extra custom score for each monster*/
        switch (indexNumber){
            case 0:{
                extraScore=3;
            }
            break;
            case 1:{
                extraScore=5;
            }
            break;
            case 2:{
                extraScore=7;
            }
            break;
            case 3:{
                extraScore=9;
            }
            break;
            case 4:{
                extraScore=11;
            }
            break;
        }

        y = -height;

    }

    public Bitmap getMonster() {
        if (isFirst) {
            isFirst = false;
            return monsters.get(monsterIndexNumber)[(monsterCounter++) % 24];
        } else {
            isFirst = true;
            return monsters.get(monsterIndexNumber)[(monsterCounter) % 24];
        }
    }

    public Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }

    public  int getScore() {
        return score;
    }

    public static Bitmap getExplosion() {
        return explosion;
    }
}

