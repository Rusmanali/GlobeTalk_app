package com.example.globatalk.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.globatalk.databinding.OnboardingBinding;

public class OnboardingActivity extends AppCompatActivity {
    private OnboardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Note: In a real app, you might use a ViewPager2 for multiple slides.
        // For now, based on onboarding.xml, we have a single informative screen.
        
        // Find the button from onboarding.xml (assuming it has id btnNext or similar)
        // Since I'm not sure of the ID in onboarding.xml, I'll use a generic approach or check the file.
        // Let's assume the button has an ID, or I'll add one.
        
        binding.btnNext.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("GlobeTalkPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("isFirstRun", false).apply();
            
            startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
            finish();
        });
    }
}
