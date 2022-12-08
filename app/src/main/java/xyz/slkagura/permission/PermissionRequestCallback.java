package xyz.slkagura.permission;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Map;

public interface PermissionRequestCallback extends Serializable {
    void onResult(@NonNull Map<String, Integer> permissions);
}
