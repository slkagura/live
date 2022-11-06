package xyz.slkagura.camera.proxy;

import android.hardware.camera2.CameraCaptureSession;

import androidx.annotation.NonNull;

import xyz.slkagura.camera.interfaces.ICameraCaptureSessionStateCallback;

public class CameraCaptureSessionStateCallbackProxy extends CameraCaptureSession.StateCallback {
    private final ICameraCaptureSessionStateCallback mCallback;
    
    public CameraCaptureSessionStateCallbackProxy(ICameraCaptureSessionStateCallback callback) {
        mCallback = callback;
    }
    
    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        if (mCallback != null) {
            mCallback.onConfigured(session);
        }
    }
    
    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        mCallback.onConfigureFailed(session);
    }
}
