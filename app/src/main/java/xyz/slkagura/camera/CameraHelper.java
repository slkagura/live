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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import xyz.slkagura.camera.interfaces.ICameraCaptureSessionCaptureCallback;
import xyz.slkagura.camera.interfaces.ICameraCaptureSessionStateCallback;
import xyz.slkagura.camera.interfaces.ICameraDeviceStateCallback;
import xyz.slkagura.camera.interfaces.ICameraHelperCallback;
import xyz.slkagura.camera.proxy.CameraCaptureSessionCaptureCallbackProxy;
import xyz.slkagura.camera.proxy.CameraCaptureSessionStateCallbackProxy;
import xyz.slkagura.camera.proxy.CameraDeviceStateCallbackProxy;
import xyz.slkagura.common.utils.Log;
import xyz.slkagura.thread.SimpleAsyncToSyncQueue;

public class CameraHelper implements ICameraDeviceStateCallback, ICameraCaptureSessionStateCallback, ICameraCaptureSessionCaptureCallback, ImageReader.OnImageAvailableListener {
    private static final String TAG = CameraHelper.class.getSimpleName();
    
    private final CameraDeviceStateCallbackProxy mCameraDeviceStateCallbackProxy = new CameraDeviceStateCallbackProxy(this);
    
    private final CameraCaptureSessionStateCallbackProxy mCameraCaptureSessionStateCallbackProxy = new CameraCaptureSessionStateCallbackProxy(this);
    
    private final CameraCaptureSessionCaptureCallbackProxy mCameraCaptureSessionCaptureCallbackProxy = new CameraCaptureSessionCaptureCallbackProxy(this);
    
    private final Context mContext;
    
    /**
     * 预览尺寸
     */
    private final Size mSize = new Size(1280, 720);
    
    /**
     * 执行 Camera 的 Thread
     */
    private HandlerThread mThread;
    
    /**
     * 执行 Camera 的 Handler
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
    
    /**
     * 相机请求
     */
    private CaptureRequest mRequest;
    
    /**
     * 执行 CameraHelper 任务的队列
     */
    private final SimpleAsyncToSyncQueue mQueue = new SimpleAsyncToSyncQueue();
    
    private final List<Surface> mOutputs = new ArrayList<>();
    
    private ICameraHelperCallback mCallback;
    
    private ImageReader mImageReader;
    
    public CameraHelper(Context context) {
        mContext = context;
        startThread();
    }
    
    public void notifyStartThread() {
        Log.v(TAG, "notifyStartThread()");
        mQueue.offer(this::startThread);
    }
    
    private void startThread() {
        Log.v(TAG, "startThread()");
        if (mThread == null || mThread.isInterrupted()) {
            mThread = new HandlerThread(TAG + "Thread" + UUID.randomUUID().toString().replace("-", "").substring(0, 5));
            mThread.start();
            mHandler = new Handler(mThread.getLooper());
        }
        if (mHandler == null) {
            mHandler = new Handler(mThread.getLooper());
        }
    }
    
    public void notifyCloseThread() {
        Log.v(TAG, "notifyCloseThread()");
        mQueue.offer(this::stopThread);
    }
    
