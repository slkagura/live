package xyz.slkagura.camera.proxy;

import android.hardware.camera2.CameraDevice;

import androidx.annotation.NonNull;

import xyz.slkagura.camera.interfaces.ICameraDeviceStateCallback;

public class CameraDeviceStateCallbackProxy extends CameraDevice.StateCallback {
    private final ICameraDeviceStateCallback mCallback;
    
    public CameraDeviceStateCallbackProxy(ICameraDeviceStateCallback callback) {
        mCallback = callback;
    }
    
    @Override
    public void onOpened(@NonNull CameraDevice device) {
        if (mCallback != null) {
            mCallback.onOpened(device);
        }
    }
    
    @Override
    public void onDisconnected(@NonNull CameraDevice device) {
        if (mCallback != null) {
            mCallback.onDisconnected(device);
        }
    }
    
    @Override
    public void onError(@NonNull CameraDevice device, int error) {
        if (mCallback != null) {
            mCallback.onError(device, error);
        }
    }
}
