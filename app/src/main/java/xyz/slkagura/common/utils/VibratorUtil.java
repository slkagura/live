package xyz.slkagura.common.utils;

import android.app.Application;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

public class VibratorUtil {
    private static final String VIBRATOR_UTIL = VibratorUtil.class.getSimpleName();
    
    private static final VibrationEffect EFFECT = VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE);
    
    private static VibratorManager sManager;
    
    private static Vibrator sDefaultVibrator;
    
    private static VibratorManager getManager() {
        if (sManager == null) {
            Application app = ContextUtil.getApplication();
            if (app != null) {
                sManager = ((VibratorManager) app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE));
            }
        }
        return sManager;
    }
    
    private static Vibrator getVibrator() {
        if (sDefaultVibrator == null) {
            sDefaultVibrator = getManager().getDefaultVibrator();
        }
        return sDefaultVibrator;
    }
    
    public static void vibrate(VibrationEffect effect) {
        getVibrator().vibrate(effect);
    }
    
    public static void vibrate(long time) {
        VibrationEffect effect = VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE);
        vibrate(effect);
    }
    
    public static void vibrate() {
        vibrate(EFFECT);
    }
}
