package com.example.globatalk.ui;

import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.globatalk.data.WordViewModel;
import com.example.globatalk.data.CourseViewModel;
import com.example.globatalk.data.Word;
import com.example.globatalk.data.Course;
import com.example.globatalk.databinding.FragmentHomeBinding;
import androidx.appcompat.app.AlertDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.globatalk.R;
import com.example.globatalk.util.StreakManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment implements TextToSpeech.OnInitListener {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private WordViewModel wordViewModel;
    private CourseViewModel courseViewModel;
    private TextToSpeech tts;
    private String currentLanguage = "English";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            if (name == null || name.isEmpty()) {
                name = (email != null) ? email.split("@")[0] : "User";
            }
            binding.tvWelcomeUser.setText(getString(R.string.welcome_user, name));
        }

        binding.tvHomeStreak.setText(getString(R.string.day_streak_format, StreakManager.getStreak(getContext())));
        binding.tvHomePoints.setText(getString(R.string.xp_points_format, PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("total_points", 0)));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentLanguage = prefs.getString("selected_language", "English");
        binding.btnChangeLanguage.setText(currentLanguage);

        tts = new TextToSpeech(getContext(), this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StreakManager.updateStreak(getContext());

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        
        // Setup Daily Lessons (Words)
        WordAdapter wordAdapter = new WordAdapter((text, language) -> speak(text));
        wordAdapter.setOnLearnedClickListener(word -> {
            word.setLearned(!word.isLearned());
            wordViewModel.update(word);
            Toast.makeText(getContext(), word.isLearned() ? "Marked as learned!" : "Moved back to study", Toast.LENGTH_SHORT).show();
            
            // Add some XP for learning
            if (word.isLearned()) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                int points = prefs.getInt("total_points", 0) + 5;
                prefs.edit().putInt("total_points", points).apply();
                binding.tvHomePoints.setText(getString(R.string.xp_points_format, points));
            }
        });
        
        binding.recyclerViewDaily.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewDaily.setAdapter(wordAdapter);
        
        loadDailyWords(wordAdapter);
        setupCategories(wordAdapter);

        // Setup Popular Courses
        CourseAdapter courseAdapter = new CourseAdapter(course -> {
            openCourse(course);
        });
        binding.rvPopularCourses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPopularCourses.setAdapter(courseAdapter);

        courseViewModel.getAllCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null && !courses.isEmpty()) {
                courseAdapter.setCourses(courses);
            }
        });

        // Setup Continue Learning Card
        courseViewModel.getLastAccessedCourse().observe(getViewLifecycleOwner(), course -> {
            if (course != null) {
                binding.tvCurrentCourse.setText(course.getTitle());
                binding.tvProgressPercent.setText(getString(R.string.percent_format, course.getProgress()));
                binding.courseProgress.setProgress(course.getProgress());
                binding.btnContinueLesson.setOnClickListener(v -> openCourse(course));
            } else {
                // If no course accessed, hide or show a default
                binding.tvCurrentCourse.setText(R.string.select_course_prompt);
                binding.tvProgressPercent.setText("0%");
                binding.courseProgress.setProgress(0);
            }
        });
        
        binding.btnChangeLanguage.setOnClickListener(v -> {
            String[] languages = {"English", "Urdu", "Arabic", "German", "Spanish"};
            new AlertDialog.Builder(getContext())
                    .setTitle("Choose Language")
                    .setItems(languages, (dialog, which) -> {
                        currentLanguage = languages[which];
                        binding.btnChangeLanguage.setText(currentLanguage);
                        PreferenceManager.getDefaultSharedPreferences(getContext())
                                .edit().putString("selected_language", currentLanguage).apply();
                        setTtsLanguage();
                        loadDailyWords(wordAdapter);
                    })
                    .show();
        });
    }

    private void openCourse(Course course) {
        course.setLastAccessed(System.currentTimeMillis());
        courseViewModel.update(course);
        
        Intent intent = new Intent(getActivity(), LessonsActivity.class);
        intent.putExtra("course_firebase_id", course.getFirebaseId());
        intent.putExtra("course_title", course.getTitle());
        startActivity(intent);
    }

    private void setupCategories(WordAdapter adapter) {
        binding.btnCatVocab.setOnClickListener(v -> filterByCategory("Vocabulary", adapter));
        binding.btnCatGrammar.setOnClickListener(v -> filterByCategory("Grammar", adapter));
        binding.btnCatSpeaking.setOnClickListener(v -> filterByCategory("Speaking", adapter));
        binding.btnCatQuiz.setOnClickListener(v -> Toast.makeText(getContext(), "Switch to Quiz tab!", Toast.LENGTH_SHORT).show());
        binding.btnCatListening.setOnClickListener(v -> Toast.makeText(getContext(), "Listening lessons coming soon!", Toast.LENGTH_SHORT).show());
        binding.btnCatChat.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AIChatFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void filterByCategory(String category, WordAdapter adapter) {
        wordViewModel.getWordsByCategory(category, currentLanguage).observe(getViewLifecycleOwner(), words -> {
             if (words != null) {
                 adapter.setWords(words);
                 Toast.makeText(getContext(), "Loaded " + words.size() + " " + category, Toast.LENGTH_SHORT).show();
             }
        });
    }

    private void loadDailyWords(WordAdapter adapter) {
        wordViewModel.getDailyWordsByLanguage(currentLanguage).observe(getViewLifecycleOwner(), words -> {
            adapter.setWords(words);
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            setTtsLanguage();
        }
    }

    private void setTtsLanguage() {
        if (tts == null) return;
        Locale locale = Locale.US;
        switch (currentLanguage) {
            case "Spanish": locale = new Locale("es", "ES"); break;
            case "German": locale = Locale.GERMAN; break;
            case "Urdu": locale = new Locale("ur", "PK"); break;
            case "Arabic": locale = new Locale("ar"); break;
            default: locale = Locale.US; break;
        }
        tts.setLanguage(locale);
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        binding = null;
    }
}
