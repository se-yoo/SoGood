package com.gigamole.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        final View btnHorizontalNtb = findViewById(R.id.btn_today_good);
        btnHorizontalNtb.setOnClickListener(this);
        final View btnTopHorizontalNtb = findViewById(R.id.btn_good_introduce);
        btnTopHorizontalNtb.setOnClickListener(this);
        final View btnSamplesNtb = findViewById(R.id.btn_heart);
        btnSamplesNtb.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        ViewCompat.animate(v)
                .setDuration(200)
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setInterpolator(new CycleInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(final View view) {

                    }

                    @Override
                    public void onAnimationEnd(final View view) {
                        switch (v.getId()) {
                            case R.id.btn_today_good:
                                startActivity(
                                        new Intent(MainActivity.this, Today_Good.class)
                                );
                                break;
                            case R.id.btn_good_introduce:
                                startActivity(
                                        new Intent(MainActivity.this, Good_Introduce.class)
                                );
                                break;

                            case R.id.btn_heart:
                                startActivity(
                                        new Intent(MainActivity.this, Heart.class)
                                );
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onAnimationCancel(final View view) {

                    }
                })
                .withLayer()
                .start();
    }

    private class CycleInterpolator implements android.view.animation.Interpolator {

        private final float mCycles = 0.5f;

        @Override
        public float getInterpolation(final float input) {
            return (float) Math.sin(2.0f * mCycles * Math.PI * input);
        }
    }
}
