package com.masterwarchief.thefive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.provider.Settings;
        import android.view.View;
        import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        /*
        RecyclerView recyclerView= findViewById(R.id.try_box);
        QuestionModel[] qus= new QuestionModel[]{
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can I go to play?","I need to play cricket."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can you turn on fan?","I am feeling very hot. The temperature is above 100F."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich."),
                new QuestionModel("Can I have a standwich?","I need a sandwich not standwich.")
        };
        QuestionAdapter questionAdapter= new QuestionAdapter(qus);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(questionAdapter);

        */
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }
    }

    /**
     * Set and initialize the view elements.
     */
    private void initializeView() {
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, FloatingService.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeView();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}