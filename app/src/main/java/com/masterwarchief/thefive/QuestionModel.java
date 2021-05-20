package com.masterwarchief.thefive;

public class QuestionModel {
    private String question, description;
    public QuestionModel(String question, String description){
        this.question=question;
        this.description=description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}
