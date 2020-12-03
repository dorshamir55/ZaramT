package com.example.doit.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.doit.model.QuestionPostData;

import java.util.Date;
import java.util.List;

@Dao
public interface AdDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertPost(QuestionPostData post);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAll(List<QuestionPostData> posts);

    @Delete
    public void deletePost(QuestionPostData post);

    @Delete
    public void deleteAll(List<QuestionPostData> posts);

    @Query("SELECT * FROM "+QuestionPostData.TABLE_NAME+" WHERE postedUserId = :userID ORDER BY startDate DESC")
    public LiveData<List<QuestionPostData>> getMyPosts(String userID);

    @Query("SELECT * FROM "+QuestionPostData.TABLE_NAME+" ORDER BY startDate DESC")
    public LiveData<List<QuestionPostData>> getAllPosts();

    @Query("SELECT max(updateDate) FROM "+QuestionPostData.TABLE_NAME)
    public Date findMaxUpdateDate();
}
