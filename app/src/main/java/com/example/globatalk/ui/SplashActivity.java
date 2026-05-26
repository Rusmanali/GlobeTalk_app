package com.example.globatalk.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.globatalk.MainActivity;
import com.example.globatalk.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            } else {
                View logo = findViewById(R.id.logo);
                View appName = findViewById(R.id.appName);
                
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                
                // Create shared element transition for a smooth transition to Login
                Pair<View, String> p1 = Pair.create(logo, "logo_trans");
                Pair<View, String> p2 = Pair.create(appName, "text_trans");
                
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, p1, p2);
                startActivity(intent, options.toBundle());
                
                // Finish after transition starts
                new Handler(Looper.getMainLooper()).postDelayed(this::finish, 1000);
            }
        }, 2000);
    }
}
