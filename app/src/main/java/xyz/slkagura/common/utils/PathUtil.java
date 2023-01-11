package xyz.slkagura.common.utils;

import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public class PathUtil {
    private static final File EXTERNAL_STORAGE_FILE = Environment.getExternalStorageDirectory();
    
    private static final StringBuffer BUFFER = new StringBuffer();
    
    @NonNull
    public static String getExternalStoragePath(@Nullable String... paths) {
        if (paths == null || paths.length < 1) {
            return "";
        } else if (paths.length == 1) {
            return paths[0];
        } else {
            for (String path : paths) {
                BUFFER.append(path);
            }
            String result = BUFFER.toString();
            BUFFER.delete(0, BUFFER.length());
            return result;
        }
    }
}
