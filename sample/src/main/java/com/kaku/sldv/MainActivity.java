package com.kaku.sldv;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.kaku.sample.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView slidingTipIv = (TextView) findViewById(R.id.sliding_tip_tv);
        AnimationDrawable animationDrawable = (AnimationDrawable) slidingTipIv.getCompoundDrawables()[0];
        animationDrawable.start();

        SlideUnlockView mySlidingView = (SlideUnlockView) findViewById(R.id.slide_unlock_view);
        mySlidingView.setSlidingTipListener(new SlideUnlockView.SlidingTipListener() {
            @Override
            public void onSlidFinish() {
                Toast.makeText(MainActivity.this, "Unlock success", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