    private void stopThread() {
        Log.v(TAG, "stopThread()");
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
    
    public void notifyOpenCamera() {
        Log.v(TAG, "notifyOpenCamera()");
        mQueue.offer(this::createDevice);
    }
    
    public void notifyCloseCamera() {
        Log.v(TAG, "notifyCloseCamera()");
        mQueue.offer(() -> {
            deleteRequest();
            deleteSession();
            deleteDevice();
            mQueue.unlock();
        });
    }
    
    public void notifyCreateDevice() {
        Log.v(TAG, "notifyCreateDevice()");
        mQueue.offer(this::createDevice);
    }
    
    @SuppressLint("MissingPermission")
    private void createDevice() {
        Log.v(TAG, "createDevice()");
        if (mHandler == null) {
            mQueue.unlock();
            return;
        }
        if (mDevice != null) {
            mQueue.unlock();
            return;
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
            e.printStackTrace();
            deleteDevice();
            mQueue.unlock();
        }
    }
    
    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        Log.v(TAG, "CameraDevice.StateCallback.onOpened()");
        mDevice = camera;
        createSession();
    }
    
    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        Log.v(TAG, "CameraDevice.StateCallback.onDisconnected()");
        deleteDevice();
        mQueue.unlock();
    }
    
    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        Log.v(TAG, "CameraDevice.StateCallback.onError()");
        deleteDevice();
        mQueue.unlock();
    }
    
    public void notifyDeleteDevice() {
        Log.v(TAG, "notifyDeleteDevice()");
        mQueue.offer(() -> {
            deleteDevice();
            mQueue.unlock();
        });
    }
    
    private void deleteDevice() {
        Log.v(TAG, "deleteDevice()");
        if (mDevice != null) {
            mDevice.close();
            mDevice = null;
        }
    }
    
    public void notifyCreateSession() {
        Log.v(TAG, "notifyCreateSession()");
        mQueue.offer(this::createSession);
    }
    
    private void createSession() {
        Log.v(TAG, "createSession() Call Method");
        if (mHandler == null) {
            Log.v(TAG, "createSession() Handler Null");
            mQueue.unlock();
            return;
        }
        if (mOutputs.isEmpty()) {
            Log.e(TAG, "createSession() Output Null OR Empty");
            mQueue.unlock();
            return;
        }
        try {
            // TODO 切换成新API
            mDevice.createCaptureSession(mOutputs, mCameraCaptureSessionStateCallbackProxy, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            deleteSession();
            mQueue.unlock();
        }
        Log.v(TAG, "createSession() Call End");
    }
    
    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        Log.v(TAG, "CameraCaptureSession.StateCallback.onConfigured()");
        mSession = session;
        createRequest();
    }
    
    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        Log.v(TAG, "CameraCaptureSession.StateCallback.onConfigureFailed()");
        deleteSession();
        mQueue.unlock();
    }
    
    public void notifyDeleteSession() {
        Log.v(TAG, "notifyDeleteSession()");
        mQueue.offer(() -> {
            deleteSession();
            mQueue.unlock();
        });
    }
    
    private void deleteSession() {
        Log.v(TAG, "deleteSession()");
        if (mSession != null) {
            mSession.close();
            mSession = null;
        }
    }
    
    public void notifyCreateRequest() {
        Log.v(TAG, "notifyCreateRequest()");
        mQueue.offer(this::createRequest);
    }
    
    private void createRequest() {
        Log.v(TAG, "createRequest()");
        try {
            CaptureRequest.Builder builder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            if (mOutputs.isEmpty()) {
                deleteRequest();
                mQueue.unlock();
                return;
            }
            for (Surface surface : mOutputs) {
                builder.addTarget(surface);
            }
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            mRequest = builder.build();
            mSession.setRepeatingRequest(mRequest, mCameraCaptureSessionCaptureCallbackProxy, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            deleteRequest();
        } finally {
            mQueue.unlock();
        }
    }
    
    public void notifyDeleteRequest() {
        Log.v(TAG, "notifyDeleteRequest()");
        mQueue.offer(() -> {
            deleteRequest();
            mQueue.unlock();
        });
    }
    
    private void deleteRequest() {
        Log.v(TAG, "deleteRequest()");
        if (mRequest != null) {
            if (mSession != null) {
                try {
                    mSession.stopRepeating();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    deleteSession();
                }
            }
            mRequest = null;
        }
    }
    
    public void notifyAddOutputs(Surface... surfaces) {
        Log.v(TAG, "notifyAddOutput()");
        if (surfaces == null || surfaces.length < 1) {
            return;
        }
        mQueue.offer(() -> addOutputs(surfaces));
    }
    
    private void addOutputs(Surface[] surfaces) {
        boolean isRunning = mRequest != null;
        if (isRunning) {
            deleteRequest();
        }
        for (Surface surface : surfaces) {
            if (surface != null && surface.isValid()) {
                mOutputs.add(surface);
            }
        }
        if (isRunning) {
            createRequest();
        }
        mQueue.unlock();
    }
    
    public void notifyRemoveOutputs(Surface... surfaces) {
        Log.v(TAG, "notifyRemoveOutput()");
        if (surfaces == null || surfaces.length < 1) {
            return;
        }
        mQueue.offer(() -> removeOutputs(surfaces));
    }
    
    private void removeOutputs(Surface[] surfaces) {
        boolean isRunning = mRequest != null;
        if (isRunning) {
            deleteRequest();
        }
        mOutputs.removeAll(Arrays.asList(surfaces));
        if (isRunning) {
            createRequest();
        }
        mQueue.unlock();
    }
    
    public void notifyCreateImageReader(ImageReader.OnImageAvailableListener listener) {
        mImageReader = ImageReader.newInstance(mSize.getWidth(), mSize.getHeight(), ImageFormat.YUV_420_888, 2);
        mImageReader.setOnImageAvailableListener(listener, mHandler);
        notifyAddOutputs(mImageReader.getSurface());
    }
    
    @Override
    public void onImageAvailable(ImageReader reader) {
        Log.v(TAG, "onImageAvailable()");
        Image image = reader.acquireLatestImage();
        if (image != null) {
            image.close();
        }
    }
    
    public Size getSize() {
        return mSize;
    }
    
    public void setCallback(ICameraHelperCallback callback) {
        mCallback = callback;
    }
}
