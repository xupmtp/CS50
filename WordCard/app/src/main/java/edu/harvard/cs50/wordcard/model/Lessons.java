package edu.harvard.cs50.wordcard.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "lessons", foreignKeys = @ForeignKey(entity = Users.class,
        parentColumns = "id",
        childColumns = "users_id",
        onDelete = CASCADE), indices = @Index(value = {"id"}, unique = true))
public class Lessons {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "users_id")
    private int usersId;

    @ColumnInfo(name = "name")
    private String name;

    //user刪除時不delete 改修狀態
    @ColumnInfo(name = "is_alive", defaultValue = "1")
    private boolean isAlive;

    public Lessons() {

    }

    public Lessons(int usersId, String name, boolean isAlive) {
        this.usersId = usersId;
        this.name = name;
        this.isAlive = isAlive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
