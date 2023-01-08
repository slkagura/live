package xyz.slkagura.log.filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MutableGlobalFilter extends Filter {
    private boolean mEnable = true;
    
    public MutableGlobalFilter() {
    }
    
    public MutableGlobalFilter(boolean enable) {
        mEnable = enable;
    }
    
    public boolean isEnable() {
        return mEnable;
    }
    
    public void setEnable(boolean enable) {
        mEnable = enable;
    }
    
    @Override
    public boolean filter(int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages) {
        return mEnable;
    }
}
