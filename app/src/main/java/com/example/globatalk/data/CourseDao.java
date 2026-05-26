package com.example.globatalk.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

@Dao
public interface CourseDao {
    @Query("SELECT * FROM courses")
    LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM courses WHERE language = :language")
    LiveData<List<Course>> getCoursesByLanguage(String language);

    @Query("SELECT * FROM courses ORDER BY lastAccessed DESC LIMIT 1")
    LiveData<Course> getLastAccessedCourse();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourse(Course course);

    @Update
    void updateCourse(Course course);

    @Query("DELETE FROM courses")
    void deleteAll();

    @Delete
    void deleteCourse(Course course);
}
