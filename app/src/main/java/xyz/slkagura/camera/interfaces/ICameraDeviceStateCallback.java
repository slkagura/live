package xyz.slkagura.camera.interfaces;

import android.hardware.camera2.CameraDevice;

import androidx.annotation.NonNull;

public interface ICameraDeviceStateCallback {
    default void onOpened(@NonNull CameraDevice device) {}
    
    default void onDisconnected(@NonNull CameraDevice device) {}
    
    default void onError(@NonNull CameraDevice device, int error) {}
}
