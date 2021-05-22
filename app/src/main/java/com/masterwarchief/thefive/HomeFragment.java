package com.masterwarchief.thefive;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.USAGE_STATS_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    View parent;
    Switch enable, usage_stats;
    UsageStatsManager mUsageStatsManager;
    UsageListAdapter mUsageListAdapter;
    RecyclerView mRecyclerView;
    TextView daily_screen;
    float total_screen;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService(USAGE_STATS_SERVICE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        daily_screen=parent.findViewById(R.id.screen_on_time);
        enable=parent.findViewById(R.id.enable_switch);
        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {


                        //If the draw over permission is not available open the settings screen
                        //to grant the permission.
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getActivity().getPackageName()));
                        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                    } else {
                        initializeView();
                    }
                }
            }
        });
        //done
        usage_stats=parent.findViewById(R.id.usage_switch);
        usage_stats.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mUsageListAdapter.getItemCount()==0 && isChecked)
                {
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.PACKAGE_USAGE_STATS) ==
                        PackageManager.PERMISSION_GRANTED) {
                    populate_recycler();
                }
                else {
                    // You can directly ask for the permission.
                    Intent intent= new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                    startActivityForResult(intent, 204);
                }}
            }
        });
        populate_recycler();
        if (mUsageListAdapter.getItemCount() != 0){
            usage_stats.setChecked(true);
        }

        return parent;
    }

    private void populate_recycler() {
        mUsageListAdapter = new UsageListAdapter();
        mRecyclerView =parent.findViewById(R.id.usage_recycle);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mUsageListAdapter);
        StatsUsageInterval statsUsageInterval = StatsUsageInterval.getValue("Daily");

        if (statsUsageInterval != null) {
            List<UsageStats> usageStatsList =
                    getUsageStatistics(statsUsageInterval.mInterval);
            Collections.sort(usageStatsList, new LastTimeLaunchedComparatorDesc());
            updateAppsList(usageStatsList);

        }


    }

    //functions
    private void initializeView() {
                getActivity().startService(new Intent(getContext(), FloatingService.class));
                getActivity().finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeView();
            } else { //Permission is not available
                Toast.makeText(getContext(),
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();
                initializeView();
            }

        }
        else if(requestCode==204){
            if (resultCode == RESULT_OK) {
                populate_recycler();
            } else { //Permission is not available
                populate_recycler();
                usage_stats.setChecked(true);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    //done
    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Toast.makeText(getContext(), ""+cal.getTimeInMillis(), Toast.LENGTH_SHORT).show();
        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, cal.getTimeInMillis(),
                        System.currentTimeMillis());
        return queryUsageStats;
    }

    /**
     * Updates the {@link #mRecyclerView} with the list of {@link UsageStats} passed as an argument.
     *
     * @param usageStatsList A list of {@link UsageStats} from which update the
     *                       {@link #mRecyclerView}.
     */
    //VisibleForTesting
    void updateAppsList(List<UsageStats> usageStatsList) {
        List<CustomUsageStats> customUsageStatsList = new ArrayList<>();
        for (int i = 0; i < usageStatsList.size(); i++) {
            CustomUsageStats customUsageStats = new CustomUsageStats();
            customUsageStats.usageStats = usageStatsList.get(i);
            try {
                Drawable appIcon = getActivity().getPackageManager()
                        .getApplicationIcon(customUsageStats.usageStats.getPackageName());
                customUsageStats.appIcon = appIcon;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("Usage", String.format("App Icon is not found for %s",
                        customUsageStats.usageStats.getPackageName()));
                customUsageStats.appIcon = getActivity()
                        .getDrawable(R.drawable.ic_launcher_foreground);
            }
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            total_screen+=customUsageStats.usageStats.getTotalTimeVisible();
            }
            customUsageStatsList.add(customUsageStats);
        }
        mUsageListAdapter.setCustomUsageStatsList(customUsageStatsList);
        mUsageListAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            float minutes = (total_screen / 1000) / 60;
            float seconds = (total_screen / 1000) % 60;
            daily_screen.setText(String.valueOf((int) minutes)+" min "+ (int)seconds+ " sec");
        }
        Log.d("Usage", "doing");
    }}

    class LastTimeLaunchedComparatorDesc implements Comparator<UsageStats> {

    @Override
    public int compare(UsageStats left, UsageStats right) {
        return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
    }
    }

/**
 * Enum represents the intervals for {@link android.app.usage.UsageStatsManager} so that
 * values for intervals can be found by a String representation.
 *
 */
//VisibleForTesting
 enum StatsUsageInterval {
    DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
    WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
    MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
    YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

    int mInterval;
    private String mStringRepresentation;

    StatsUsageInterval(String stringRepresentation, int interval) {
        mStringRepresentation = stringRepresentation;
        mInterval = interval;
    }

    static StatsUsageInterval getValue(String stringRepresentation) {
        for (StatsUsageInterval statsUsageInterval : values()) {
            if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                return statsUsageInterval;
            }
        }
        return null;
    }

}

