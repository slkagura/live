package xyz.slkagura.thread;

import androidx.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

public class TaskQueue {
    private static final String TASK_QUEUE_TAG = TaskQueue.class.getSimpleName();
    
    private final LinkedBlockingQueue<Runnable> mQueue = new LinkedBlockingQueue<>();
    
    private final Thread mThread = new Thread(this::run);
    
    public TaskQueue() {
        mThread.start();
    }
    
    public void offer(@NonNull Runnable runnable) {
        mQueue.offer(runnable);
    }
    
    public void unlock() {
        LockSupport.unpark(mThread);
    }
    
    public void run() {
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
}
