package xyz.slkagura.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import xyz.slkagura.camera.interfaces.ICameraCaptureSessionCaptureCallback;
import xyz.slkagura.camera.interfaces.ICameraCaptureSessionStateCallback;
import xyz.slkagura.camera.interfaces.ICameraDeviceStateCallback;
import xyz.slkagura.camera.interfaces.ICameraHelperCallback;
import xyz.slkagura.camera.proxy.CameraCaptureSessionCaptureCallbackProxy;
import xyz.slkagura.camera.proxy.CameraCaptureSessionStateCallbackProxy;
import xyz.slkagura.camera.proxy.CameraDeviceStateCallbackProxy;
import xyz.slkagura.codec.SyncCodec;
import xyz.slkagura.common.utils.LogUtil;

public class CameraHelper implements ICameraDeviceStateCallback, ICameraCaptureSessionStateCallback, ICameraCaptureSessionCaptureCallback, ImageReader.OnImageAvailableListener {
    private static final String TAG = "CameraHelper";
    
    private final CameraDeviceStateCallbackProxy mCameraDeviceStateCallbackProxy = new CameraDeviceStateCallbackProxy(this);
    
    private final CameraCaptureSessionStateCallbackProxy mCameraCaptureSessionStateCallbackProxy = new CameraCaptureSessionStateCallbackProxy(this);
    
    private final CameraCaptureSessionCaptureCallbackProxy mCameraCaptureSessionCaptureCallbackProxy = new CameraCaptureSessionCaptureCallbackProxy(this);
    
    private final Context mContext;
    
    /**
     * 预览尺寸
     */
    private final Size mSize = new Size(1280, 720);
    
    /**
     * 后台线程
     */
    private HandlerThread mThread;
    
    /**
     * 后台线程对应 Handler
     */
    private Handler mHandler;
    
    /**
     * 相机设备
     */
    private CameraDevice mDevice;
    
    /**
     * 相机会话
     */
    private CameraCaptureSession mSession;
    
    private List<Surface> mOutputs = new ArrayList<>();
    
    private ICameraHelperCallback mCallback;
    
    private ImageReader mImageReader;
    
    private SyncCodec mSyncCodec;
    
    public CameraHelper(Context context) {
        mContext = context;
        startThread();
    }
    
    public Size getSize() {
        return mSize;
    }
    
    public void setCallback(ICameraHelperCallback callback) {
        mCallback = callback;
    }
    
    public void setSyncCodec(SyncCodec syncCodec) {
        mSyncCodec = syncCodec;
    }
    
    private void startThread() {
        LogUtil.d(TAG, "startThread()");
        if (mThread == null) {
            mThread = new HandlerThread("CameraThread");
            mThread.start();
            mHandler = new Handler(mThread.getLooper());
        }
        if (mHandler == null) {
            mHandler = new Handler(mThread.getLooper());
        }
    }
    
    private void stopThread() {
        LogUtil.d(TAG, "stopThread()");
        if (mThread != null) {
            mThread.quitSafely();
            try {
                mThread.join();
                mThread = null;
                mHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void createImageReader(ImageReader.OnImageAvailableListener listener) {
        mImageReader = ImageReader.newInstance(mSize.getWidth(), mSize.getHeight(), ImageFormat.YUV_420_888, 2);
        mImageReader.setOnImageAvailableListener(listener, mHandler);
        notifyAddOutput(mImageReader.getSurface());
    }
    
    private void createSession() {
        LogUtil.d(TAG, "createSession()");
        try {
            if (mOutputs != null) {
                // TODO 切换成新API
                mDevice.createCaptureSession(mOutputs, mCameraCaptureSessionStateCallbackProxy, mHandler);
            }
        } catch (CameraAccessException e) {
            notifyCloseCamera();
            e.printStackTrace();
        }
    }
    
    private void closeSession() {
        LogUtil.d(TAG, "closeSession()");
        if (mSession != null) {
            mSession.close();
            mSession = null;
        }
    }
    
    private void closeDevice() {
        LogUtil.d(TAG, "closeDevice()");
        if (mDevice != null) {
            mDevice.close();
            mDevice = null;
        }
    }
    
    @SuppressLint("MissingPermission")
    public void notifyOpenCamera() {
        LogUtil.d(TAG, "notifyOpenCamera()");
        if (mDevice != null) {
            notifyCloseCamera();
        }
        CameraManager manager = mContext.getSystemService(CameraManager.class);
        try {
            String cameraId = "";
            String[] cameraIdList = manager.getCameraIdList();
            for (int i = 0; i <= cameraIdList.length; i++) {
                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraIdList[i]);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    cameraId = cameraIdList[i];
                    break;
                }
            }
            manager.openCamera(cameraId, mCameraDeviceStateCallbackProxy, mHandler);
        } catch (CameraAccessException e) {
            notifyCloseCamera();
            e.printStackTrace();
        }
    }
    
    public void notifyAddOutput(Surface surface) {
        LogUtil.d(TAG, "notifyAddOutput()");
        if (surface == null || !surface.isValid()) {
            return;
        }
        if (mOutputs == null) {
            mOutputs = new ArrayList<>();
        }
        mOutputs.add(surface);
        if (mSession != null) {
            closeSession();
            createSession();
        }
    }
    
    public void notifyRemoveOutput(Surface surface) {
        LogUtil.d(TAG, "notifyRemoveOutput()");
        if (mOutputs != null && surface != null) {
            mOutputs.remove(surface);
            if (mSession != null) {
                closeSession();
                createSession();
            }
        }
    }
    
    public void notifyCloseCamera() {
        LogUtil.d(TAG, "notifyCloseCamera()");
        closeSession();
        closeDevice();
    }
    
    public void notifyCloseThread() {
        LogUtil.d(TAG, "notifyCloseThread()");
        stopThread();
    }
    
    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        LogUtil.d(TAG, "onOpened()");
        mDevice = camera;
        createSession();
    }
    
    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        LogUtil.d(TAG, "onDisconnected()");
        notifyCloseCamera();
    }
    
    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        LogUtil.d(TAG, "onError()");
        notifyCloseCamera();
    }
    
    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        LogUtil.d(TAG, "onConfigured()");
        mSession = session;
        try {
            CaptureRequest.Builder builder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            if (mOutputs == null || mOutputs.isEmpty()) {
                closeSession();
                closeDevice();
                return;
            }
            for (Surface surface : mOutputs) {
                builder.addTarget(surface);
            }
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            CaptureRequest request = builder.build();
            mSession.setRepeatingRequest(request, mCameraCaptureSessionCaptureCallbackProxy, mHandler);
        } catch (CameraAccessException e) {
            notifyCloseCamera();
            e.printStackTrace();
        }
    }
    
    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        LogUtil.d(TAG, "onConfigureFailed()");
    }
    
    @Override
    public void onImageAvailable(ImageReader reader) {
        LogUtil.d(TAG, "onImageAvailable()");
        Image image = reader.acquireLatestImage();
        if (image != null) {
            image.close();
        }
    }
}
