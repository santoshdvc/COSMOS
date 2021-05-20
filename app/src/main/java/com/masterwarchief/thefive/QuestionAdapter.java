package com.masterwarchief.thefive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>{
    private QuestionModel[] questions;
    public QuestionAdapter(QuestionModel[] questions){
        this.questions=questions;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.qus_desc.setText(questions[position].getDescription());
        holder.question_box.setText(questions[position].getQuestion());
    }

    @Override
    public int getItemCount() {
        return questions.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView question_box, qus_desc;
        public ViewHolder(View itemView){
            super(itemView);
            this.question_box=itemView.findViewById(R.id.question_text);
            this.qus_desc=itemView.findViewById(R.id.question_desc_box);
        }
    }
}
