package xyz.slkagura.permission;

import androidx.annotation.IntDef;

@IntDef({ PermissionState.GRANTED, PermissionState.DENIED, PermissionState.REJECTED })
public @interface PermissionState {
    /**
     * 同意
     */
    int GRANTED = 0;
    
    /**
     * 拒绝
     */
    int DENIED = -1;
    
    /**
     * 拒绝切不再提示
     */
    int REJECTED = -2;
}
