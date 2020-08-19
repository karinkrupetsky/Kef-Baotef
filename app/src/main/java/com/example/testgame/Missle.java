package com.example.testgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Missle {
    public int x, y, width, height, speed;
    public Bitmap missle;

    public Missle(Resources res,int x,int speed){
        this.x=x;
        this.speed=speed;
        missle= BitmapFactory.decodeResource(res, R.drawable.missle);
        width = (missle.getWidth()/2)*(int)GameView.screenRatioX;
        height = (missle.getHeight()/2)*(int)GameView.screenRatioY;
        missle = Bitmap.createScaledBitmap(missle, width/8, height/8, false);
    }

    Rect getCollisionShape() {
        return new Rect(x, y, (x + width), (y + height));
    }
}
