package xyz.slkagura.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TaskQueue {
    private int mCapacity = 10;
    
    /**
     * 任务队列
     */
    private final LinkedBlockingQueue<Runnable> mQueue = new LinkedBlockingQueue<>();
    
    /**
     * 线程池
     */
    private final ExecutorService mExecutor = new ThreadPoolExecutor(1, 3, 0L, TimeUnit.MILLISECONDS, mQueue);
    
    /**
     * TaskQueue的锁
     */
    private final ReentrantLock mLock = new ReentrantLock();
    
    /**
     * TaskQueue的执行条件
     */
    private final Condition mQueryable = mLock.newCondition();
    
    /**
     * 正在执行同步任务
     */
    private boolean mSyncTask = false;
    
    public void post(Runnable runnable, boolean syncTask) {
        mLock.lock();
        try {
            if (mSyncTask) {
                mQueryable.await();
            }
            if (syncTask) {
                mExecutor.execute(() -> {
                    syncStart();
                    runnable.run();
                });
            } else {
                mExecutor.execute(runnable);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mLock.unlock();
        }
    }
    
    public void post(Runnable runnable) {
        post(runnable, false);
    }
    
    public void syncStart() {
        mLock.lock();
        mSyncTask = true;
        mLock.unlock();
    }
    
    public void syncComplete() {
        mLock.lock();
        mSyncTask = false;
        mQueryable.signal();
        mLock.unlock();
    }
    
    public void setCapacity(int capacity) {
        mCapacity = capacity;
    }
    
    public void close() {
        mLock.lock();
        mExecutor.execute(() -> {
            try {
                mExecutor.shutdown();
                while (!mExecutor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                }
                mLock.unlock();
            } catch (InterruptedException e) {
                mExecutor.shutdownNow();
                mLock.unlock();
                throw new RuntimeException(e);
            }
        });
    }
}
