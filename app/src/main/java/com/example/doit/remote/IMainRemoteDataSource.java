package com.example.doit.remote;

import android.net.Uri;

import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.AnswerInQuestion;
import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.QuestionPostData;
import com.example.doit.model.UserData;

import java.util.Date;
import java.util.List;

public interface IMainRemoteDataSource {
    public void fetchQuestionsPosts(Date fromDate, Consumer<List<QuestionPostData>> consumer);
    public void removePost(String id, Runnable onFinish);
    public void fetchAllQuestions(Consumer<List<QuestionFireStore>> consumerList);
    public void fetchAllUsers(Consumer<List<UserData>> consumerList);
    public void fetchAnswers(Consumer<List<AnswerFireStore>> consumerList, List<AnswerInQuestion> answerInQuestions);
    public void updateVotes(String questionId, String currentUserId, List<AnswerInPost> answersInPost, List<String> votedQuestionPostsIdList, int votedPosition, Runnable onFinish);
    public void endingPostDate(String id, Runnable onFinish);
    public void changeUpdatedToFalse(String id);
    public void incrementAnswerWins(String questionID, List<String> winners);
    public void getCurrentUserData(String uid, Consumer<UserData> userConsumer);
    public void fetchAllAccountImages(Consumer<List<Uri>> uriConsumer);
    public void decrementAmountOfChosenQuestionInQuestionPost(String questionID);
    public void deleteQuestionPostIdFromUser(String questionPostID, String userID, List<String> postedQuestionPostsIdList, Runnable onFinish);
    public void decrementVotesOfVoters(String questionPostID, List<AnswerInPost> answersInPost);
}
