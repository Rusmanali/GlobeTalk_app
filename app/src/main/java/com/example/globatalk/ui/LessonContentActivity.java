package com.example.globatalk.ui;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.globatalk.data.AppDatabase;
import com.example.globatalk.databinding.ActivityLessonContentBinding;
import java.util.Locale;

public class LessonContentActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private ActivityLessonContentBinding binding;
    private WordAdapter adapter;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLessonContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String lessonId = getIntent().getStringExtra("lesson_firebase_id");
        String lessonTitle = getIntent().getStringExtra("lesson_title");

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(lessonTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        tts = new TextToSpeech(this, this);

        adapter = new WordAdapter((text, language) -> speak(text, language));
        binding.rvLessonWords.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLessonWords.setAdapter(adapter);

        AppDatabase db = AppDatabase.getDatabase(this);
        db.wordDao().getWordsForLesson(lessonId).observe(this, words -> {
            if (words != null) {
                adapter.setWords(words);
            }
        });
    }

    private void speak(String text, String language) {
        if (tts != null) {
            Locale locale = Locale.US;
            if (language.equals("Spanish")) locale = new Locale("es", "ES");
            else if (language.equals("German")) locale = Locale.GERMAN;
            else if (language.equals("Urdu")) locale = new Locale("ur", "PK");
            else if (language.equals("Arabic")) locale = new Locale("ar");
            
            tts.setLanguage(locale);
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onInit(int status) {}

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
