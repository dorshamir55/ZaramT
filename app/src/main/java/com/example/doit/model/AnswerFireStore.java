package com.example.doit.model;

import androidx.room.Embedded;

import com.esotericsoftware.kryo.NotNull;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;


@IgnoreExtraProperties  // For Firebase deserialization
public class AnswerFireStore implements Serializable, LanguageConverter {
    public static final String TABLE_NAME = "answers";

    @NotNull
    private String id;
    @Embedded
    private AnswerLanguage en;
    @Embedded
    private AnswerLanguage he;

    public AnswerFireStore() {

    }

    public AnswerFireStore(AnswerLanguage en, AnswerLanguage he) {
        this.en = en;
        this.he = he;
    }

    public AnswerFireStore(String id, AnswerLanguage en, AnswerLanguage he) {
        this.id = id;
        this.en = en;
        this.he = he;
    }

    public <T extends AnswerFireStore> T withId(String id) {
        this.id = id;
        return (T)this;
    }

    @Override
    public String getTextByLanguage(String language) {
        switch (language){
            case "en":
                return getEn().getAnswerText();
            case "he":
                return getHe().getAnswerText();
            default:
                return getEn().getAnswerText();
        }
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

