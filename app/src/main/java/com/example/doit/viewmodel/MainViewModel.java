package com.example.doit.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

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
import com.example.doit.model.UserData;
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
    public LiveData<List<QuestionPostData>> getMyPostsLiveData(String userID) {
        return mainRepository.getMyPostsLiveData(userID);
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
    public void getTopQuestions(Consumer<List<QuestionFireStore>> consumerList, int topQuestion) {
        mainRepository.getTopQuestions(consumerList, topQuestion);
    }

    @Override
    public void getListOfAnswers(Consumer<List<AnswerFireStore>> consumerList, List<AnswerInQuestion> answerInQuestions) {
        mainRepository.getListOfAnswers(consumerList, answerInQuestions);
    }

    @Override
    public void getAllUsers(Consumer<List<UserData>> consumerList){
        mainRepository.getAllUsers(consumerList);
    }

    @Override
    public void getUsersByIds(List<AnswerInPost> answersInPost, Consumer<List<UserData>> consumerList) {
        mainRepository.getUsersByIds(answersInPost, consumerList);
    }

    @Override
    public void voteOnPost(String id, String currentUserId, List<AnswerInPost> answersInPost, List<String> votedQuestionPostsIdList, int votedPosition) {
        mainRepository.voteOnPost(id, currentUserId, answersInPost, votedQuestionPostsIdList, votedPosition, () -> {
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
    public void incrementAnswerWins(String questionID, List<String> winners) {
        mainRepository.incrementAnswerWins(questionID, winners);
    }

    @Override
    public void getCurrentUserData(String uid, Consumer<UserData> userConsumer) {
        mainRepository.getCurrentUserData(uid, userConsumer);
    }

    @Override
    public void getAllAccountImages(Consumer<List<Uri>> uriConsumer) {
        mainRepository.getAllAccountImages(uriConsumer);
    }

    @Override
    public void decrementAmountOfChosenQuestionInQuestionPost(String questionID) {
        mainRepository.decrementAmountOfChosenQuestionInQuestionPost(questionID);
    }

    @Override
    public void deleteQuestionPostIdFromUser(String questionPostID, String userID, List<String> postedQuestionPostsIdList) {
        mainRepository.deleteQuestionPostIdFromUser(questionPostID, userID, postedQuestionPostsIdList, ()->{
            LocalBroadcastManager.getInstance(getApplication().getApplicationContext())
                    .sendBroadcast(new Intent("com.project.ACTION_RELOAD_USER"));
        });
    }

    @Override
    public void searchMyPostsAndRun(Consumer<List<QuestionPostData>> consumerList, String userID) {
        mainRepository.searchMyPostsAndRun( consumerList, userID);
    }

    @Override
    public void decrementVotesOfVoters(String questionPostID, List<AnswerInPost> answersInPost) {
        mainRepository.decrementVotesOfVoters(questionPostID, answersInPost);
    }
}
