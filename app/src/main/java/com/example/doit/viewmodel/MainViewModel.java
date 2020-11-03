package com.example.doit.viewmodel;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.doit.model.Consumer;
import com.example.doit.model.NewQuestion;
import com.example.doit.model.QuestionPostData;
import com.example.doit.repository.IMainRepository;
import com.example.doit.repository.MainRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel implements IMainViewModel {
    private IMainRepository mainRepository;
    private LiveData<List<QuestionPostData>> questionPostLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mainRepository = new MainRepository(application);
        questionPostLiveData = mainRepository.getPostsLiveData();
    }

    @Override
    public LiveData<List<QuestionPostData>> getPostsLiveData() {
        return questionPostLiveData;
    }

    @Override
    public void loadAds(Runnable onFinish) {
        mainRepository.loadAds(onFinish);
    }

    @Override
    public void deletePost(QuestionPostData questionPostData) {
        mainRepository.deletePost(questionPostData, () -> {
            LocalBroadcastManager.getInstance(getApplication().getApplicationContext())
                    .sendBroadcast(new Intent("com.project.ACTION_RELOAD"));
        });
    }

    @Override
    public void getListOfQuestions(Consumer<List<NewQuestion>> consumerList, String category) {
        mainRepository.getListOfQuestions(consumerList, category);
    }
}
