package com.example.testgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private boolean mute;
    private MediaPlayer mediaPlayer;
    SharedPreferences spHighScore, spLevels,spSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        /* init Highscore and level menu */
        spHighScore = getSharedPreferences("high score", MODE_PRIVATE);
        spLevels = getSharedPreferences("open levels", MODE_PRIVATE);
        spSound = getSharedPreferences("sound", MODE_PRIVATE);
        mute=spSound.getBoolean("mute",false);
        final SharedPreferences.Editor soundEditor = spSound.edit();


        /*Init data for the first time playing*/
        if (!spLevels.getBoolean("has played already", false)) {
            SharedPreferences.Editor editorlevels = spLevels.edit();
            editorlevels.putBoolean("first time playing", true);
            editorlevels.commit();

            SharedPreferences.Editor editor = spHighScore.edit();
            editor.putInt("1", 0);
            editor.putString("name1", getResources().getString(R.string.player) + " 1");
            editor.putInt("2", 0);
            editor.putString("name2", getResources().getString(R.string.player) + " 2");
            editor.putInt("3", 0);
            editor.putString("name3", getResources().getString(R.string.player) + " 3");
            editor.putInt("4", 0);
            editor.putString("name4", getResources().getString(R.string.player) + " 4");
            editor.putInt("5", 0);
            editor.putString("name5", getResources().getString(R.string.player) + " 5");
            editor.apply();
        }

        SharedPreferences.Editor editor = spHighScore.edit();
        /*Handles initial default high score records language*/
        for (int j = 1; j <= 5; j++) {
            if ((Locale.getDefault().getLanguage().equals("en")
                    && spHighScore.getString("name" + j, "").contains("שחקן"))
                    || (!Locale.getDefault().getLanguage().equals("en")
                    && (spHighScore.getString("name" + j, "").contains("Player")
                    || spHighScore.getString("name" + j, "").equals("")))) {
                editor.putString("name" + j, getResources().getString(R.string.player) + " " + j);
            }
        }
        editor.commit();

        // Play button
        Button playBtn = findViewById(R.id.play_btn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                Intent intent = new Intent(MainActivity.this, LevelActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //High score Button
        Button highscoreBtn = findViewById(R.id.highscore_btn);
        highscoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HighScore.class));
                finish();
            }
        });

        // Volume control
        final ImageView volumeIv = findViewById(R.id.volume_iv);
        volumeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mute) {
                    mute = true;
                    mediaPlayer.pause();
                    soundEditor.putBoolean("mute", true);
                    volumeIv.setImageResource(R.drawable.ic_volume_off_black_24dp);
                } else { // when muted
                    mute = false;
                    mediaPlayer.start();
                    soundEditor.putBoolean("mute", false);
                    volumeIv.setImageResource(R.drawable.ic_volume_up_black_24dp);
                }
                soundEditor.commit();
            }
        });

        /* Plays the animation */
        ImageView animation = findViewById(R.id.animation_iv);
        AnimationDrawable animationDrawable = (AnimationDrawable) animation.getDrawable();
        animationDrawable.start();

        mediaPlayer = MediaPlayer.create(this, R.raw.menus_music);
        mediaPlayer.setLooping(true);
        /*Plays the level's music in loops*/
        if (!spSound.getBoolean("mute", false))
            mediaPlayer.start();
        else
            volumeIv.setImageResource(R.drawable.ic_volume_off_black_24dp);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this).setTitle(getResources().getString(R.string.exit_game_title))
                .setMessage(getResources().getString(R.string.exit_game_msg)+"\ud83d\ude1e").setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(getResources().getString(R.string.exit_game_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.pause();
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.exit_game_no), null)
                .setIcon(R.drawable.ic_error_black_24dp)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!spSound.getBoolean("mute", false))
            mediaPlayer.start();
    }
}

