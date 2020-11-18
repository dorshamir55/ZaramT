package com.example.doit.viewmodel;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.AnswerInQuestion;
import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionFireStore;
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
    public void getListOfQuestions(Consumer<List<QuestionFireStore>> consumerList) {
        mainRepository.getListOfQuestions(consumerList);
    }

    @Override
    public void getListOfAnswers(Consumer<List<AnswerFireStore>> consumerList, List<AnswerInQuestion> answerInQuestions) {
        mainRepository.getListOfAnswers(consumerList, answerInQuestions);
    }

    @Override
    public void voteOnPost(String id, String currentUserId, List<AnswerInPost> answersInPost, int votedPosition) {
        mainRepository.voteOnPost(id, currentUserId, answersInPost, votedPosition, () -> {
            LocalBroadcastManager.getInstance(getApplication().getApplicationContext())
                    .sendBroadcast(new Intent("com.project.ACTION_RELOAD"));
        });
    }

    @Override
    public void stopPosting(String id) {
        mainRepository.stopPosting(id, () -> {
            LocalBroadcastManager.getInstance(getApplication().getApplicationContext())
                    .sendBroadcast(new Intent("com.project.ACTION_RELOAD"));
        });
    }

    @Override
    public void incrementAnswerWins(List<String> winners) {
        mainRepository.incrementAnswerWins(winners);
    }
}
