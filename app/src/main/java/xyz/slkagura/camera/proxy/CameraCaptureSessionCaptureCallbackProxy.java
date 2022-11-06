package xyz.slkagura.camera.proxy;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.view.Surface;

import androidx.annotation.NonNull;

import xyz.slkagura.camera.interfaces.ICameraCaptureSessionCaptureCallback;

public class CameraCaptureSessionCaptureCallbackProxy extends CameraCaptureSession.CaptureCallback {
    private final ICameraCaptureSessionCaptureCallback mCallback;
    
    public CameraCaptureSessionCaptureCallbackProxy(ICameraCaptureSessionCaptureCallback callback) {
        mCallback = callback;
    }
    
    @Override
    public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
        if (mCallback != null) {
            mCallback.onCaptureStarted(session, request, timestamp, frameNumber);
        }
    }
    
    @Override
    public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
        if (mCallback != null) {
            mCallback.onCaptureProgressed(session, request, partialResult);
        }
    }
    
    @Override
    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
        if (mCallback != null) {
            mCallback.onCaptureCompleted(session, request, result);
        }
    }
    
    @Override
    public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
        if (mCallback != null) {
            mCallback.onCaptureFailed(session, request, failure);
        }
    }
    
    @Override
    public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
        if (mCallback != null) {
            mCallback.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }
    }
    
    @Override
    public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
        if (mCallback != null) {
            mCallback.onCaptureSequenceAborted(session, sequenceId);
        }
    }
    
    @Override
    public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
        if (mCallback != null) {
            mCallback.onCaptureBufferLost(session, request, target, frameNumber);
        }
    }
}
