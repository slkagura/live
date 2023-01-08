package xyz.slkagura.log.appender;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FileAppender extends Appender {
    @Override
    public void append(int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages) {
        String message = concat(messages);
    }
}
