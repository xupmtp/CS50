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

    @Query("select * from words where lessons_id = :lessonsId and is_alive = 1 and favorite = 1")
    List<Words> selectFavoriteWords(int lessonsId);

    @Insert
    long insertWord(Words words);

    @Update
    int updateWord(Words words);

    @Query("update words set favorite = :favorite where id = :id")
    int updateFavorite(boolean favorite, int id);

    @Query("update words set front_word = :word, back_detail = :detail where id = :id")
    int updateWordText(String word, String detail, int id);
}
