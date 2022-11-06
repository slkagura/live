package xyz.slkagura.common.utils;

import android.app.Application;

public class SizeUtil {
    private static float sDensity;
    
    public static void init(Application application) {
        sDensity = application.getResources().getDisplayMetrics().density;
    }
    
    public static int dp2px(float dp) {
        return (int) (dp * sDensity + 0.5f);
    }
    
    public static int px2dp(float px) {
        return (int) (px / sDensity + 0.5f);
    }
}
