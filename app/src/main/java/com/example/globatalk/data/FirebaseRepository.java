package com.example.globatalk.data;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseRepository {
    private static final String TAG = "FirebaseRepository";
    private final DatabaseReference mDatabase;
    private final CourseDao courseDao;
    private final LessonDao lessonDao;
    private final WordDao wordDao;
    private final ExecutorService executorService;

    public FirebaseRepository(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        AppDatabase db = AppDatabase.getDatabase(context);
        courseDao = db.courseDao();
        lessonDao = db.lessonDao();
        wordDao = db.wordDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public void syncCourses() {
        Log.d(TAG, "Starting course sync...");
        mDatabase.child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Courses onDataChange: " + snapshot.getChildrenCount() + " items");
                executorService.execute(() -> {
                    // Option 1: Clean local courses first if we want absolute mirror
                    // courseDao.deleteAll(); 

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Course course = postSnapshot.getValue(Course.class);
                        if (course != null) {
                            course.setFirebaseId(postSnapshot.getKey());
                            courseDao.insertCourse(course);
                            syncLessons(course.getFirebaseId());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadCourses:onCancelled", error.toException());
            }
        });
    }

    public void syncLessons(String courseId) {
        Log.d(TAG, "Setting up lesson sync for course: " + courseId);
        mDatabase.child("lessons").child(courseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Lessons data received from cloud for course " + courseId + ": " + snapshot.getChildrenCount() + " items");
                executorService.execute(() -> {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Lesson lesson = postSnapshot.getValue(Lesson.class);
                        if (lesson != null) {
                            lesson.setFirebaseId(postSnapshot.getKey());
                            lesson.setCourseFirebaseId(courseId);
                            lessonDao.insertLesson(lesson);
                            Log.d(TAG, "Inserted lesson: " + lesson.getTitle() + " for course: " + courseId);
                            syncWords(lesson.getFirebaseId());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadLessons:onCancelled", error.toException());
            }
        });
    }

    public void syncWords(String lessonId) {
        mDatabase.child("words").child(lessonId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                executorService.execute(() -> {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Word word = postSnapshot.getValue(Word.class);
                        if (word != null) {
                            word.setFirebaseId(postSnapshot.getKey());
                            word.setLessonFirebaseId(lessonId);
                            wordDao.insertWord(word);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadWords:onCancelled", error.toException());
            }
        });
    }
    
    public void seedFirebase() {
        mDatabase.child("courses").removeValue();
        mDatabase.child("lessons").removeValue();
        mDatabase.child("words").removeValue();

        addCourseWithDetailedLessons("English for Beginners", "English", "Beginner", 15, new String[]{
            "Alphabet & Greetings", "Numbers & Time", "Colors & Shapes"
        });

        addCourseWithDetailedLessons("Spanish Mastery", "Spanish", "Beginner", 20, new String[]{
            "¡Hola! & Vowels", "Basic Nouns", "Pronouns"
        });

        addCourseWithDetailedLessons("Urdu Conversation", "Urdu", "Beginner", 12, new String[]{
            "Greetings", "Family", "Food"
        });
    }

    private void addCourseWithDetailedLessons(String title, String lang, String level, int lessonCount, String[] lessonTitles) {
        String courseId = mDatabase.child("courses").push().getKey();
        if (courseId == null) return;

        Course course = new Course(title, lang, level, lessonCount, 0);
        course.setFirebaseId(courseId);
        mDatabase.child("courses").child(courseId).setValue(course);

        for (int i = 0; i < lessonTitles.length; i++) {
            String lessonId = mDatabase.child("lessons").child(courseId).push().getKey();
            if (lessonId == null) continue;

            Lesson lesson = new Lesson(lessonTitles[i], "Master " + lessonTitles[i], i + 1, courseId);
            lesson.setFirebaseId(lessonId);
            mDatabase.child("lessons").child(courseId).child(lessonId).setValue(lesson);

            addWordsToFirebaseLesson(lessonId, lang);
        }
    }

    private void addWordsToFirebaseLesson(String lessonId, String lang) {
        String[][] words;
        switch (lang) {
            case "Spanish":
                if (lessonId.contains("0")) // First lesson
                    words = new String[][]{{"Hola", "Hello"}, {"Buenos días", "Good morning"}};
                else if (lessonId.contains("1"))
                    words = new String[][]{{"Amigo", "Friend"}, {"Familia", "Family"}};
                else
                    words = new String[][]{{"Comida", "Food"}, {"Agua", "Water"}};
                break;
            case "Urdu":
                if (lessonId.contains("0"))
                    words = new String[][]{{"ہیلو", "Hello"}, {"شکریہ", "Thank you"}};
                else
                    words = new String[][]{{"دوست", "Friend"}, {"گھر", "Home"}};
                break;
            case "English":
                if (lessonId.contains("0"))
                    words = new String[][]{{"Welcome", "Welcome"}, {"Greetings", "Greetings"}};
                else
                    words = new String[][]{{"School", "School"}, {"Book", "Book"}};
                break;
            default:
                words = new String[][]{{"Start", "Start"}};
                break;
        }

        for (String[] w : words) {
            Word word = new Word(w[0], w[1], "Vocab", lang, lessonId);
            String wordId = mDatabase.child("words").child(lessonId).push().getKey();
            if (wordId != null) {
                word.setFirebaseId(wordId);
                mDatabase.child("words").child(lessonId).child(wordId).setValue(word);
            }
        }
    }
}
