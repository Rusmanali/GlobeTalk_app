package com.example.globatalk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.globatalk.data.Word;
import com.example.globatalk.data.WordViewModel;
import com.example.globatalk.databinding.FragmentQuizBinding;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.List;
import java.util.Random;

public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;
    private WordViewModel viewModel;
    private Word currentWord;
    private int score = 0;
    private int questionsDone = 0;
    private List<Word> allWords;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(WordViewModel.class);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        score = prefs.getInt("total_points", 0);
        updateUI();

        viewModel.getAllWords().observe(getViewLifecycleOwner(), words -> {
            if (words != null && !words.isEmpty()) {
                allWords = words;
                if (currentWord == null) {
                    showRandomQuestion();
                }
            }
        });

        binding.btnSubmit.setOnClickListener(v -> {
            String answer = binding.editAnswer.getText().toString().trim();
            if (currentWord != null) {
                if (answer.equalsIgnoreCase(currentWord.getTranslatedText())) {
                    score += 10;
                    PreferenceManager.getDefaultSharedPreferences(getContext())
                            .edit().putInt("total_points", score).apply();
                    Toast.makeText(getContext(), "Correct! +10 points", Toast.LENGTH_SHORT).show();
                    currentWord.setLearned(true);
                    viewModel.update(currentWord);
                } else {
                    Toast.makeText(getContext(), "Incorrect. It was: " + currentWord.getTranslatedText(), Toast.LENGTH_LONG).show();
                }
                
                questionsDone++;
                updateUI();
                showRandomQuestion();
            }
            binding.editAnswer.setText("");
        });
    }

    private void updateUI() {
        binding.tvScore.setText("Score: " + score);
        binding.progressBar.setProgress(questionsDone % 11);
    }

    private void showRandomQuestion() {
        if (allWords == null || allWords.isEmpty()) return;
        Random random = new Random();
        currentWord = allWords.get(random.nextInt(allWords.size()));
        binding.textQuestion.setText("Translate: " + currentWord.getOriginalText());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
