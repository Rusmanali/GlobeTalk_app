package com.example.globatalk.data;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordViewModel extends AndroidViewModel {
    private final WordDao wordDao;
    private final ExecutorService executorService;

    public WordViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        wordDao = db.wordDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Word>> getAllWords() {
        return wordDao.getAllWords();
    }

    public LiveData<List<Word>> getDailyWords() {
        return wordDao.getDailyWords();
    }

    public LiveData<List<Word>> getDailyWordsByLanguage(String language) {
        return wordDao.getDailyWordsByLanguage(language);
    }

    public LiveData<List<Word>> getWordsByCategory(String category, String language) {
        return wordDao.getWordsByCategory(category, language);
    }

    public void insert(Word word) {
        executorService.execute(() -> wordDao.insertWord(word));
    }

    public void update(Word word) {
        executorService.execute(() -> wordDao.updateWord(word));
    }

    public void seedData() {
        executorService.execute(() -> {
            // English
            wordDao.insertWord(new Word("Hello", "Hello", "Vocabulary", "English"));
            wordDao.insertWord(new Word("How are you?", "How are you?", "Speaking", "English"));
            wordDao.insertWord(new Word("I am learning.", "I am learning.", "Grammar", "English"));
            
            // Urdu
            wordDao.insertWord(new Word("ہیلو", "Hello", "Vocabulary", "Urdu"));
            wordDao.insertWord(new Word("آپ کیسے ہیں؟", "How are you?", "Speaking", "Urdu"));
            wordDao.insertWord(new Word("میں سیکھ رہا ہوں۔", "I am learning.", "Grammar", "Urdu"));

            // Arabic
            wordDao.insertWord(new Word("مرحباً", "Hello", "Vocabulary", "Arabic"));
            wordDao.insertWord(new Word("كيف حالك؟", "How are you?", "Speaking", "Arabic"));
            wordDao.insertWord(new Word("أنا أتعلم.", "I am learning.", "Grammar", "Arabic"));

            // German
            wordDao.insertWord(new Word("Hallo", "Hello", "Vocabulary", "German"));
            wordDao.insertWord(new Word("Wie geht es dir?", "How are you?", "Speaking", "German"));
            wordDao.insertWord(new Word("Ich lerne.", "I am learning.", "Grammar", "German"));

            // Spanish
            wordDao.insertWord(new Word("Hola", "Hello", "Vocabulary", "Spanish"));
            wordDao.insertWord(new Word("¿Cómo estás?", "How are you?", "Speaking", "Spanish"));
            wordDao.insertWord(new Word("Estoy aprendiendo.", "I am learning.", "Grammar", "Spanish"));
        });
    }
}
