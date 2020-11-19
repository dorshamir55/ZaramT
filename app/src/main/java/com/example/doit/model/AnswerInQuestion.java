package com.example.doit.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class AnswerInQuestion implements Serializable {
    private String answerID;
    private long amountOfWins;

    public AnswerInQuestion() {
        this.amountOfWins = 0;
    }

//    public <T extends AnswerInQuestion> T withId(String id) {
//        this.id = id;
//        return (T)this;
//    }

    public String getAnswerID() {
        return answerID;
    }

    public void setAnswerID(String answerID) {
        this.answerID = answerID;
    }

    public long getAmountOfWins() {
        return amountOfWins;
    }

    public void setAmountOfWins(long amountOfWins) {
        this.amountOfWins = amountOfWins;
    }
}
