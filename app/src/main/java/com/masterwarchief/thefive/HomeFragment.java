package com.masterwarchief.thefive;

import android.Manifest;
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
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;
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
    RecyclerView.LayoutManager mLayoutManager;
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
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.PACKAGE_USAGE_STATS) ==
                        PackageManager.PERMISSION_GRANTED) {
                    populate_recycler();
                }
                else {
                    // You can directly ask for the permission.
                    requestPermissions(new String[] { Manifest.permission.PACKAGE_USAGE_STATS },167 );
                }
            }
        });

        return parent;
    }

    private void populate_recycler() {
        mUsageListAdapter = new UsageListAdapter();
        mRecyclerView = (RecyclerView) parent.findViewById(R.id.usage_recycle);
        mLayoutManager = mRecyclerView.getLayoutManager();
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

                getActivity().finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    /*
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (DataManager.getInstance().hasPermission(getContext())) {
            mSwipe.setEnabled(true);
            mSort.setVisibility(View.VISIBLE);
            mSwitch.setVisibility(View.GONE);
            initSpinner();
            initSort();
            process();
        }
    }
    */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 167:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populate_recycler();
                }  else {
                    Toast.makeText(getContext(), "Can't Enable Usage Statistics"+grantResults[0]+"/"+PackageManager.PERMISSION_GRANTED+"/"+grantResults.length, Toast.LENGTH_SHORT).show();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    //done
    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

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
            customUsageStatsList.add(customUsageStats);
        }
        mUsageListAdapter.setCustomUsageStatsList(customUsageStatsList);
        mUsageListAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
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

