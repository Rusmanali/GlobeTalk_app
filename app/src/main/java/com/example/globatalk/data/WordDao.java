package com.example.globatalk.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface WordDao {
    @Query("SELECT * FROM words")
    LiveData<List<Word>> getAllWords();

    @Query("SELECT * FROM words WHERE category = :category AND language = :language")
    LiveData<List<Word>> getWordsByCategory(String category, String language);

    @Query("SELECT * FROM words WHERE isLearned = 0 ORDER BY RANDOM() LIMIT 10")
    LiveData<List<Word>> getDailyWords();

    @Query("SELECT * FROM words WHERE language = :language AND isLearned = 0 ORDER BY RANDOM() LIMIT 10")
    LiveData<List<Word>> getDailyWordsByLanguage(String language);

    @Query("SELECT * FROM words WHERE lessonFirebaseId = :lessonId")
    LiveData<List<Word>> getWordsForLesson(String lessonId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWord(Word word);

    @Update
    void updateWord(Word word);

    @Delete
    void deleteWord(Word word);
}
