package xyz.slkagura.common.utils;

import android.util.Log;

import xyz.slkagura.common.Level;

public class LogUtil {
    private static final boolean LOG_ENABLE = true;
    
    @Level
    private static int sLevel = Level.Debug;
    
    public static void setLevel(@Level int level) {
        sLevel = level;
    }
    
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
    
    private static void log(@Level int level, String tag, Object... messages) {
        if (!LOG_ENABLE) {
            return;
        }
        if (sLevel < level) {
            return;
        }
        String message;
        if (messages.length > 1) {
            message = build(messages);
        } else {
            message = (String) messages[0];
        }
        if (message != null && message.length() > 0) {
            if (level == Level.Verbose) {
                Log.v(tag, message);
            } else if (level == Level.Debug) {
                Log.d(tag, message);
            } else if (level == Level.Info) {
                Log.i(tag, message);
            } else if (level == Level.Warn) {
                Log.w(tag, message);
            } else if (level == Level.Error) {
                Log.e(tag, message);
            }
        }
    }
    
    public static void v(String tag, Object... messages) {
        log(Level.Verbose, tag, messages);
    }
    
    public static void d(String tag, Object... messages) {
        log(Level.Debug, tag, messages);
    }
    
    public static void i(String tag, Object... messages) {
        log(Level.Info, tag, messages);
    }
    
    public static void w(String tag, Object... messages) {
        log(Level.Warn, tag, messages);
    }
    
    public static void e(String tag, Object... messages) {
        log(Level.Error, tag, messages);
    }
}
