package com.example.doit.model;

import androidx.room.Entity;

import java.util.List;

//@Entity(tableName = "answer_in_post")
public class AnswerInPost extends AnswerFireStore{
    private String answerID;
    private List<String> votedUserIdList;

    public AnswerInPost() {

    }

    public AnswerInPost(AnswerLanguage en, AnswerLanguage he, List<String> votedUserIdList) {
        super(en, he);
        this.votedUserIdList = votedUserIdList;
    }

    public AnswerInPost(String answerID, AnswerLanguage en, AnswerLanguage he, List<String> votedUserIdList) {
        super(en, he);
        this.answerID = answerID;
        this.votedUserIdList = votedUserIdList;
    }

    public String getAnswerID() {
        return answerID;
    }

    public void setAnswerID(String answerID) {
        this.answerID = answerID;
    }

    public List<String> getVotedUserIdList() {
        return votedUserIdList;
    }

    public void setVotedUserIdList(List<String> votedUserIdList) {
        this.votedUserIdList = votedUserIdList;
    }
}
