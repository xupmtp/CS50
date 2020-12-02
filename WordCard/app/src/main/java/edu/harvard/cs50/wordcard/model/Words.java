package edu.harvard.cs50.wordcard.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "words", foreignKeys = @ForeignKey(entity = Lessons.class,
        parentColumns = "id",
        childColumns = "lessons_id",
        onDelete = CASCADE), indices = @Index(value = {"id"}, unique = true))
public class Words {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "lessons_id")
    private int lessonsId;

    //單字卡正面單字
    @ColumnInfo(name = "front_word")
    private String frontWord;

    //單字卡背面說明
    @ColumnInfo(name = "back_detail")
    private String backDetail;

    @ColumnInfo(name = "favorite", defaultValue = "0")
    private boolean favorite;

    //user刪除時不delete 改修狀態
    @ColumnInfo(name = "is_alive", defaultValue = "1")
    private boolean isAlive;

    public Words() {

    }

    public Words(int lessonsId, String frontWord, String backDetail, boolean favorite, boolean isAlive) {
        this.lessonsId = lessonsId;
        this.frontWord = frontWord;
        this.backDetail = backDetail;
        this.favorite = favorite;
        this.isAlive = isAlive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLessonsId() {
        return lessonsId;
    }

    public void setLessonsId(int lessonsId) {
        this.lessonsId = lessonsId;
    }

    public String getFrontWord() {
        return frontWord;
    }

    public void setFrontWord(String frontWord) {
        this.frontWord = frontWord;
    }

    public String getBackDetail() {
        return backDetail;
    }

    public void setBackDetail(String backDetail) {
        this.backDetail = backDetail;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
