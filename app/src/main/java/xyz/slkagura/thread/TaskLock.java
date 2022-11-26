package xyz.slkagura.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TaskLock {
    /**
     * TaskQueue的锁
     */
    public final ReentrantLock mLock = new ReentrantLock(true);
    
    /**
     * TaskQueue的执行条件
     */
    public final Condition mCanRun = mLock.newCondition();
    
    /**
     * TaskQueue的结束条件
     */
    public final Condition mCanFinish = mLock.newCondition();
    
    /**
     * 正在执行同步任务
     */
    public boolean mIsRunning = false;
}
