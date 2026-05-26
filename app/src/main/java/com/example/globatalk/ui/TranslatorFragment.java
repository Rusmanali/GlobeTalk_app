package com.example.globatalk.ui;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.globatalk.databinding.FragmentTranslatorBinding;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.Locale;

public class TranslatorFragment extends Fragment implements TextToSpeech.OnInitListener {
    private static final String TAG = "TranslatorFragment";
    private FragmentTranslatorBinding binding;
    private TextToSpeech tts;
    private String sourceLang = "English";
    private String targetLang = "Urdu";
    private Translator translator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTranslatorBinding.inflate(inflater, container, false);
        tts = new TextToSpeech(getContext(), this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] languages = {"English", "Urdu", "Arabic", "German", "Spanish"};

        binding.btnSourceLang.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Select Source Language")
                    .setItems(languages, (dialog, which) -> {
                        sourceLang = languages[which];
                        binding.btnSourceLang.setText(sourceLang);
                    }).show();
        });

        binding.btnTargetLang.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Select Target Language")
                    .setItems(languages, (dialog, which) -> {
                        targetLang = languages[which];
                        binding.btnTargetLang.setText(targetLang);
                    }).show();
        });

        binding.btnTranslate.setOnClickListener(v -> {
            String text = binding.etTranslateInput.getText().toString().trim();
            if (!text.isEmpty()) {
                performTranslation(text);
            }
        });

        binding.btnSpeakResult.setOnClickListener(v -> {
            String result = binding.tvTranslateResult.getText().toString();
            if (!result.isEmpty()) {
                speak(result);
            }
        });
    }

    private void performTranslation(String text) {
        binding.translateProgress.setVisibility(View.VISIBLE);
        binding.outputContainer.setVisibility(View.GONE);

        String sourceCode = getLanguageCode(sourceLang);
        String targetCode = getLanguageCode(targetLang);

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceCode)
                .setTargetLanguage(targetCode)
                .build();

        if (translator != null) {
            translator.close();
        }
        translator = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .build(); // Removed requireWifi() to allow faster initial setup on any connection

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> {
                    translator.translate(text)
                            .addOnSuccessListener(translatedText -> {
                                binding.translateProgress.setVisibility(View.GONE);
                                binding.outputContainer.setVisibility(View.VISIBLE);
                                binding.tvTranslateResult.setText(translatedText);
                            })
                            .addOnFailureListener(e -> {
                                binding.translateProgress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Translation failed", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    binding.translateProgress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Model download failed. Please check internet.", Toast.LENGTH_SHORT).show();
                });
    }

    private String getLanguageCode(String language) {
        switch (language) {
            case "Urdu": return TranslateLanguage.URDU;
            case "Arabic": return TranslateLanguage.ARABIC;
            case "German": return TranslateLanguage.GERMAN;
            case "Spanish": return TranslateLanguage.SPANISH;
            default: return TranslateLanguage.ENGLISH;
        }
    }

    private void speak(String text) {
        if (tts != null) {
            Locale locale = Locale.US;
            switch (targetLang) {
                case "Spanish": locale = new Locale("es", "ES"); break;
                case "German": locale = Locale.GERMAN; break;
                case "Urdu": locale = new Locale("ur", "PK"); break;
                case "Arabic": locale = new Locale("ar"); break;
            }
            
            int result = tts.setLanguage(locale);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getContext(), "Speech not supported for this language on your device", Toast.LENGTH_SHORT).show();
            } else {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            Log.e(TAG, "TTS Initialization failed");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (translator != null) {
            translator.close();
        }
        binding = null;
    }
}
