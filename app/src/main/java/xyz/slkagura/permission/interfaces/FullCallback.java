package xyz.slkagura.permission.interfaces;

import androidx.annotation.NonNull;

import java.util.Map;

public interface FullCallback extends BaseCallback {
    void onResult(@NonNull Map<String, Boolean> permissions);
}
