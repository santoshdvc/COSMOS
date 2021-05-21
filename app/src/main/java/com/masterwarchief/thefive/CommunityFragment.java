package com.masterwarchief.thefive;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CommunityFragment extends Fragment {
    View parent;
    CardView ask_qus;
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        parent=inflater.inflate(R.layout.fragment_community, container, false);
        QuestionModel[] qus= new QuestionModel[]{
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich.",new AnswerModel[]{new AnswerModel("Hello", "jssadfafssg")}),
        };
        ask_qus=parent.findViewById(R.id.ask_qus);
        ask_qus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog qus_dialog= new BottomSheetDialog(getContext());
                LayoutInflater layoutinflate= LayoutInflater.from(getContext());
                View bottomSheetView = layoutinflate.inflate(R.layout.ask_qus_bottomsheet, null);
                qus_dialog.setContentView(bottomSheetView);
                EditText edit_qus= bottomSheetView.findViewById(R.id.qus_tit);
                EditText edit_qusdesc= bottomSheetView.findViewById(R.id.qus_desc_box);
                Button post= bottomSheetView.findViewById(R.id.post_qus);
                Button cancel= bottomSheetView.findViewById(R.id.qus_cancel);
                post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> question = new HashMap<>();
                        question.put("qus_title", edit_qus.getText().toString());
                        question.put("qus_desc", edit_qusdesc.getText().toString());
                        Map<String, Object> nestedData = new HashMap<>();
                        nestedData.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), "Please Post Answers");
                        question.put("answers", nestedData);
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("hh_mm_dd_MM_yyyy");
                        String date_uid= FirebaseAuth.getInstance().getCurrentUser().getUid()+"_"+sdf.format(cal.getTime());
                        question.put("uid", date_uid);
                        db.collection("questions").document(date_uid)
                                .set(question)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("FireStore", "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("FireStore", "Error writing document", e);
                                    }
                                });
                        qus_dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qus_dialog.dismiss();
                    }
                });
                qus_dialog.show();
            }
        });
        RecyclerView recyclerView;
        recyclerView=parent.findViewById(R.id.home_recycler);
        QuestionAdapter questionAdapter= new QuestionAdapter(qus);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(questionAdapter);
        return parent;
    }
}