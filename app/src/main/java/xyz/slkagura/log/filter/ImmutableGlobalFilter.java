package xyz.slkagura.log.filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImmutableGlobalFilter extends Filter {
    private final boolean mEnable;
    
    public ImmutableGlobalFilter(boolean enable) {
        mEnable = enable;
    }
    
    public boolean isEnable() {
        return mEnable;
    }
    
    @Override
    public boolean filter(int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages) {
        return mEnable;
    }
}
