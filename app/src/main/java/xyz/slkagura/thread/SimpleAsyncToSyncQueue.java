package xyz.slkagura.thread;

import androidx.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

import xyz.slkagura.common.utils.LogUtil;

public class SimpleAsyncToSyncQueue {
    private static final String TASK_QUEUE_TAG = SimpleAsyncToSyncQueue.class.getSimpleName();
    
    private final LinkedBlockingQueue<Runnable> mQueue = new LinkedBlockingQueue<>();
    
    private final Thread mThread = new Thread(this::run);
    
    public SimpleAsyncToSyncQueue() {
        mThread.start();
    }
    
    public void offer(@NonNull Runnable runnable) {
        mQueue.offer(runnable);
    }
    
    public void unlock() {
        LockSupport.unpark(mThread);
    }
    
    private void run() {
        try {
            mQueue.take().run();
            for (; ; ) {
                LockSupport.park();
                Runnable runnable = mQueue.take();
                runnable.run();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void test() {
        SimpleAsyncToSyncQueue consumer = new SimpleAsyncToSyncQueue();
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
