package com.masterwarchief.thefive;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {
    View parent;
    CardView bug_report;
    String appName;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public ReportFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_report, container, false);
        // Inflate the layout for this fragment
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        bug_report= parent.findViewById(R.id.add_bug);
        bug_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PackageManager pm = getActivity().getPackageManager();
                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                List<String> appNames= new ArrayList<>();
                for (ApplicationInfo packageInfo : packages) {
                    appNames.add(packageInfo.packageName);
                }
                BottomSheetDialog bug_dialog= new BottomSheetDialog(getContext());
                LayoutInflater layoutinflate= LayoutInflater.from(getContext());
                View bottomSheetView = layoutinflate.inflate(R.layout.report_bug_bottomsheet, null);
                bug_dialog.setContentView(bottomSheetView);
                EditText edit_bug= bottomSheetView.findViewById(R.id.bug_tit);
                EditText edit_bugdesc= bottomSheetView.findViewById(R.id.bug_desc_box);
                Button post= bottomSheetView.findViewById(R.id.bug_qus);
                Button cancel= bottomSheetView.findViewById(R.id.bug_cancel);
                Spinner spinner_1 = bottomSheetView.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, appNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_1.setAdapter(adapter);
                spinner_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int position, long id) {
                        appName = adapterView.getItemAtPosition(position).toString();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        // TODO Auto-generated method stub

                    }
                });
                post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                Map<String, Object> bug = new HashMap<>();
                bug.put("bug_title", edit_bug.getText().toString());
                bug.put("bug_desc", edit_bugdesc.getText().toString());
                bug.put("appName", appName);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("hh_mm_dd_MM_yyyy");
                String date_uid= FirebaseAuth.getInstance().getCurrentUser().getUid()+"_"+sdf.format(cal.getTime());
                bug.put("uid", date_uid);
                db.collection("bug_reports").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("bugs")
                        .document(date_uid)
                        .set(bug)
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
                bug_dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bug_dialog.dismiss();
            }
        });
        bug_dialog.show();
            }
        });
        RecyclerView bug_recycler= parent.findViewById(R.id.bug_report_recycler);
        bug_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return parent;
    }
}