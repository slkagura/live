package xyz.slkagura.log.appender;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.slkagura.log.Priority;

public class ConsoleAppender extends Appender {
    @Override
    public void append(@Priority int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages) {
        String message = concat(messages);
        Log.println(priority, tag, message);
    }
}
