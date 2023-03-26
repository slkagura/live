package xyz.slkagura.concurrent;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class CountTimer {
    private final Thread mThread;
    
    private final long mDelay;
    
    private final long mPeriod;
    
    private final int mInitCount;
    
    private final AtomicInteger mState;
    
    private final AtomicInteger mCurrentCount;
    
    private final Listener mListener;
    
    public CountTimer(Listener listener, long delay, long period, int count, TimeUnit unit) {
        mListener = listener;
        mInitCount = count;
        mDelay = unit.toNanos(delay);
        mPeriod = unit.toNanos(period);
        mState = new AtomicInteger(State.READY);
        mCurrentCount = new AtomicInteger(mInitCount);
        mThread = new Thread(this::run);
        mListener.onReady(this);
    }
    
    private void run() {
        if (mDelay > 0) {
            LockSupport.parkNanos(mThread, mDelay);
        }
        mListener.onStart(this);
        @State int state;
        while ((state = mState.get()) != State.FINAL) {
            switch (state) {
                case State.RUNNING:
                    LockSupport.parkNanos(mThread, mPeriod);
                    mListener.onCount(this);
                    if (mCurrentCount.decrementAndGet() == 0) {
                        mState.set(State.DONE);
                    }
                    break;
                case State.PAUSE:
                    mListener.onPause(this);
                    LockSupport.park(mThread);
                    break;
                case State.DONE:
                    mListener.onDone(this);
                    LockSupport.park(mThread);
                    break;
                default:
            }
        }
        mListener.onFinal(this);
    }
    
    public void start() {
        if (mThread.isAlive() || mThread.isInterrupted()) {
            return;
        }
        mThread.start();
    }
    
    public void stop() {
        mState.set(State.FINAL);
    }
    
    public void pause() {
        mState.set(State.PAUSE);
    }
    
    public void resume() {
        mState.set(State.RUNNING);
        LockSupport.unpark(mThread);
    }
    
    public void reset() {
        mCurrentCount.set(mInitCount);
    }
    
    public static Builder getBuilder() {
        return new Builder();
    }
    
    public interface Listener {
        default void onReady(CountTimer timer) {}
        
        default void onStart(CountTimer timer) {}
        
        default void onCount(CountTimer timer) {}
        
        default void onPause(CountTimer timer) {}
        
        default void onDone(CountTimer timer) {}
        
        default void onFinal(CountTimer timer) {}
    }
    
    @IntDef({
        State.READY, State.RUNNING, State.PAUSE, State.DONE, State.FINAL
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
        int READY = 0;
        
        int RUNNING = 1;
        
        int PAUSE = 2;
        
        int DONE = 3;
        
        int FINAL = 4;
    }
    
    public static class Builder {
        private long mDelay = 0L;
        
        private long mPeriod = 1000L;
        
        private int mCount = 1;
        
        private TimeUnit mTimeUnit = TimeUnit.MILLISECONDS;
        
        private boolean mAutoStart = false;
        
        private Listener mListener;
        
        private Builder() {}
        
        public CountTimer build() {
            CountTimer timer = new CountTimer(mListener, mDelay, mPeriod, mCount, mTimeUnit);
            if (mAutoStart) {
                timer.start();
            }
            return timer;
        }
        
        public Builder setAutoStart(boolean autoStart) {
            mAutoStart = autoStart;
            return this;
        }
        
        public Builder setCount(int count) {
            mCount = count;
            return this;
        }
        
        public Builder setDelay(long delay) {
            mDelay = delay;
            return this;
        }
        
        public Builder setListener(@NonNull Listener listener) {
            mListener = listener;
            return this;
        }
        
        public Builder setPeriod(long period) {
            mPeriod = period;
            return this;
        }
        
        public Builder setTimeUnit(TimeUnit timeUnit) {
            mTimeUnit = timeUnit;
            return this;
        }
    }
}
