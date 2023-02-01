package xyz.slkagura.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import xyz.slkagura.common.interfaces.PCallback;

public class CountTimer {
    private final Thread mThread;
    
    private final long mDelay;
    
    private final long mPeriod;
    
    private final int mCount;
    
    private final AtomicBoolean mIsRunning;
    
    private final AtomicBoolean mIsPause;
    
    private final AtomicInteger mCountdown;
    
    private final PCallback<CountTimer> mEndCallback;
    
    private PCallback<CountTimer> mResetCallback;
    
    public CountTimer(PCallback<CountTimer> callback, long delay, long period, int count, TimeUnit unit) {
        mThread = new Thread(this::run);
        mDelay = unit.toNanos(delay);
        mPeriod = unit.toNanos(period);
        mIsRunning = new AtomicBoolean(true);
        mIsPause = new AtomicBoolean(false);
        mCount = count;
        mCountdown = new AtomicInteger(mCount);
        mEndCallback = callback;
    }
    
    public void setResetCallback(PCallback<CountTimer> resetCallback) {
        mResetCallback = resetCallback;
    }
    
    private void run() {
        if (mDelay > 0) {
            LockSupport.parkNanos(mThread, mDelay);
        }
        while (mIsRunning.get()) {
            if (mIsPause.get()) {
                LockSupport.park(mThread);
            }
            LockSupport.parkNanos(mThread, mPeriod);
            if (mCountdown.decrementAndGet() <= 0) {
                mEndCallback.call(this);
                LockSupport.park(mThread);
            }
        }
    }
    
    public void start() {
        if (mThread.isAlive() || mThread.isInterrupted()) {
            return;
        }
        mThread.start();
    }
    
    public void stop() {
        mIsRunning.compareAndSet(true, false);
    }
    
    public void pause() {
        mIsPause.compareAndSet(false, true);
    }
    
    public void resume() {
        if (mIsPause.get()) {
            LockSupport.unpark(mThread);
        }
    }
    
    public void reset() {
        mCountdown.set(mCount);
    }
}
