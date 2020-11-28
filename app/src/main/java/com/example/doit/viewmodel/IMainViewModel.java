package com.example.doit.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.AnswerInQuestion;
import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.QuestionPostData;
import com.example.doit.model.UserData;

import java.util.List;

public interface IMainViewModel {
    public LiveData<List<QuestionPostData>> getPostsLiveData();
    public void loadAds(Runnable onFinish);
    public void deletePost(QuestionPostData questionPostData);
    public void getListOfQuestions(Consumer<List<QuestionFireStore>> consumerList);
    public void getListOfAnswers(Consumer<List<AnswerFireStore>> consumerList, List<AnswerInQuestion> answerInQuestions);
    public void voteOnPost(String id, String currentUserId, List<AnswerInPost> answersInPost, List<String> votedQuestionPostsIdList, int votedPosition);
    public void stopPosting(String id);
    public void incrementAnswerWins(String questionID, List<String> winners);
    public void getCurrentUserData(String uid, Consumer<UserData> userConsumer);
    public void getAllAccountImages(Consumer<List<Uri>> uriConsumer);
    public void decrementAmountOfChosenQuestionInQuestionPost(String questionID);
    public void deleteQuestionPostIdFromUser(String questionPostID, String userID, List<String> postedQuestionPostsIdList);
    public void searchMyPostsAndRun(Consumer<List<QuestionPostData>> consumerList, String userID);
}

