package com.example.doit.repository;

import androidx.lifecycle.LiveData;

import com.example.doit.model.Consumer;
import com.example.doit.model.NewQuestion;
import com.example.doit.model.QuestionPostData;

import java.util.List;

public interface IMainRepository {
    public void loadAds(Runnable onFinish);
    public LiveData<List<QuestionPostData>> getPostsLiveData();
    public void deletePost(QuestionPostData questionPostData, Runnable onFinish);
    public void getListOfQuestions(Consumer<List<NewQuestion>> consumerList);
}
