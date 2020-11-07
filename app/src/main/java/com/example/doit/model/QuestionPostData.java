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

    private List<AnswerFireStore> answers;

    @ServerTimestamp
    private Date updateDate;  // update (also created) date - from Firebase

    private boolean isRemoved;

    public QuestionPostData() {
    }

    public QuestionPostData(String postedUserId, QuestionInPost question, List<AnswerFireStore> answers) {
        this.postedUserId = postedUserId;
        this.question = question;
        this.answers = answers;
        this.isRemoved = false;
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

    public List<AnswerFireStore> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerFireStore> answers) {
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
