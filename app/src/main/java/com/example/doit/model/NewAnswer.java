package com.example.doit.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class NewAnswer implements Serializable {
    private String id;
    private AnswerLanguage en;
    private AnswerLanguage he;

    public NewAnswer() {

    }

    public NewAnswer(AnswerLanguage en, AnswerLanguage he) {
        this.en = en;
        this.he = he;
    }

    public <T extends NewAnswer> T withId(String id) {
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

    public AnswerLanguage getEn() {
        return en;
    }

    public void setEn(AnswerLanguage en) {
        this.en = en;
    }

    public AnswerLanguage getHe() {
        return he;
    }

    public void setHe(AnswerLanguage he) {
        this.he = he;
    }
}
