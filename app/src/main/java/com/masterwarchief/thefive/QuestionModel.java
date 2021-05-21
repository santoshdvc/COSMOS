package com.masterwarchief.thefive;

public class QuestionModel {
    private String question, description;
    private  AnswerModel[] answers;
    public QuestionModel(String question, String description, AnswerModel[] answers){
        this.question=question;
        this.description=description;
        this.answers=answers;
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

    public AnswerModel[] getAnswers() {
        return answers;
    }

    public void setAnswers(AnswerModel[] answers) {
        this.answers = answers;
    }
}
