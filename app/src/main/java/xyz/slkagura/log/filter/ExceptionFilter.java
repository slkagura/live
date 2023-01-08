package xyz.slkagura.log.filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExceptionFilter extends Filter {
    private final List<Class<?>> mBannedExceptions = new ArrayList<>();
    
    public boolean addException(@NonNull Class<?> clazz) {
        return !mBannedExceptions.contains(clazz) && mBannedExceptions.add(clazz);
    }
    
    @Override
    public boolean filter(int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages) {
        return throwable != null && mBannedExceptions.contains(throwable.getClass());
    }
}
