package edu.harvard.cs50.wordcard.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.harvard.cs50.wordcard.model.Users;

@Dao
public interface UsersDao {

    @Query("select * from users where name = :name")
    List<Users> selectByName(String name);

    @Query("select * from users where name = :name and password = :password")
    List<Users> login(String name, String password);

    @Insert()
    long insertUser(Users users);

    @Update
    int updateUsers(Users users);
}
