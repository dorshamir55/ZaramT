package com.example.doit.model;

public class Question {
    private int questionID;
    private String question;

    public Question(int questionID, String question) {
        this.questionID = questionID;
        this.question = question;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
