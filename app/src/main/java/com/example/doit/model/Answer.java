package com.example.doit.model;

public class Answer {
    private String answerID;
    private String answerText;

    public Answer() {

    }

    public Answer(String answerID, String answerText) {
        this.answerID = answerID;
        this.answerText = answerText;
    }

    public String getAnswerID() {
        return answerID;
    }

    public void setAnswerID(String answerID) {
        this.answerID = answerID;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
