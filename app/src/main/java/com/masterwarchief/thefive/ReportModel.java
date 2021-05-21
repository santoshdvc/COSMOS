package com.masterwarchief.thefive;

public class ReportModel {
    String appName, bug_desc, bug_title, uid;
    public ReportModel(){}
    public ReportModel(String appName, String bug_desc, String bug_title,String uid){
        this.appName=appName;
        this.bug_desc=bug_desc;
        this.bug_title=bug_title;
        this.uid=uid;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBug_desc() {
        return bug_desc;
    }

    public void setBug_desc(String bug_desc) {
        this.bug_desc = bug_desc;
    }

    public String getBug_title() {
        return bug_title;
    }

    public void setBug_title(String bug_title) {
        this.bug_title = bug_title;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
