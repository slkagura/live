package xyz.slkagura.permission;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.ActivityCompat;

public class PermissionManager {
    private static final String PERMISSION_MANAGER_TAG = PermissionManager.class.getSimpleName();
    
    private static final String[] CAMERA_PERMISSION_SET = { Manifest.permission_group.CAMERA };
    
    private static Application sApplication;
    private static Context sContext;
    private PermissionManager() {}
    
    public static void init(Application application) {
        sApplication = application;
        sContext = sApplication.getApplicationContext();
    }
    public static void request() {
        // ActivityCompat.requestPermissions(new PermissionProxyActivity(), new String[]{ Manifest.permission.CAMERA}, 10086);
        // Intent intent = new Intent(sContext, PermissionProxyActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // sContext.startActivity(intent);
    }
}
