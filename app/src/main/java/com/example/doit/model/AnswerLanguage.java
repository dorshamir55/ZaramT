package com.example.doit.model;

import java.io.Serializable;

public class AnswerLanguage implements Serializable {
    private String answerText;

    public AnswerLanguage() {
    }

    public AnswerLanguage(String answerText) {
        this.answerText = answerText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
