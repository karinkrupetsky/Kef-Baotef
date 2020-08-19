package com.example.testgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HighScore extends AppCompatActivity {
    SharedPreferences spHighScore,spSound;
    MediaPlayer mediaPlayer;
    private boolean mute;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        spHighScore = getSharedPreferences("high score", MODE_PRIVATE);
        spSound=getSharedPreferences("sound",MODE_PRIVATE);
        mute=spSound.getBoolean("mute",false);
        final SharedPreferences.Editor soundEditor=spSound.edit();

        ListView listView = findViewById(R.id.lv_highscore);

        List<Map<String, Object>> dataHighScore = new ArrayList<>();
        HashMap<String, Object> highScoreCell;

        /*Updates the score table from the shared preferences*/
        for (int i = 0; i < 5; i++) {
            highScoreCell = new HashMap<>();
            highScoreCell.put("name", spHighScore.getString("name" + (i + 1), ""));
            highScoreCell.put("score image", R.drawable.score);
            highScoreCell.put("score", spHighScore.getInt((i + 1) + "", 0));
            dataHighScore.add(highScoreCell);
        }

        String[] from = {"name", "score image", "score"};
        int[] to = {R.id.name_tv, R.id.score_iv, R.id.score_tv};

        SimpleAdapter adapter = new SimpleAdapter(this, dataHighScore, R.layout.highscore_cell, from, to);
        listView.setAdapter(adapter);

        TextView backBtnTv = findViewById(R.id.backBtn_tv);
        backBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                startActivity(new Intent(HighScore.this, MainActivity.class));
                finish();
            }
        });

        // Volume control
        final ImageView volumeIv = findViewById(R.id.volume_iv_highscore);
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
        return;
    }
}
