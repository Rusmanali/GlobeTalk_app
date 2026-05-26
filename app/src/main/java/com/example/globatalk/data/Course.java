package com.example.globatalk.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "courses", indices = {@androidx.room.Index(value = {"firebaseId"}, unique = true)})
public class Course {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String firebaseId; // Unique ID for Firebase
    private String title;
    private String language;
    private String level;
    private int lessonsCount;
    private int progress;
    private String imageUrl;
    private long lastAccessed; // Timestamp for "Continue Learning"

    public Course() {
        // Required for Firebase
    }

    @Ignore
    public Course(String title, String language, String level, int lessonsCount, int progress) {
        this.title = title;
        this.language = language;
        this.level = level;
        this.lessonsCount = lessonsCount;
        this.progress = progress;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public int getLessonsCount() { return lessonsCount; }
    public void setLessonsCount(int lessonsCount) { this.lessonsCount = lessonsCount; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public long getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(long lastAccessed) { this.lastAccessed = lastAccessed; }
}
