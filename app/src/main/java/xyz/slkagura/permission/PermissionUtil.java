package xyz.slkagura.permission;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Map;

public class PermissionUtil {
    private static final String PERMISSION_MANAGER_TAG = PermissionUtil.class.getSimpleName();
    
    public static final String PERMISSION_KEY = "xyz.slkagura.permission.Permission";
    
    public static final String CALLBACK_KEY = "xyz.slkagura.permission.Callback";
    
    private static Application sApplication;
    
    private static Context sContext;
    
    private PermissionUtil() {}
    
    public static void init(Application application) {
        sApplication = application;
        sContext = sApplication.getApplicationContext();
    }
    
    public static void request(Activity activity, @NonNull String[] permissions, @NonNull PermissionRequestCallback callback) {
        Intent intent = new Intent();
        intent.putExtra(PERMISSION_KEY, permissions);
        intent.putExtra(CALLBACK_KEY, callback);
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
        request(activity, permissions, new PermissionRequestCallback() {
            @Override
            public void onResult(@NonNull Map<String, Integer> permissions) {
            }
        });
    }
}
