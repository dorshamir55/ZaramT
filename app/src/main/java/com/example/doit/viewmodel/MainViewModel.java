package com.example.doit.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
        questionPostLiveData =mainRepository.getPostsLiveData();
    }

    @Override
    public LiveData<List<QuestionPostData>> getPostsLiveData() {
        return null;
    }

    @Override
    public void loadAds(Runnable onFinish) {

    }
}
