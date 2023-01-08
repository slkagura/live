package xyz.slkagura.log.appender;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.slkagura.log.Priority;

public abstract class Appender {
    protected final StringBuffer mStringBuffer = new StringBuffer();
    
    @NonNull
    protected String concat(@NonNull Object... messages) {
        if (messages.length < 1) {
            return "";
        }
        if (messages.length == 1) {
            return String.valueOf(messages[0]);
        }
        for (Object o : messages) {
            mStringBuffer.append(o);
        }
        String message = mStringBuffer.toString();
        mStringBuffer.delete(0, mStringBuffer.length());
        return message;
    }
    
    public abstract void append(@Priority int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages);
}
