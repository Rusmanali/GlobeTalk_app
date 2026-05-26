package com.example.globatalk.data;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourseViewModel extends AndroidViewModel {
    private final CourseDao courseDao;
    private final ExecutorService executorService;

    public CourseViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        courseDao = db.courseDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Course>> getAllCourses() {
        return courseDao.getAllCourses();
    }

    public LiveData<List<Course>> getCoursesByLanguage(String language) {
        return courseDao.getCoursesByLanguage(language);
    }

    public LiveData<Course> getLastAccessedCourse() {
        return courseDao.getLastAccessedCourse();
    }

    public void insert(Course course) {
        executorService.execute(() -> courseDao.insertCourse(course));
    }

    public void update(Course course) {
        executorService.execute(() -> courseDao.updateCourse(course));
    }

    public void seedCourses() {
        // Method deprecated - Use FirebaseRepository.seedFirebase() instead
        // to ensure all courses have valid Firebase IDs
    }
}
