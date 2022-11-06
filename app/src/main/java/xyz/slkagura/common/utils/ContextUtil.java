package xyz.slkagura.common.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.ColorRes;

public class ContextUtil {
    private static Application sApplication;
    
    private static Resources sResources;
    
    public static void init(Application application) {
        sApplication = application;
        sResources = application.getResources();
    }
    
    public static int getColor(@ColorRes int res) {
        return sResources.getColor(res, sApplication.getTheme());
    }
    
    public static Application getApplication() {
        return sApplication;
    }
    
    public static Context getApplicationContext() {
        return sApplication.getApplicationContext();
    }
}
