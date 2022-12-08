package xyz.slkagura.permission;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import xyz.slkagura.common.utils.LogUtil;

public class PermissionProxyActivity extends AppCompatActivity {
    private static final String PERMISSION_PROXY_ACTIVITY_TAG = PermissionProxyActivity.class.getSimpleName();
    
    private final Map<String, Integer> mPermissions = new HashMap<>();
    
    private PermissionRequestCallback mCallback;
    
    private ActivityResultLauncher<String[]> mLauncher;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        // Prepare Callback
        Serializable serializable = intent.getSerializableExtra(PermissionUtil.CALLBACK_KEY);
        if (serializable instanceof PermissionRequestCallback) {
            mCallback = (PermissionRequestCallback) serializable;
        }
        // Prepare Permissions
        String[] permissions = intent.getStringArrayExtra(PermissionUtil.PERMISSION_KEY);
        covert(permissions);
        // Prepare Launcher
        ActivityResultContracts.RequestMultiplePermissions contracts = new ActivityResultContracts.RequestMultiplePermissions();
        mLauncher = registerForActivityResult(contracts, this::onRequestPermissionsCallback);
        mLauncher.launch(permissions);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.v(PERMISSION_PROXY_ACTIVITY_TAG, Arrays.toString(permissions));
        LogUtil.v(PERMISSION_PROXY_ACTIVITY_TAG, Arrays.toString(grantResults));
        onBackPressed();
    }
    
    private void onRequestPermissionsCallback(Map<String, Boolean> result) {
        if (result != null && mCallback != null) {
            for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                LogUtil.v(PERMISSION_PROXY_ACTIVITY_TAG, entry.getKey(), " : ", entry.getValue());
            }
            // mCallback.onResult(result);
        }
        onBackPressed();
    }
    
    private void covert(@NonNull String[] permissions) {
        for (String permission : permissions) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                mPermissions.put(permission, PermissionState.GRANTED);
            } else if (shouldShowRequestPermissionRationale(permission)) {
                mPermissions.put(permission, PermissionState.DENIED);
            } else {
                mPermissions.put(permission, PermissionState.REJECTED);
            }
        }
    }
}
