package com.example.doit.viewmodel;

import androidx.lifecycle.LiveData;

import com.example.doit.model.Consumer;
import com.example.doit.model.NewQuestion;
import com.example.doit.model.QuestionPostData;

import java.util.List;

public interface IMainViewModel {
    public LiveData<List<QuestionPostData>> getPostsLiveData();
    public void loadAds(Runnable onFinish);
    public void deletePost(QuestionPostData questionPostData);
    public void getListOfQuestions(Consumer<List<NewQuestion>> consumerList);
}

