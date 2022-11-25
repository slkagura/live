package xyz.slkagura.thread;

import androidx.annotation.NonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import xyz.slkagura.common.utils.LogUtil;

public class TaskQueue {
    private static final String TASK_QUEUE_TAG = TaskQueue.class.getSimpleName();
    
    /**
     * 任务队列
     */
    private final LinkedBlockingQueue<Runnable> mQueue = new LinkedBlockingQueue<>();
    
    /**
     * 线程池
     */
    private final ExecutorService mExecutor = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, mQueue);
    
    /**
     * 同步任务组的锁
     */
    private final ConcurrentHashMap<String, TaskLock> mLocks = new ConcurrentHashMap<>();
    
    public void offer(@NonNull Runnable runnable, @NonNull String group, boolean isSync) {
        Runnable task = runnable;
        if (isSync) {
            TaskLock lock = mLocks.get(group);
            if (lock == null) {
                lock = new TaskLock();
            }
            mLocks.put(group, lock);
            final TaskLock curLock = lock;
            task = () -> {
                try {
                    if (curLock.mIsRunning) {
                        curLock.mCondition.await();
                    }
                    curLock.mLock.lock();
                    curLock.mIsRunning = true;
                    curLock.mLock.unlock();
                    runnable.run();
                } catch (Exception e) {
                    LogUtil.e(TASK_QUEUE_TAG, e.getMessage());
                    reset(curLock);
                }
            };
        }
        mExecutor.execute(task);
    }
    
    public void unlock(String group) {
        TaskLock lock = mLocks.get(group);
        if (lock == null) {
            return;
        }
        reset(lock);
    }
    
    public void release(String group) {
        TaskLock lock = mLocks.remove(group);
        if (lock == null) {
            return;
        }
        reset(lock);
    }
    
    private void reset(TaskLock lock) {
        lock.mLock.lock();
        lock.mIsRunning = false;
        lock.mCondition.signal();
        lock.mLock.unlock();
    }
}
