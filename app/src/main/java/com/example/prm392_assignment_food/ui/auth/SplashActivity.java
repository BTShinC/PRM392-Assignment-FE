package com.example.prm392_assignment_food.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.ui.onboarding.OnboardingActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int STAGE_1_TIMEOUT = 1000; // 1 second
    private static final int STAGE_2_TIMEOUT = 1500; // 1.5 seconds

    private ImageView ivTopLeftGraphic;
    private ImageView ivBottomRightGraphic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivTopLeftGraphic = findViewById(R.id.iv_top_left_graphic);
        ivBottomRightGraphic = findViewById(R.id.iv_bottom_right_graphic);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Stage 2: Fade in corner graphics
                fadeInGraphics();
            }
        }, STAGE_1_TIMEOUT);
    }

    private void fadeInGraphics() {
        ivTopLeftGraphic.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);

        ivBottomRightGraphic.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // After fade-in, wait and then navigate
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, STAGE_2_TIMEOUT);
                    }
                });
    }
}
