package xyz.slkagura.thread;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

import xyz.slkagura.common.utils.LogUtil;

public class SimpleAsyncToSyncQueue {
    private static final String TASK_QUEUE_TAG = SimpleAsyncToSyncQueue.class.getSimpleName();
    
    private final LinkedBlockingQueue<Runnable> mQueue = new LinkedBlockingQueue<>();
    
    private final Thread mThread = new Thread(this::run);
    
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    
    private boolean mIsAuto = false;
    
    public void start() {
        if (!mThread.isAlive()) {
            mThread.start();
        }
    }
    
    public void stop() {
        if (mThread.isAlive() && !mThread.isInterrupted()) {
            mThread.interrupt();
        }
    }
    
    public void setAuto(boolean isAuto) {
        mIsAuto = isAuto;
    }
    
    private void run() {
        try {
            if (mIsAuto) {
                mQueue.take().run();
            }
            while (!mThread.isInterrupted()) {
                LockSupport.park();
                Runnable runnable = mQueue.take();
                runnable.run();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void offer(@NonNull Runnable runnable, boolean isMain) {
        if (isMain) {
            mQueue.offer(() -> {
                mHandler.post(runnable);
            });
        } else {
            mQueue.offer(runnable);
        }
    }
    
    public void offer(@NonNull Runnable runnable) {
        offer(runnable, false);
    }
    
    public void unlock() {
        LockSupport.unpark(mThread);
    }
    
    public static void test() {
        SimpleAsyncToSyncQueue consumer = new SimpleAsyncToSyncQueue();
        consumer.setAuto(true);
        consumer.start();
        for (int i = 0; i < 100; i++) {
            final int id = i;
            boolean isSync = Math.random() < 0.9D;
            String groupId = isSync ? "group-1" : "group-2";
            consumer.offer(() -> {
                LogUtil.d(TASK_QUEUE_TAG, "task: ", id, " group: ", groupId, " sync: ", String.valueOf(isSync), " start: ", System.nanoTime());
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    consumer.unlock();
                    LogUtil.d(TASK_QUEUE_TAG, "task: ", id, " group: ", groupId, " sync: ", String.valueOf(isSync), " unlock: ", System.nanoTime());
                }).start();
                LogUtil.d(TASK_QUEUE_TAG, "task: ", id, " group: ", groupId, " sync: ", String.valueOf(isSync), " end: ", System.nanoTime());
            });
        }
    }
}
