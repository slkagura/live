package xyz.slkagura.common.utils;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

import java.util.concurrent.locks.ReentrantLock;

public class VibratorUtil {
    private static final String VIBRATOR_UTIL = VibratorUtil.class.getSimpleName();
    
    private static final ReentrantLock LOCK = new ReentrantLock();
    
    private static final VibrationEffect EFFECT = VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE);
    
    private static VibratorManager sManager;
    
    private static Vibrator sDefaultVibrator;
    
    private static VibratorManager getManager() {
        LOCK.lock();
        if (sManager == null) {
            sManager = ((VibratorManager) ContextUtil.getApplication().getSystemService(Context.VIBRATOR_MANAGER_SERVICE));
        }
        LOCK.unlock();
        return sManager;
    }
    
    private static Vibrator getVibrator() {
        LOCK.lock();
        if (sDefaultVibrator == null) {
            sDefaultVibrator = getManager().getDefaultVibrator();
        }
        LOCK.unlock();
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
