package com.example.doit.remote;

import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.AnswerInQuestion;
import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.QuestionPostData;

import java.util.Date;
import java.util.List;

public interface IMainRemoteDataSource {
    public void fetchQuestionsPosts(Date fromDate, Consumer<List<QuestionPostData>> consumer);
    public void removePost(String id, Runnable onFinish);
    public void fetchAllQuestions(Consumer<List<QuestionFireStore>> consumerList);
    public void fetchAnswers(Consumer<List<AnswerFireStore>> consumerList, List<AnswerInQuestion> answerInQuestions);
    public void updateVotes(String id, String currentUserId, List<AnswerInPost> answersInPost, int votedPosition, Runnable onFinish);
    public void endingPostDate(String id, Runnable onFinish);
    public void changeUpdatedToFalse(String id);
    public void incrementAnswerWins(List<String> winners);
}
