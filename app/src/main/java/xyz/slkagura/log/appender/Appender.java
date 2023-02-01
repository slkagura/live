package xyz.slkagura.log.appender;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.slkagura.log.Priority;

public abstract class Appender {
    @NonNull
    protected String concat(@NonNull Object... messages) {
        if (messages.length < 1) {
            return "";
        }
        if (messages.length == 1) {
            return String.valueOf(messages[0]);
        }
        StringBuilder builder = new StringBuilder();
        for (Object o : messages) {
            builder.append(o);
        }
        return builder.toString();
    }
    
    public abstract void append(@Priority int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages);
}
