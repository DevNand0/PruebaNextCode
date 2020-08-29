package com.example.nextcode.conn;

import android.app.Application;
import android.content.Context;

public class ApplicationConn extends Application {
    private static ApplicationConn instance;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        this.setAppContext(getApplicationContext());
    }

    public void setAppContext(Context c){
        appContext=c;
    }

    public static ApplicationConn getInstance(){
        return instance;
    }
    public static Context getAppContext(){
        return appContext;
    }
}
