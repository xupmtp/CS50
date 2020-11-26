package edu.harvard.cs50.wordcard.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.harvard.cs50.wordcard.model.Words;

@Dao
public interface WordsDao {

    @Query("select * from words where lessons_id = :lessonsId and is_alive = 1")
    List<Words> selectByLessons(int lessonsId);

    @Insert
    long insertWord(Words words);

    @Update
    long updateWord(Words words);
}
