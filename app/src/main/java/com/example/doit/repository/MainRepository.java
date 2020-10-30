package com.example.doit.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.doit.model.QuestionPostData;
import com.example.doit.remote.IMainRemoteDataSource;
import com.example.doit.remote.MainRemoteDataSource;

import java.util.List;

public class MainRepository implements IMainRepository{

    private IMainRemoteDataSource remoteDataSource;
    //private AdDAO adDAO;


    public MainRepository(Application application) {
        remoteDataSource = new MainRemoteDataSource();
        //AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        //adDAO = db.getAdDAO();
    }

    @Override
    public void loadAds(Runnable onFinish) {

    }

    @Override
    public LiveData<List<QuestionPostData>> getPostsLiveData() {
        return null;
    }
}
