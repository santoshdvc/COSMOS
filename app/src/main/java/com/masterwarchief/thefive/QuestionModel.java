package com.masterwarchief.thefive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionModel {
    private String qus_title, qus_desc;
    private Map<String, Object> answers;
    public QuestionModel(){}
    public QuestionModel(String question, String description, Map<String, Object> answers){
        this.qus_title=question;
        this.qus_desc=description;
        this.answers=answers;
    }

    public String getQus_desc() {
        return qus_desc;
    }

    public void setQus_desc(String description) {
        this.qus_desc = description;
    }

    public void setQus_title(String question) {
        this.qus_title = question;
    }

    public String getQus_title() {
        return qus_title;
    }

    public Map<String, Object> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Object> answers) {
        this.answers = answers;
    }
}
