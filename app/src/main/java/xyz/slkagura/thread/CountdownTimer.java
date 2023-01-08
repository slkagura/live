package xyz.slkagura.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import xyz.slkagura.common.interfaces.PCallback;

public class CountdownTimer {
    private final Thread mThread;
    
    private final long mDelay;
    
    private final long mPeriod;
    
    private final AtomicBoolean mIsRunning;
    
    private final AtomicBoolean mIsPause;
    
    private final int mInitCount;
    
    private final AtomicInteger mCountdown;
    
    private final PCallback<CountdownTimer> mCallback;
    
    public CountdownTimer(PCallback<CountdownTimer> callback, long delay, long period, int count, TimeUnit unit) {
        mThread = new Thread(this::run);
        mDelay = unit.toNanos(delay);
        mPeriod = unit.toNanos(period);
        mIsRunning = new AtomicBoolean(true);
        mIsPause = new AtomicBoolean(false);
        mInitCount = count;
        mCountdown = new AtomicInteger(mInitCount);
        mCallback = callback;
    }
    
    private void run() {
        if (mDelay > 0) {
            LockSupport.parkNanos(mThread, mDelay);
        }
        while (mIsRunning.get()) {
            if (mIsPause.get()) {
                LockSupport.park(mThread);
            }
            if (mCountdown.decrementAndGet() <= 0) {
                mCallback.callback(this);
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
    
    public void resume() {
        if (mIsPause.get()) {
            LockSupport.unpark(mThread);
        }
    }
    
    public void pause() {
        mIsPause.compareAndSet(false, true);
    }
    
    public void recount() {
        mCountdown.set(mInitCount);
    }
}
