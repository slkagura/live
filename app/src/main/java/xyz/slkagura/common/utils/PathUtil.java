package xyz.slkagura.common.utils;

import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import xyz.slkagura.common.extension.log.Log;

public class PathUtil {
    private static final String PATH_UTIL = PathUtil.class.getSimpleName();
    
    private static final File FILES = ContextUtil.getApplication().getFilesDir();
    
    private static final File CACHE = ContextUtil.getApplication().getCacheDir();
    
    private static final File STORAGE = Environment.getStorageDirectory();
    
    private static final File STORAGE_EXTERNAL = Environment.getExternalStorageDirectory();
    
    public static String getCanonical(File file) {
        String result = file.getAbsolutePath();
        try {
            result = file.getCanonicalPath();
        } catch (IOException e) {
            Log.e(PATH_UTIL, e.getMessage());
        }
        return result;
    }
    
    public static String getExternalCanonical() {
        return getCanonical(STORAGE_EXTERNAL);
    }
    
    public static String getStorageCanonical() {
        return getCanonical(STORAGE);
    }
    
    public static String getFilesCanonical() {
        return getCanonical(FILES);
    }
    
    /**
     * 构造路径
     *
     * @param prefix   路径前缀
     * @param separate 前缀后是否添加分隔符
     * @param paths    路径
     * @return 路径
     */
    @NonNull
    public static String getPath(@NonNull String prefix, boolean separate, @Nullable String... paths) {
        if (paths == null || paths.length < 1) {
            return "";
        }
        Log.d(PATH_UTIL, "getPath() Param", System.lineSeparator(), "prefix: ", prefix, System.lineSeparator(), "separate: ", separate, System.lineSeparator(), "paths: ", Arrays.toString(paths));
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);
        if (separate) {
            builder.append(File.separator);
        }
        builder.append(paths[0]);
        for (int i = 1; i < paths.length; i++) {
            String path = paths[i];
            if (path != null) {
                builder.append(File.separator).append(path);
            }
        }
        String result = builder.toString();
        Log.d(PATH_UTIL, "getPath() result: ", result);
        return result;
    }
    
    @NonNull
    public static String getExternalStoragePath(@Nullable String... paths) {
        return getPath(getExternalCanonical(), true, paths);
    }
    
    @NonNull
    public static String getStoragePath(@Nullable String... paths) {
        return getPath(getStorageCanonical(), true, paths);
    }
    
    @NonNull
    public static String getFilesPath(@Nullable String... paths) {
        return getPath(getFilesCanonical(), true, paths);
    }
}
