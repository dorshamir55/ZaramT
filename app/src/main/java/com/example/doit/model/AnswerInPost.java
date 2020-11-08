package com.example.doit.model;

import androidx.room.Entity;

import java.util.List;

//@Entity(tableName = "answer_in_post")
public class AnswerInPost extends AnswerFireStore{
    private List<String> votedUserIdList;

    public AnswerInPost() {

    }

    public AnswerInPost(AnswerLanguage en, AnswerLanguage he, List<String> votedUserIdList) {
        super(en, he);
        this.votedUserIdList = votedUserIdList;
    }

    public AnswerInPost(String id, AnswerLanguage en, AnswerLanguage he, List<String> votedUserIdList) {
        super(id, en, he);
        this.votedUserIdList = votedUserIdList;
    }

    public List<String> getVotedUserIdList() {
        return votedUserIdList;
    }

    public void setVotedUserIdList(List<String> votedUserIdList) {
        this.votedUserIdList = votedUserIdList;
    }
}
