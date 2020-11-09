package com.example.doit.model;

import androidx.room.Embedded;

import com.esotericsoftware.kryo.NotNull;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties  // For Firebase deserialization
public class QuestionInPost implements LanguageConverter {

    @NotNull
    private String questionID;
    @Embedded(prefix = "english_")
    private QuestionLanguage en;
    @Embedded(prefix = "hebrew_")
    private QuestionLanguage he;

    public QuestionInPost() {

    }

    public QuestionInPost(String questionID, QuestionLanguage en, QuestionLanguage he) {
        this.questionID = questionID;
        this.en = en;
        this.he = he;
    }

    @Override
    public String getTextByLanguage(String language) {
        switch (language){
            case "en":
                return getEn().getQuestionText();
            case "he":
                return getHe().getQuestionText();
            default:
                return getEn().getQuestionText();
        }
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
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
