package com.example.doit.viewmodel;

import androidx.lifecycle.LiveData;

import com.example.doit.model.QuestionPostData;

import java.util.List;

public interface IMainViewModel {
    public LiveData<List<QuestionPostData>> getPostsLiveData();
    public void loadAds(Runnable onFinish);
}

