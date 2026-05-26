package com.example.globatalk;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.globatalk.databinding.ActivityMainBinding;
import com.example.globatalk.ui.HomeFragment;
import com.example.globatalk.ui.ProfileFragment;
import com.example.globatalk.ui.TranslatorFragment;
import com.example.globatalk.ui.AIChatFragment;
import com.example.globatalk.ui.ProgressFragment;
import com.example.globatalk.data.CourseViewModel;
import com.example.globatalk.data.FirebaseRepository;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executors;
import com.example.globatalk.data.AppDatabase;
import com.example.globatalk.data.Course;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseRepository firebaseRepository = new FirebaseRepository(this);
        firebaseRepository.syncCourses();

        CourseViewModel courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        
        courseViewModel.getAllCourses().observe(this, courses -> {
            if (courses.isEmpty()) {
                Log.d("MainActivity", "Initializing professional curriculum...");
                firebaseRepository.seedFirebase();
            } else {
                // Remove any courses that don't have a Firebase ID
                for (Course c : courses) {
                    if (c.getFirebaseId() == null) {
                        Log.d("MainActivity", "Removing legacy local course: " + c.getTitle());
                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppDatabase.getDatabase(this).courseDao().deleteCourse(c);
                        });
                    }
                }
            }
        });

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_learn) {
                selectedFragment = new TranslatorFragment();
            } else if (itemId == R.id.nav_chat) {
                selectedFragment = new AIChatFragment();
            } else if (itemId == R.id.nav_progress) {
                selectedFragment = new ProgressFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }
}
