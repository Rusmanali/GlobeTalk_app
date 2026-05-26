package com.example.globatalk.data;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private String targetLanguage;
    private int streakCount;
    private long lastActiveDate;
    private int totalPoints;

    public User() {
        // Required for Firebase
    }

    public User(String uid, String email, String displayName) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.targetLanguage = "English";
        this.streakCount = 0;
        this.lastActiveDate = 0;
        this.totalPoints = 0;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }
    public int getStreakCount() { return streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }
    public long getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(long lastActiveDate) { this.lastActiveDate = lastActiveDate; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
}
