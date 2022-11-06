package xyz.slkagura.camera.interfaces;

import android.hardware.camera2.CameraCaptureSession;

import androidx.annotation.NonNull;

public interface ICameraCaptureSessionStateCallback {
    default void onConfigured(@NonNull CameraCaptureSession session) {}
    
    default void onConfigureFailed(@NonNull CameraCaptureSession session) {}
}
