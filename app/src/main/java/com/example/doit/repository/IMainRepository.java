package com.example.doit.repository;

import androidx.lifecycle.LiveData;

import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.AnswerInQuestion;
import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.QuestionPostData;

import java.util.List;

public interface IMainRepository {
    public void loadAds(Runnable onFinish);
    public LiveData<List<QuestionPostData>> getPostsLiveData();
    public void deletePost(QuestionPostData questionPostData, Runnable onFinish);
    public void getListOfQuestions(Consumer<List<QuestionFireStore>> consumerList);
    public void getListOfAnswers(Consumer<List<AnswerFireStore>> consumerList, List<AnswerInQuestion> answerInQuestions);
    public void voteOnPost(String id, String currentUserId, List<AnswerInPost> answersInPost, int votedPosition, Runnable onFinish);
}
