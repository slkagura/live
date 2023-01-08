package xyz.slkagura.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import xyz.slkagura.log.appender.Appender;
import xyz.slkagura.log.filter.Filter;

public class Logger {
    private final List<Appender> mAppenders = new ArrayList<>();
    
    private final List<Filter> mFilters = new ArrayList<>();
    
    public boolean addAppender(@NonNull Appender appender) {
        return !mAppenders.contains(appender) && mAppenders.add(appender);
    }
    
    public boolean addFilter(@NonNull Filter filter) {
        return !mFilters.contains(filter) && mFilters.add(filter);
    }
    
    public void println(@Priority int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages) {
        if (mAppenders.isEmpty()) {
            return;
        }
        if (!mFilters.isEmpty()) {
            for (Filter filter : mFilters) {
                if (filter != null && !filter.filter(priority, tag, throwable, messages)) {
                    return;
                }
            }
        }
        for (Appender appender : mAppenders) {
            appender.append(priority, tag, throwable, messages);
        }
    }
    
    public void v(@NonNull String tag, Object... messages) {
        println(Priority.VERBOSE, tag, null, messages);
    }
    
    public void d(@NonNull String tag, Object... messages) {
        println(Priority.DEBUG, tag, null, messages);
    }
    
    public void i(@NonNull String tag, Object... messages) {
        println(Priority.INFO, tag, null, messages);
    }
    
    public void w(@NonNull String tag, Object... messages) {
        println(Priority.WARN, tag, null, messages);
    }
    
    public void e(@NonNull String tag, Object... messages) {
        println(Priority.ERROR, tag, null, messages);
    }
    
    public void ex(@NonNull String tag, @NonNull Throwable throwable, Object... messages) {
        println(Priority.ERROR, tag, throwable, messages);
    }
    
    public void ex(@Priority int priority, @NonNull String tag, @NonNull Throwable throwable, Object... messages) {
        println(priority, tag, throwable, messages);
    }
}
