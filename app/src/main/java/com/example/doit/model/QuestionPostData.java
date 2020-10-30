package com.example.doit.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.doit.db.Converters;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

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
    private Question question;

    @Embedded
    private List<Answer> answers;

    @ServerTimestamp
    private Date updateDate;  // update (also created) date - from Firebase

    private boolean isRemoved;

    public QuestionPostData(String postedUserId, Question question, List<Answer> answers, Date updateDate, boolean isRemoved) {
        this.postedUserId = postedUserId;
        this.question = question;
        this.answers = answers;
        this.updateDate = updateDate;
        this.isRemoved = isRemoved;
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }
}
