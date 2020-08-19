package com.example.testgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.testgame.GameView.screenRatioX;
import static com.example.testgame.GameView.screenRatioY;

public class Bullet {
    Bitmap bullet;
    int width, height, x, y;

    Bullet(Resources res, int resId) {
        bullet = BitmapFactory.decodeResource(res, resId);

        width = (bullet.getWidth() / 4);
        height = (bullet.getHeight() / 4);

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);

    }

    Rect getCollisionShape() {
        return new Rect(x, y, (x + width), (y + height));
    }

}
