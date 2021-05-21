package com.masterwarchief.thefive;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class FloatingService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    TextView comm_click;
    CardView perf_metric, report_btn, microphone;
    LinearLayout card_perf, card_comm;
    Button stop_button;
    private FirestoreRecyclerAdapter<QuestionModel, FloatingService.QuestionViewHolder> adapter;
    public FloatingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_screen, null);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        stop_button=mFloatingView.findViewById(R.id.button2);
        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.stopListening();
                stopSelf();
            }
        });
        microphone=mFloatingView.findViewById(R.id.open_assitant);
        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getApplicationContext().getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage("com.google.android.apps.googleassistant");
                if (launchIntent != null) {
                    getApplicationContext().startActivity(launchIntent);
                } else {
                    Toast.makeText(FloatingService.this, "No Assistant App found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);


        //Set the close button
        CardView closeButton = (CardView) mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });
        report_btn=mFloatingView.findViewById(R.id.report_btn);
        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("frag",2);
                expandedView.setVisibility(View.GONE);
                startActivity(intent);
            }
        });
        card_perf=(LinearLayout)mFloatingView.findViewById(R.id.perf_drop);
        perf_metric= (CardView) mFloatingView.findViewById(R.id.perf_card);
        perf_metric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(card_perf.getVisibility()==View.VISIBLE){
                    card_perf.setVisibility(View.GONE);
                }
                else {
                    card_perf.setVisibility(View.VISIBLE);
                }
            }
        });
        comm_click=(TextView)mFloatingView.findViewById(R.id.textV);
        card_comm=(LinearLayout)mFloatingView.findViewById(R.id.comm_drop);
        comm_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(card_comm.getVisibility()==View.VISIBLE){
                    card_comm.setVisibility(View.GONE);
                }
                else{
                    card_comm.setVisibility(View.VISIBLE);
                }
            }
        });
        //start_listener
        RecyclerView recyclerView;
        recyclerView=mFloatingView.findViewById(R.id.float_comm);
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("questions").orderBy("qus_title").limitToLast(10);
        FirestoreRecyclerOptions<QuestionModel> options = new FirestoreRecyclerOptions.Builder<QuestionModel>()
                .setQuery(query, QuestionModel.class)
                .build();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new FirestoreRecyclerAdapter<QuestionModel, FloatingService.QuestionViewHolder>(options) {
            @NonNull
            @Override
            public FloatingService.QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_element, parent, false);
                return new FloatingService.QuestionViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FloatingService.QuestionViewHolder holder, int position, @NonNull QuestionModel model) {
                holder.setQuestion(model.getQus_title());
                holder.setDescription(model.getQus_desc());
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.VISIBLE);
                                expandedView.setVisibility(View.VISIBLE);

                            }
                            else{
                                expandedView.setVisibility(View.GONE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }


    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);

    }
    private class QuestionViewHolder extends RecyclerView.ViewHolder {
        private View view;

        QuestionViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setQuestion(String qus_title) {
            TextView textView = view.findViewById(R.id.question_text);
            textView.setText(qus_title);
        }
        void setDescription(String qus_desc){
            TextView textView = view.findViewById(R.id.question_desc_box);
            textView.setText(qus_desc);
        }
    }
}