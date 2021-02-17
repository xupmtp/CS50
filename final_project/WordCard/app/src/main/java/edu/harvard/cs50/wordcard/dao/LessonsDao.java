package edu.harvard.cs50.wordcard.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.harvard.cs50.wordcard.model.Lessons;

@Dao
public interface LessonsDao {

    @Query("select * from lessons where users_id = :usersId and is_alive = 1")
    List<Lessons> selectByUserId(int usersId);

    @Insert
    long insertLesson(Lessons lessons);

    @Update
    int updateLesson(Lessons lessons);

}
