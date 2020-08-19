package com.example.testgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.DialogPreference;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private GameView gameView;
    private SharedPreferences spHighScore, spLevels;
    private String nameScore = "";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        spHighScore = getSharedPreferences("high score", MODE_PRIVATE);
        spLevels = getSharedPreferences("open levels", MODE_PRIVATE);


        int bgResId = getIntent().getIntExtra("bgResId", R.drawable.tel_aviv_beach);
        float enemyCreationTime = getIntent().getFloatExtra("enemyCreationTime", 1f);
        int enemySpeed = getIntent().getIntExtra("enemySpeed", 15);
        int monsterScore = getIntent().getIntExtra("monsterScore", 10);
        int rocketScore = getIntent().getIntExtra("rocketScore", 15);
        int bossScore = getIntent().getIntExtra("bossScore", 70);
        int redColorDuration = getIntent().getIntExtra("redColorDuration", 20);
        int bossLifeCounter = getIntent().getIntExtra("bossLifeCounter", 100);
        int level = getIntent().getIntExtra("level", 1);

        /*First time playing case*/
        if (spLevels.getBoolean("first time playing", false)) {
            startActivity(new Intent(GameActivity.this, TutorialActivity.class));
        }

        /*Back thread to schedule events in the end of the game*/
        final Object object=new Object();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.stopTimer();
                        gameOver();
                    }
                });
            }
        });
        gameView = new GameView(object,this, point.x, point.y,
                bgResId, enemyCreationTime, enemySpeed, monsterScore,
                rocketScore, bossScore, redColorDuration, bossLifeCounter, level);

        setContentView(gameView);
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    public void onBackPressed() {
        gameView.pause();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.exit_level_title))
                .setMessage(getResources().getString(R.string.exit_level_msg) +" "+gameView.getLevelNumber() + "?")
                .setPositiveButton(getResources().getString(R.string.exit_level_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(GameActivity.this, LevelActivity.class));
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.exit_level_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameView.resume();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void gameOver() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(GameActivity.this);

        View dialogView;
        /*Level up case*/
        if (gameView.getGameState() == GameView.GameState.END_OF_LEVEL) {
            if(!gameView.isMuteSoundPool())
                gameView.getSoundPool().play(gameView.getLevel_up_sound(), 1, 1, 0, 0, 1);
            dialogView = getLayoutInflater().inflate(R.layout.dialog_level_up, null);
            final TextView levelTv = dialogView.findViewById(R.id.level_tv),
                    coinsTv = dialogView.findViewById(R.id.coins_tv);
            final Button backToActivityBtn = dialogView.findViewById(R.id.go_btn);
            levelTv.setText(levelTv.getText().toString() + " " + (gameView.getLevelNumber() + 1));
            coinsTv.setText(coinsTv.getText().toString() + " " + gameView.getScore());
            backToActivityBtn.setOnClickListener(this);

            /*Open the next level*/
            if (gameView.getLevelNumber() < 8) {
                SharedPreferences.Editor editor = spLevels.edit();
                if (!spLevels.getBoolean("has played already", false))
                    editor.putBoolean("has played already", true);
                editor.putInt("level" + (gameView.getLevelNumber() + 1), 1);
                editor.commit();
            }
        }
        /*Game over case*/
        else {
            if(!gameView.isMuteSoundPool())
                gameView.getSoundPool().play(gameView.getGame_over_sound(), 1, 1, 0, 0, 1);
            dialogView = getLayoutInflater().inflate(R.layout.dialog_game_over, null);
            final TextView /*levelTv = dialogView.findViewById(R.id.level_tv),*/
                    coinsTv = dialogView.findViewById(R.id.coins_tv);
            final Button backToActivityBtn = dialogView.findViewById(R.id.back_btn);
            /* levelTv.setText(levelTv.getText().toString() + " " + gameView.getLevelNumber());*/
            coinsTv.setText(coinsTv.getText().toString() + " " + gameView.getScore());

            if (!spLevels.getBoolean("has played already", false)) {
                SharedPreferences.Editor editor = spLevels.edit();
                editor.putBoolean("has played already", true);
                editor.apply();
            }
            backToActivityBtn.setOnClickListener(this);
        }

        builder.setView(dialogView).setCancelable(false);
        final android.app.AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setLayout(500, 280);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        boolean highScoreFlag = false;
        /*Updates high score table*/
        for (int i = 0; i < 5; i++) {
            if (gameView.getScore() >= spHighScore.getInt((i + 1) + "", 0)) {
                /*Handle new high score case*/
                if (i == 0) {
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(GameActivity.this);

                    highScoreFlag = true;
                    if(!gameView.isMuteSoundPool())
                        gameView.getSoundPool().play(gameView.getHigh_score_sound(), 1, 1, 0, 0, 1);
                    // AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    final View dialogView;
                    dialogView = getLayoutInflater().inflate(R.layout.dialog_new_highscore, null);
                    final EditText nameEt = dialogView.findViewById(R.id.name_et);
                    int maxLength = 10;
                    nameEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                    final TextView scoreTv = dialogView.findViewById(R.id.coins_tv);
                    scoreTv.setText(gameView.getScore() + "");
                    final Button goBtn = dialogView.findViewById(R.id.go_btn);
                    goBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nameScore = nameEt.getText().toString();
                            changeHighScoreTable(0);
                            exit();
                        }
                    });

                    builder.setView(dialogView).setCancelable(false);
                    final android.app.AlertDialog dialog = builder.create();
                    Window window = dialog.getWindow();
                    window.setLayout(500, 280);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
                /*Score is not higher than first place case*/
                if (!highScoreFlag) {
                    changeHighScoreTable(i);
                    break;
                }
            }
        }
        /*Waiting before returning to the levels activity*/
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*Score is not higher than first place case*/
        if (!highScoreFlag)
            exit();
    }

    /*Change the high score table by the last score if needed*/
    public void changeHighScoreTable(int j) {
        SharedPreferences.Editor editor = spHighScore.edit();

        int tmp1 = spHighScore.getInt((j + 1) + "", 0);
        String str1 = spHighScore.getString("name" + (j + 1), "");
        editor.putInt((j + 1) + "", gameView.getScore());
        editor.putString("name" + (j + 1), nameScore);
        for (int i = j + 1; i < 5; i++) {
            int tmp2 = spHighScore.getInt((i + 1) + "", 0);
            String str2 = spHighScore.getString("name" + (i + 1), "");
            editor.putInt((i + 1) + "", tmp1);
            editor.putString("name" + (i + 1), str1);
            tmp1 = tmp2;
            str1 = str2;
        }
        editor.commit();
    }

    private void exit() {
        gameView.stopMediaPlayerAndSoundPool();
        Intent intent = new Intent(GameActivity.this, LevelActivity.class);
        startActivity(intent);
        finish();
    }
}
