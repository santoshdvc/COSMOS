package com.masterwarchief.thefive.service;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.masterwarchief.thefive.data.AppItem;
import com.masterwarchief.thefive.data.DataManager;
import com.masterwarchief.thefive.data.HistoryItem;
import com.masterwarchief.thefive.db.DbHistoryExecutor;
import com.masterwarchief.thefive.log.FileLogManager;
import com.masterwarchief.thefive.util.AlarmUtil;
import com.masterwarchief.thefive.util.AppUtil;

/**
 * Alarm service
 * Created by zb on 02/01/2018.
 */

public class AlarmService extends IntentService {

    private static final String ALARM_SERVICE_NAME = "alarm.service";

    public AlarmService() {
        super(ALARM_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        DataManager manager = DataManager.getInstance();
        List<AppItem> items = manager.getApps(this.getApplicationContext(), 0, 1);
        for (AppItem item : items) {
            HistoryItem historyItem = new HistoryItem();
            historyItem.mName = item.mName;
            historyItem.mPackageName = item.mPackageName;
            historyItem.mMobileTraffic = item.mMobile;
            historyItem.mIsSystem = AppUtil.isSystemApp(getPackageManager(), item.mPackageName) ? 1 : 0;
            historyItem.mDuration = item.mUsageTime;
            historyItem.mTimeStamp = AppUtil.getYesterdayTimestamp();
            historyItem.mDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(historyItem.mTimeStamp));
            DbHistoryExecutor.getInstance().insert(historyItem);
        }

        FileLogManager fileLogManager = FileLogManager.getInstance();
        fileLogManager.log("alarm " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis())) + "\n");

        AlarmUtil.setAlarm(this.getApplicationContext());
    }
}
