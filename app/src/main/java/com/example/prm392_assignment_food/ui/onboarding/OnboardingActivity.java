package com.example.prm392_assignment_food.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.ui.auth.LoginActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnNext, btnSkip, btnGetStarted;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);
        btnGetStarted = findViewById(R.id.btn_get_started);

        adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adapter.getItemCount() - 1) {
                    showGetStartedButton();
                } else {
                    showNextAndSkipButtons();
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        btnSkip.setOnClickListener(v -> navigateToLogin());
        btnGetStarted.setOnClickListener(v -> navigateToLogin());
    }

    private void showGetStartedButton() {
        btnNext.setVisibility(View.GONE);
        btnSkip.setVisibility(View.GONE);
        btnGetStarted.setVisibility(View.VISIBLE);
    }

    private void showNextAndSkipButtons() {
        btnNext.setVisibility(View.VISIBLE);
        btnSkip.setVisibility(View.VISIBLE);
        btnGetStarted.setVisibility(View.GONE);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish OnboardingActivity so user can't go back to it
    }
}
