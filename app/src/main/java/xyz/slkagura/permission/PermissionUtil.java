package xyz.slkagura.permission;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import xyz.slkagura.common.extension.log.Log;
import xyz.slkagura.permission.interfaces.BaseCallback;
import xyz.slkagura.permission.interfaces.FullCallback;
import xyz.slkagura.permission.interfaces.SimpleCallback;
import xyz.slkagura.permission.interfaces.SingleCallback;

public class PermissionUtil {
    public static final String PERMISSION_KEY = "xyz.slkagura.permission.Permission";
    
    public static final String CALLBACK_KEY = "xyz.slkagura.permission.Callback";
    
    private static final String PERMISSION_MANAGER_TAG = PermissionUtil.class.getSimpleName();
    
    private static final Map<String, BaseCallback> CALLBACKS = new HashMap<>();
    
    private static Application sApplication;
    
    private PermissionUtil() {}
    
    public static void init(Application application) {
        sApplication = application;
    }
    
    public static void request(Activity activity, @NonNull String[] permissions, @NonNull BaseCallback callback) {
        if ((sApplication == null && activity == null) || permissions.length < 1) {
            return;
        }
        String hash = String.valueOf(callback.hashCode());
        CALLBACKS.put(hash, callback);
        Intent intent = new Intent();
        intent.putExtra(CALLBACK_KEY, hash);
        intent.putExtra(PERMISSION_KEY, permissions);
        if (activity != null) {
            intent.setClass(activity, PermissionProxyActivity.class);
            activity.startActivity(intent);
        } else {
            intent.setClass(sApplication, PermissionProxyActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sApplication.startActivity(intent);
        }
    }
    
    public static void test(Activity activity) {
        String[] permissions = { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE };
        request(activity, permissions, new FullCallback() {
            @Override
            public void onResult(@NonNull Map<String, Boolean> permissions) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                    sb.append(entry.getKey()).append(":").append(entry.getValue()).append(System.lineSeparator());
                }
                Log.v(PERMISSION_MANAGER_TAG, sb.toString());
            }
        });
        request(activity, permissions, new SimpleCallback() {
            @Override
            public void onResult(boolean granted) {
                Log.v(PERMISSION_MANAGER_TAG, "Permission Request Result: ", granted);
            }
        });
        request(activity, permissions, new SingleCallback() {
            @Override
            public void onGranted() {
                Log.v(PERMISSION_MANAGER_TAG, "Permission All Granted");
            }
        });
    }
    
    public static BaseCallback getCallback(String key, boolean remove) {
        return remove ? CALLBACKS.get(key) : CALLBACKS.remove(key);
    }
}
