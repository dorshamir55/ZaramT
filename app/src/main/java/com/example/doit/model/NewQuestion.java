package com.example.doit.model;

import com.esotericsoftware.kryo.NotNull;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties  // For Firebase deserialization
public class NewQuestion {
    public static final String TABLE_NAME = "questions";

    @NotNull
    private String id;
    private QuestionLanguage en;
    private QuestionLanguage he;
    //private List<Answer> answers;

    public NewQuestion() {

    }

    public NewQuestion(QuestionLanguage en, QuestionLanguage he) {
        this.he = he;
        this.en = en;
    }

    public <T extends NewQuestion> T withId(String id) {
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

    public QuestionLanguage getEn() {
        return en;
    }

    public void setEn(QuestionLanguage en) {
        this.en = en;
    }

    public QuestionLanguage getHe() {
        return he;
    }

    public void setHe(QuestionLanguage he) {
        this.he = he;
    }
}
