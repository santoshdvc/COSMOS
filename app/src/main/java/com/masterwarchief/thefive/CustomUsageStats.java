package com.masterwarchief.thefive;

import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;

public class CustomUsageStats {
    public UsageStats usageStats;
    public Drawable appIcon;
    public CustomUsageStats(){}
    public CustomUsageStats(UsageStats usageStats, Drawable appIcon)
    {
        this.usageStats=usageStats;
        this.appIcon=appIcon;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public void setUsageStats(UsageStats usageStats) {
        this.usageStats = usageStats;
    }
}