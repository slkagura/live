package xyz.slkagura.log.filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.slkagura.log.Priority;

public class PriorityFilter extends Filter {
    @Priority
    private int mPriority = Priority.INFO;
    
    public PriorityFilter() {
    }
    
    public PriorityFilter(int priority) {
        mPriority = priority;
    }
    
    public int getPriority() {
        return mPriority;
    }
    
    public void setPriority(@Priority int priority) {
        mPriority = priority;
    }
    
    @Override
    public boolean filter(@Priority int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages) {
        return priority >= mPriority;
    }
}
