package xyz.slkagura.log.appender;

import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.Date;
import java.util.Locale;

public class FileAppender extends Appender {
    private final SimpleDateFormat mFileFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
    
    private final SimpleDateFormat mLogFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
    
    private final Date mDate = new Date();
    
    private final File mLogDir;
    
    private final File mLogPath;
    
    public FileAppender(File logDir, File logPath) {
        mLogDir = logDir;
        mLogPath = logPath;
    }
    
    @Override
    public void append(int priority, @NonNull String tag, @Nullable Throwable throwable, Object... messages) {
        String message = concat(messages);
    }
}
