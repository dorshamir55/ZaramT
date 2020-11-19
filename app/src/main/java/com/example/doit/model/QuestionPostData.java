package com.example.doit.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.doit.db.Converters;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties  // For Firebase deserialization
@Entity(tableName = QuestionPostData.TABLE_NAME)
public class QuestionPostData {
    public static final String TABLE_NAME = "posts";

    @PrimaryKey
    @NonNull
    private String id; // Document id (EXCLUDED)

    private String postedUserId; // User document id which is also the authentication id.

    @Embedded
    private QuestionInPost question;

    private List<AnswerInPost> answers;

    @ServerTimestamp
    private Date startDate;  // update (also created) date - from Firebase

    @ServerTimestamp
    private Date updateDate;  // update (also created) date - from Firebase

    private Date endingPostDate;  // ending post date

    private boolean isRemoved;

    private boolean isVoted;

    private boolean isPostTimeOver;

    public QuestionPostData() {
    }

    public QuestionPostData(String postedUserId, QuestionInPost question, List<AnswerInPost> answers, Date endingPostDate) {
        this.postedUserId = postedUserId;
        this.question = question;
        this.answers = answers;
        this.isRemoved = false;
        this.isVoted = false;
        this.isPostTimeOver = false;
        this.endingPostDate = endingPostDate;
    }

    public <T extends QuestionPostData> T withId(String id) {
        this.id = id;
        return (T)this;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getPostedUserId() {
        return postedUserId;
    }

    public void setPostedUserId(String postedUserId) {
        this.postedUserId = postedUserId;
    }

    public QuestionInPost getQuestion() {
        return question;
    }

    public void setQuestion(QuestionInPost question) {
        this.question = question;
    }

    public List<AnswerInPost> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerInPost> answers) {
        this.answers = answers;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getEndingPostDate() {
        return endingPostDate;
    }

    public void setEndingPostDate(Date endingPostDate) {
        this.endingPostDate = endingPostDate;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    public boolean isVoted() {
        return isVoted;
    }

    public void setVoted(boolean voted) {
        isVoted = voted;
    }

    public boolean isPostTimeOver() {
        return isPostTimeOver;
    }

    public void setPostTimeOver(boolean postTimeOver) {
        isPostTimeOver = postTimeOver;
    }

    public List<String> calculateWinningAnswerID() {
        List<String> winners = new ArrayList<>();
        int max = 0;
        for(AnswerInPost answerInPost : answers){
            if(max < answerInPost.getVotedUserIdList().size()){
                max = answerInPost.getVotedUserIdList().size();
                winners.clear();
                winners.add(answerInPost.getAnswerID());
            }
            else if(max == answerInPost.getVotedUserIdList().size()){
                winners.add(answerInPost.getAnswerID());
            }
        }

        return winners;
    }
}
