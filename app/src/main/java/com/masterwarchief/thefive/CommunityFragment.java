package com.masterwarchief.thefive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommunityFragment extends Fragment {
    View parent;
    public CommunityFragment() {
        // Required empty public constructor
    }

    public static CommunityFragment newInstance() {
        CommunityFragment fragment = new CommunityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent=inflater.inflate(R.layout.fragment_community, container, false);
        QuestionModel[] qus= new QuestionModel[]{
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can I go to play?","I need to play cricket."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can you turn on fan?","I am feeling very hot. The temperature is above 100F."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can I go to play?","I need to play cricket."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can I go to play?","I need to play cricket."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can I go to play?","I need to play cricket."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich.")
        };
        RecyclerView recyclerView;
        recyclerView=parent.findViewById(R.id.home_recycler);
        QuestionAdapter questionAdapter= new QuestionAdapter(qus);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(questionAdapter);
        return parent;
    }
}