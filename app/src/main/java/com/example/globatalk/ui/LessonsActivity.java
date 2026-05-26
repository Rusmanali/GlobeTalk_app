package com.example.globatalk.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.globatalk.data.AppDatabase;
import com.example.globatalk.databinding.ActivityLessonsBinding;

public class LessonsActivity extends AppCompatActivity {
    private ActivityLessonsBinding binding;
    private LessonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLessonsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String courseFirebaseId = getIntent().getStringExtra("course_firebase_id");
        String courseTitle = getIntent().getStringExtra("course_title");

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(courseTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new LessonAdapter();
        adapter.setOnLessonClickListener(lesson -> {
            Intent intent = new Intent(this, LessonContentActivity.class);
            intent.putExtra("lesson_firebase_id", lesson.getFirebaseId());
            intent.putExtra("lesson_title", lesson.getTitle());
            startActivity(intent);
        });
        binding.rvLessons.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLessons.setAdapter(adapter);

        AppDatabase db = AppDatabase.getDatabase(this);
        db.lessonDao().getLessonsForCourse(courseFirebaseId).observe(this, lessons -> {
            if (lessons != null) {
                android.util.Log.d("LessonsActivity", "Lessons found: " + lessons.size() + " for course: " + courseFirebaseId);
                adapter.setLessons(lessons);
            } else {
                android.util.Log.d("LessonsActivity", "Lessons LiveData is null");
            }
        });
    }
}
