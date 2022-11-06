package xyz.slkagura.common.utils;

import android.util.Log;

public class LogUtil {
    private static final boolean sEnable = true;
    
    private static String build(Object... msg) {
        if (msg.length < 1) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : msg) {
            sb.append(o);
        }
        String log = sb.toString();
        sb.delete(0, sb.length());
        return log;
    }
    
    public static void d(String tag, Object... msg) {
        if (sEnable && msg.length > 0) {
            Log.d(tag, build(msg));
        }
    }
    
    public static void i(String tag, Object... msg) {
        if (sEnable && msg.length > 0) {
            Log.i(tag, build(msg));
        }
    }
    
    public static void w(String tag, Object... msg) {
        if (sEnable && msg.length > 0) {
            Log.w(tag, build(msg));
        }
    }
    
    public static void e(String tag, Object... msg) {
        if (sEnable && msg.length > 0) {
            Log.e(tag, build(msg));
        }
    }
    
    public static void d(String tag, String msg) {
        if (sEnable) {
            Log.d(tag, msg);
        }
    }
    
    public static void i(String tag, String msg) {
        if (sEnable) {
            Log.i(tag, msg);
        }
    }
    
    public static void w(String tag, String msg) {
        if (sEnable) {
            Log.w(tag, msg);
        }
    }
    
    public static void e(String tag, String msg) {
        if (sEnable) {
            Log.e(tag, msg);
        }
    }
}
