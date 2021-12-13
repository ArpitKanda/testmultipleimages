package com.example.bflmultipleimages;

import android.app.Application;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App extends Application {
    private static App instance;
    private static Gson gson = null;
    private static GsonBuilder gsonBuilder = null;
    public static boolean activityVisible;

    public static synchronized App getInstance() {
        return instance;
    }

    public static  Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static GsonBuilder gsonBuilder(){
        if (gsonBuilder==null){
            gsonBuilder=new GsonBuilder();
        }
        return gsonBuilder;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

}
