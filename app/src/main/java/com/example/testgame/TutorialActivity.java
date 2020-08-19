package com.example.testgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.OnCompleteListener;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        final FancyShowCaseView fancyShowCaseView = new FancyShowCaseView.Builder(this)
                .title(getResources().getString(R.string.showcase1_txt))
                .titleSize(46, TypedValue.COMPLEX_UNIT_SP)
                .titleGravity(Gravity.CENTER)
                .build();

        FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.shooting_tut_tv))
                .title(getResources().getString(R.string.showcase2_txt))
                .titleSize(56, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();

        FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.player_tut_tv))
                .title("\n\n"+getResources().getString(R.string.tuturial1))
                .titleSize(56, TypedValue.COMPLEX_UNIT_SP)
                .titleGravity(Gravity.CENTER)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();

        FancyShowCaseView fancyShowCaseView3 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.enemy_tut_tv))
                .title("\n\n\n\n\n"+getResources().getString(R.string.tutorial2))
                .titleSize(56, TypedValue.COMPLEX_UNIT_SP)
                .titleGravity(Gravity.BOTTOM)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();


        FancyShowCaseQueue queue = new FancyShowCaseQueue()
                .add(fancyShowCaseView)
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2)
                .add(fancyShowCaseView3);

        queue.show();
        queue.setCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete() {
                ImageView bgIv = findViewById(R.id.layout_bg_iv);
                bgIv.setBackgroundResource(R.drawable.tutorial_rockets);

                FancyShowCaseView fancyShowCaseView4 = new FancyShowCaseView.Builder(TutorialActivity.this)
                        .focusOn(findViewById(R.id.rockets_tut_tv))
                        .title("\n\n\n\n\n\n"+getResources().getString(R.string.tutorial3))
                        .titleSize(56, TypedValue.COMPLEX_UNIT_SP)
                        .titleGravity(Gravity.BOTTOM)
                        .focusShape(FocusShape.ROUNDED_RECTANGLE)
                        .roundRectRadius(90)
                        .build();
                FancyShowCaseQueue queue2 = new FancyShowCaseQueue().add(fancyShowCaseView4);
                queue2.show();
                queue2.setCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        SharedPreferences spLevels = getSharedPreferences("open levels", MODE_PRIVATE);
                        SharedPreferences.Editor editorlevels = spLevels.edit();
                        editorlevels.putBoolean("first time playing", false);
                        editorlevels.commit();
                        finish();
                    }
                });
            }
        });
    }
    @Override
    public void onBackPressed() {
        return;
    }
}
