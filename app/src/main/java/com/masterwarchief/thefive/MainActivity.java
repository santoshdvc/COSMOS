package com.masterwarchief.thefive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.provider.Settings;
        import android.view.View;
        import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    String titles[];
    private FirebaseUser user;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 007;
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
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());
        auth=FirebaseAuth.getInstance();
// Create and launch sign-in intent
        if(auth.getCurrentUser()==null){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        }

        titles=new String[]{"Home", "Community", "Report bugs"};
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        tabLayout=(TabLayout)findViewById(R.id.main_tab);
        viewPager2=findViewById(R.id.main_pager);
        viewPager2.setNestedScrollingEnabled(true);
        viewPager2.setAdapter(new ViewPagerFragmentAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(titles[position])).attach();
        tabLayout.setBackgroundColor(Color.WHITE);
        viewPager2.setCurrentItem(0);
        Bundle extras = getIntent().getExtras();
        int frag;

        if (extras != null) {
            frag = extras.getInt("frag");
            viewPager2.setCurrentItem(frag);
        }

    }

    /**
     * Set and initialize the view elements.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                finish();
            }
        }
    }



    class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new CommunityFragment();
                case 2:
                    return new ReportFragment();
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}