package com.example.globatalk.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class FirebaseSync {
    private final DatabaseReference database;

    public FirebaseSync() {
        database = FirebaseDatabase.getInstance().getReference("users_words");
    }

    public void uploadWords(String userId, List<Word> words) {
        database.child(userId).setValue(words);
    }

    public void downloadWords(String userId) {
        // Implementation for downloading words from Firebase
    }
}
