package xyz.slkagura.log.filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.slkagura.log.Priority;

public abstract class Filter {
    public abstract boolean filter(@Priority int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages);
}
