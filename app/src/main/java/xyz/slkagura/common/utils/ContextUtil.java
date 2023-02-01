package xyz.slkagura.common.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

public class ContextUtil {
    private static Application sApplication;
    
    private static Resources sResources;
    
    public static void init(@NonNull Application application) {
        sApplication = application;
        sResources = application.getResources();
    }
    
    public static int getColor(@ColorRes int res) {
        return sResources.getColor(res, sApplication.getTheme());
    }
    
    @NonNull
    public static Application getApplication() {
        if (sApplication == null) {
            throw new RuntimeException("ContextUtil Not Initialized");
        }
        return sApplication;
    }
    
    public static Context getApplicationContext() {
        return sApplication.getApplicationContext();
    }
}
