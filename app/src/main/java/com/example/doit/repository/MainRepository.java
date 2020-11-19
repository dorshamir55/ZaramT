package com.example.doit.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.doit.db.AdDAO;
import com.example.doit.db.AppDatabase;
import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.AnswerInQuestion;
import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionFireStore;
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
                        if(result.get(i).isRemoved()) {
                            adDAO.deletePost(result.get(i));
                            result.remove(i);
                            //Delete from FireStore if needed.
                        }
                        else if(result.get(i).isVoted()) {
                            remoteDataSource.changeUpdatedToFalse(result.get(i).getId());
                            result.get(i).setVoted(false);
                            adDAO.deletePost(result.get(i));
                            //Delete from Room & Insert later the updated.
                        }
                        else if(result.get(i).isPostTimeOver()){
                            //adDAO.deletePost(result.get(i));
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
        //doAsynch(()->adDAO.deleteAd(questionPostData));
        remoteDataSource.removePost(questionPostData.getId(), onFinish);
    }

    @Override
    public void getListOfQuestions(Consumer<List<QuestionFireStore>> consumerList) {
        remoteDataSource.fetchAllQuestions(consumerList);
    }

    @Override
    public void getListOfAnswers(Consumer<List<AnswerFireStore>> consumerList, List<AnswerInQuestion> answerInQuestions) {
        remoteDataSource.fetchAnswers(consumerList, answerInQuestions);
    }

    @Override
    public void voteOnPost(String id ,String currentUserId, List<AnswerInPost> answersInPost, int votedPosition, Runnable onFinish) {
        remoteDataSource.updateVotes(id, currentUserId, answersInPost, votedPosition, onFinish);
    }

    @Override
    public void stopPosting(String id, Runnable onFinish) {
        remoteDataSource.endingPostDate(id, onFinish);
    }

    @Override
    public void incrementAnswerWins(String questionID, List<String> winners) {
        remoteDataSource.incrementAnswerWins(questionID, winners);
    }

    private void doAsynch(Runnable task) {
        new Thread(task).start();
    }
}