package com.naivesoft.android.see;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.naivesoft.android.see.widget.HeartLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 2017/12/17.
 */

public class MappingAcitivity extends FragmentActivity {

    private Random mRandom = new Random();
    private Timer mTimer = new Timer();
    private HeartLayout mHeartLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapping);

        mHeartLayout = findViewById(R.id.heart_layout);
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHeartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeartLayout.addHeart(randomColor());
                    }
                });
            }
        }, 500, 200);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
    }

    @Override
    protected void onDestroy() {
        mTimer.cancel();

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
