package xyz.slkagura.common.utils;

import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import xyz.slkagura.common.extension.log.Log;

public class PathUtil {
    private static final String TAG = PathUtil.class.getSimpleName();
    
    private static final File FILES = ContextUtil.getApplication().getFilesDir();
    
    private static final File EXTERNAL_FILES = ContextUtil.getApplication().getExternalFilesDir(null);
    
    private static final File CACHE = ContextUtil.getApplication().getCacheDir();
    
    private static final File EXTERNAL_CACHE = ContextUtil.getApplication().getExternalCacheDir();
    
    private static final File STORAGE = Environment.getStorageDirectory();
    
    private static final File EXTERNAL_STORAGE = Environment.getExternalStorageDirectory();
    
    public static String getCanonical(File file) {
        String result = file.getAbsolutePath();
        try {
            result = file.getCanonicalPath();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }
    
    public static String getExternalCanonical() {
        return getCanonical(EXTERNAL_STORAGE);
    }
    
    public static String getExternalFilesCanonical() {
        return getCanonical(EXTERNAL_FILES);
    }
    
    @NonNull
    public static String getExternalFilesPath(@Nullable String... paths) {
        return getPath(getExternalFilesCanonical(), true, paths);
    }
    
    @NonNull
    public static String getExternalStoragePath(@Nullable String... paths) {
        return getPath(getExternalCanonical(), true, paths);
    }
    
    public static String getFilesCanonical() {
        return getCanonical(FILES);
    }
    
    @NonNull
    public static String getFilesPath(@Nullable String... paths) {
        return getPath(getFilesCanonical(), true, paths);
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
        Log.d(TAG, "getPath() Param", System.lineSeparator(), "prefix: ", prefix, System.lineSeparator(), "separate: ", separate, System.lineSeparator(), "paths: ", Arrays.toString(paths));
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
        Log.d(TAG, "getPath() result: ", result);
        return result;
    }
    
    public static String getStorageCanonical() {
        return getCanonical(STORAGE);
    }
    
    @NonNull
    public static String getStoragePath(@Nullable String... paths) {
        return getPath(getStorageCanonical(), true, paths);
    }
}
