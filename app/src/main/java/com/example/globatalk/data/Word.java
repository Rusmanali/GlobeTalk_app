package com.example.globatalk.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "words")
public class Word {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String firebaseId;
    private String lessonFirebaseId; // Link to Lesson
    private String originalText;
    private String translatedText;
    private String category;
    private String language;
    private String pronunciation;
    private int difficulty;
    private boolean isLearned;
    private long lastReviewed;

    public Word() {
        // Required for Firebase
    }

    @Ignore
    public Word(String originalText, String translatedText, String category, String language) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.category = category;
        this.language = language;
        this.difficulty = 1;
        this.isLearned = false;
        this.lastReviewed = 0;
    }

    @Ignore
    public Word(String originalText, String translatedText, String category, String language, String lessonFirebaseId) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.category = category;
        this.language = language;
        this.lessonFirebaseId = lessonFirebaseId;
        this.difficulty = 1;
        this.isLearned = false;
        this.lastReviewed = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }
    public String getLessonFirebaseId() { return lessonFirebaseId; }
    public void setLessonFirebaseId(String lessonFirebaseId) { this.lessonFirebaseId = lessonFirebaseId; }
    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }
    public String getTranslatedText() { return translatedText; }
    public void setTranslatedText(String translatedText) { this.translatedText = translatedText; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getPronunciation() { return pronunciation; }
    public void setPronunciation(String pronunciation) { this.pronunciation = pronunciation; }
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public boolean isLearned() { return isLearned; }
    public void setLearned(boolean learned) { isLearned = learned; }
    public long getLastReviewed() { return lastReviewed; }
    public void setLastReviewed(long lastReviewed) { this.lastReviewed = lastReviewed; }
}
