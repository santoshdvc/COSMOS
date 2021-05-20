package com.masterwarchief.thefive.app;

import android.app.Application;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import com.masterwarchief.thefive.AppConst;
import com.masterwarchief.thefive.BuildConfig;
import com.masterwarchief.thefive.data.AppItem;
import com.masterwarchief.thefive.data.DataManager;
import com.masterwarchief.thefive.db.DbHistoryExecutor;
import com.masterwarchief.thefive.db.DbIgnoreExecutor;
import com.masterwarchief.thefive.service.AppService;
import com.masterwarchief.thefive.util.CrashHandler;
import com.masterwarchief.thefive.util.PreferenceManager;

/**
 * My Application
 * Created by zb on 18/12/2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.init(this);
        getApplicationContext().startService(new Intent(getApplicationContext(), AppService.class));
        DbIgnoreExecutor.init(getApplicationContext());
        DbHistoryExecutor.init(getApplicationContext());
        DataManager.init();
        addDefaultIgnoreAppsToDB();
        if (AppConst.CRASH_TO_FILE) CrashHandler.getInstance().init();
    }

    private void addDefaultIgnoreAppsToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> mDefaults = new ArrayList<>();
                mDefaults.add("com.android.settings");
                mDefaults.add(BuildConfig.APPLICATION_ID);
                for (String packageName : mDefaults) {
                    AppItem item = new AppItem();
                    item.mPackageName = packageName;
                    item.mEventTime = System.currentTimeMillis();
                    DbIgnoreExecutor.getInstance().insertItem(item);
                }
            }
        }).run();
    }
}
