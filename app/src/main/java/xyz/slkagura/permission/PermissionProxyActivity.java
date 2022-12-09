package xyz.slkagura.permission;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import xyz.slkagura.common.utils.Log;
import xyz.slkagura.permission.interfaces.BaseCallback;
import xyz.slkagura.permission.interfaces.FullCallback;
import xyz.slkagura.permission.interfaces.SimpleCallback;
import xyz.slkagura.permission.interfaces.SingleCallback;

public class PermissionProxyActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    
    private static final String PERMISSION_PROXY_ACTIVITY_TAG = PermissionProxyActivity.class.getSimpleName();
    
    private final Map<String, Boolean> mPermissions = new HashMap<>();
    
    private BaseCallback mCallback;
    
    private ActivityResultLauncher<String[]> mLauncher;
    
    private boolean mGranted = true;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        // Prepare Callback
        String key = intent.getStringExtra(PermissionUtil.CALLBACK_KEY);
        mCallback = PermissionUtil.getCallback(key, true);
        // Prepare Permissions
        String[] permissions = intent.getStringArrayExtra(PermissionUtil.PERMISSION_KEY);
        for (String permission : permissions) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                mPermissions.put(permission, true);
            }
            // Need Implement
            // else if (shouldShowRequestPermissionRationale(permission)) {
            //     mPermissions.put(permission, false);
            // }
            else {
                mPermissions.put(permission, false);
            }
        }
        // Prepare Launcher (System)
        ActivityResultContracts.RequestMultiplePermissions contracts = new ActivityResultContracts.RequestMultiplePermissions();
        mLauncher = registerForActivityResult(contracts, this::onRequestPermissionsCallback);
        mLauncher.launch(permissions);
        // Prepare Request (Custom)
        // requestPermissions(permissions, REQUEST_CODE);
        // finish();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE) {
            return;
        }
        for (int i = 0; i < permissions.length; i++) {
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            mGranted = mGranted && granted;
            mPermissions.put(permissions[i], granted);
        }
        onRequestPermissionsCallback(null);
    }
    
    private void onRequestPermissionsCallback(Map<String, Boolean> result) {
        if (result != null) {
            for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                String permission = entry.getKey();
                boolean granted = entry.getValue();
                mGranted = mGranted && granted;
                Log.v(PERMISSION_PROXY_ACTIVITY_TAG, permission, " : ", granted);
                mPermissions.put(permission, granted);
            }
        }
        callback(mGranted);
        finish();
    }
    
    private void callback(boolean granted) {
        if (mCallback instanceof FullCallback) {
            ((FullCallback) mCallback).onResult(mPermissions);
        } else if (mCallback instanceof SimpleCallback) {
            ((SimpleCallback) mCallback).onResult(granted);
        } else if (mCallback instanceof SingleCallback && granted) {
            ((SingleCallback) mCallback).onGranted();
        }
    }
}
