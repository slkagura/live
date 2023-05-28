package xyz.slkagura.common.utils;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import xyz.slkagura.common.extension.log.Log;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/5/14 19:05
 */
public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    
    private FileUtil() {}
    
    public static boolean prepare(@NonNull File file, boolean overwrite) {
        if (file.exists()) {
            return overwrite;
        }
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            return false;
        }
        if ((parentFile.exists() || parentFile.mkdirs()) && parentFile.canRead() && parentFile.canWrite()) {
            try {
                if (file.createNewFile() && file.exists() && file.canRead() && file.canWrite()) {
                    return true;
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return false;
    }
}
