package com.example.globatalk.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

@Dao
public interface LessonDao {
    @Query("SELECT * FROM lessons WHERE courseFirebaseId = :courseId ORDER BY `order` ASC")
    LiveData<List<Lesson>> getLessonsForCourse(String courseId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLesson(Lesson lesson);

    @Update
    void updateLesson(Lesson lesson);

    @Query("DELETE FROM lessons")
    void deleteAll();

    @Delete
    void deleteLesson(Lesson lesson);
}
