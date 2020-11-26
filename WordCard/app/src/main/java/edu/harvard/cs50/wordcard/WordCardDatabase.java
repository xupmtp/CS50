package edu.harvard.cs50.wordcard;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import edu.harvard.cs50.wordcard.dao.LessonsDao;
import edu.harvard.cs50.wordcard.dao.UsersDao;
import edu.harvard.cs50.wordcard.dao.WordsDao;
import edu.harvard.cs50.wordcard.model.Lessons;
import edu.harvard.cs50.wordcard.model.Users;
import edu.harvard.cs50.wordcard.model.Words;

/**
 * 第一版 新增資料庫
 */
@Database(entities = {Users.class, Lessons.class, Words.class}, version = 1)
public abstract class WordCardDatabase extends RoomDatabase {
    public abstract UsersDao usersDao();
    public abstract LessonsDao lessonsDao();
    public abstract WordsDao wordsDao();
}
