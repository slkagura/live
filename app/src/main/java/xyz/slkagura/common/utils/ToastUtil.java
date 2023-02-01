package xyz.slkagura.common.utils;

import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @since 2023/2/1 13:41
 */
public class ToastUtil {
    private static final String TOAST_UTIL = ToastUtil.class.getSimpleName();
    
    private static final ReentrantLock LOCK = new ReentrantLock(true);
    
    private static WeakReference<Toast> sLast;
    
    public static void show(@NonNull String text, boolean length) {
        if (text.isEmpty()) {
            return;
        }
        LOCK.lock();
        cancel();
        Toast last = Toast.makeText(ContextUtil.getApplication(), text, length ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        sLast = new WeakReference<>(last);
        last.show();
        LOCK.unlock();
    }
    
    public static void cancel() {
        if (sLast == null) {
            return;
        }
        LOCK.lock();
        Toast last = sLast.get();
        if (last != null) {
            last.cancel();
        }
        sLast = null;
        LOCK.unlock();
    }
}
