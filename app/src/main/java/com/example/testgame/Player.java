package com.example.testgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import static com.example.testgame.GameView.screenRatioX;
import static com.example.testgame.GameView.screenRatioY;


public class Player {
    public int toShoot = 0, playerCounter = 0;
    public boolean wasHurt = false;
    int width, height;
    private Bitmap player1;
    private static Bitmap hurt;
    public Bitmap[] playerAnim;
    private GameView gameView;
    float x, y;
    private boolean isFirst = true;

    Player(GameView gameView, int screenY, Resources res) {

        this.gameView = gameView;

        player1 = BitmapFactory.decodeResource(res, R.drawable.player_anim);

        width = player1.getWidth() / 10;
        height = player1.getHeight();

        playerAnim = new Bitmap[10];
        for (int i = 0; i < 10; i++) {
            playerAnim[i] = Bitmap.createBitmap(player1, width * i, 0, width, height);
            playerAnim[i] = Bitmap.createScaledBitmap(playerAnim[i], 3 * width / 4, 3 * height / 4, false);
        }

        hurt = BitmapFactory.decodeResource(res, R.drawable.player_hurt);
        int height1 = hurt.getHeight(), width1 = hurt.getWidth();
        hurt = Bitmap.createBitmap(hurt, 0, 0, width1, height1);
        hurt = Bitmap.createScaledBitmap(hurt, width1 / 8, height1 / 8, false);


        y = screenY - 505 * screenRatioY;
        x = screenRatioX;

    }

    Bitmap getPlayer() {
        if (toShoot != 0) {
            toShoot--;
            gameView.newBullet(true);
        }
        if (isFirst) {
            isFirst = false;
            return playerAnim[(playerCounter++) % 10];
        } else {
            isFirst = true;
            return playerAnim[(playerCounter) % 10];
        }

    }

    public static Bitmap getHurt() {
        return hurt;
    }


    public Rect getCollisionShape() {
        return new Rect((int) x, (int) y, (int) x + width, (int) y + height);
    }


}
