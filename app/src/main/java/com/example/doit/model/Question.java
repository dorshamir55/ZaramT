package com.example.doit.model;

public class Question {
    private String questionID;
    private String questionText;

    public Question() {

    }

    public Question(String questionID, String questionText) {
        this.questionID = questionID;
        this.questionText = questionText;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
