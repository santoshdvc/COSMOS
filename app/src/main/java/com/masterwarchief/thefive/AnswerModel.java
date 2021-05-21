package com.masterwarchief.thefive;

public class AnswerModel {
    private String answer, user_id;
    public AnswerModel(String answer, String user_id)
    {
        this.answer=answer;
        this.user_id=user_id;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
