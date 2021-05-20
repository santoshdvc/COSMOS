package com.masterwarchief.thefive;

public class AnswerModel {
    private String answer, question_id;
    public AnswerModel(String answer, String question_id)
    {
        this.answer=answer;
        this.question_id=question_id;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }
}
