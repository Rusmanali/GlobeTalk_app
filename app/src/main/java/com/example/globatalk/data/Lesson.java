package com.example.globatalk.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "lessons", indices = {@androidx.room.Index(value = {"firebaseId"}, unique = true)})
public class Lesson {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String firebaseId;
    private String courseFirebaseId; // Link to Course
    private String title;
    private String description;
    private int order;
    private boolean isCompleted;

    public Lesson() {
        // Required for Firebase
    }

    @Ignore
    public Lesson(String title, String description, int order, String courseFirebaseId) {
        this.title = title;
        this.description = description;
        this.order = order;
        this.courseFirebaseId = courseFirebaseId;
        this.isCompleted = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }
    public String getCourseFirebaseId() { return courseFirebaseId; }
    public void setCourseFirebaseId(String courseFirebaseId) { this.courseFirebaseId = courseFirebaseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
