package com.example.doit.model;

public class QuestionLanguage {
    private String category;
    private String questionText;

    public QuestionLanguage() {

    }

    public QuestionLanguage(String category, String question) {
        this.category = category;
        this.questionText = question;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
