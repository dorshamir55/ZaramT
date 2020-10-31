package com.example.doit.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.doit.db.AdDAO;
import com.example.doit.db.AppDatabase;
import com.example.doit.model.QuestionPostData;
import com.example.doit.remote.IMainRemoteDataSource;
import com.example.doit.remote.MainRemoteDataSource;

import java.util.Date;
import java.util.List;

public class MainRepository implements IMainRepository{

    private IMainRemoteDataSource remoteDataSource;
    private AdDAO adDAO;

    public MainRepository(Application application) {
        remoteDataSource = new MainRemoteDataSource();
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        adDAO = db.getPostDAO();
    }

    @Override
    public void loadAds(Runnable onFinish) {
        doAsynch(() -> {
            Date cacheDate = adDAO.findMaxUpdateDate();  // null if no entries
            remoteDataSource.fetchQuestionsPosts(cacheDate, result -> {
                if(result == null) {
                    if(onFinish != null)
                        onFinish.run();
                    return;
                }
                doAsynch(() -> {
                    // remove deleted posts from the cache..
                    for(int i = result.size()-1; i>=0; i--) {
                        if(result.get(i).getIsRemoved()) {
                            adDAO.deletePost(result.get(i));
                            result.remove(i);
                        }
                    }
                    //insert remaining elements..
                    adDAO.insertAll(result);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if(onFinish != null)
                            onFinish.run();
                    });
                });
            });
        });
    }

    @Override
    public LiveData<List<QuestionPostData>> getPostsLiveData () {
        return adDAO.getAllPosts();
    }

    @Override
    public void deletePost(QuestionPostData questionPostData, Runnable onFinish) {
        //doAsynch(()->adDAO.deleteAd(adoptionItemData));
        remoteDataSource.removePost(questionPostData.getId(), onFinish);
    }

    private void doAsynch(Runnable task) {
        new Thread(task).start();
    }
}