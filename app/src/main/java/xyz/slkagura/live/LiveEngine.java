package xyz.slkagura.live;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import xyz.slkagura.camera.CameraHelper;
import xyz.slkagura.codec.SyncCodec;
import xyz.slkagura.codec.SyncCodec2;
import xyz.slkagura.codec.bean.Frame;
import xyz.slkagura.common.utils.ContextUtil;
import xyz.slkagura.common.utils.ConvertUtil;
import xyz.slkagura.live.bean.LiveUser;
import xyz.slkagura.live.interfaces.LiveEngineHandler;
import xyz.slkagura.live.tag.LiveState;

public class LiveEngine implements LiveEngineHandler {
    private static final List<LiveEngine> INSTANCES = new ArrayList<>();
    
    private final MutableLiveData<Integer> mLiveStatus = new MutableLiveData<>(LiveState.NULL);
    
    private final LiveUser mLocalUser = new LiveUser("Local");
    
    private final List<LiveUser> mRemoteUsers = new ArrayList<>();
    
    private Size mSize = new Size(1280, 720);
    
    private CameraHelper mCameraHelper;
    
    private Surface mSurface;
    
    private SyncCodec mSyncCodec;
    
    private SyncCodec2 mSyncCodec2;
    
    private LiveEngine() {}
    
    public static LiveEngine create(int index) {
        LiveEngine engine;
        if (INSTANCES.size() > index) {
            engine = INSTANCES.get(index);
        } else {
            engine = new LiveEngine();
            INSTANCES.add(engine);
        }
        return engine;
    }
    
    public MutableLiveData<Integer> getLiveStatus() {
        return mLiveStatus;
    }
    
    private CameraHelper getCameraHelper() {
        if (mCameraHelper == null) {
            Context context = ContextUtil.getApplicationContext();
            mCameraHelper = new CameraHelper(context);
        }
        return mCameraHelper;
    }
    
    private SyncCodec getSyncCodec() {
        if (mSyncCodec == null) {
            mSyncCodec = new SyncCodec("SyncCodecThread");
        }
        return mSyncCodec;
    }
    
    private SyncCodec2 getSyncCodec2() {
        if (mSyncCodec2 == null) {
            mSyncCodec2 = new SyncCodec2("SyncCodecThread");
        }
        return mSyncCodec2;
    }
    
    public boolean isInRoom() {
        return mLocalUser.getRoomId() != null && !mLocalUser.getRoomId().isEmpty();
    }
    
    public void destroy() {
        INSTANCES.remove(this);
    }
    
    public void join(String roomId) {
        onJoinRoom(roomId);
    }
    
    public void leave() {
        onLeaveRoom();
    }
    
    public TextureView startLocalView() {
        if (mLocalUser.getPreview() != null) {
            return mLocalUser.getPreview();
        }
        Context context = ContextUtil.getApplicationContext();
        CameraHelper cameraHelper = getCameraHelper();
        TextureView textureView = new TextureView(context);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
                mSurface = new Surface(surfaceTexture);
                cameraHelper.notifyAddOutputs(mSurface);
                cameraHelper.notifyCreateDevice();
            }
            
            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
            }
            
            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                cameraHelper.notifyRemoveOutputs(mSurface);
                return false;
            }
            
            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
            }
        });
        mLocalUser.setPreview(textureView);
        return mLocalUser.getPreview();
    }
    
    public void stopLocalView() {
        if (mLocalUser.getPreview() != null) {
            CameraHelper cameraHelper = getCameraHelper();
            cameraHelper.notifyRemoveOutputs(mSurface);
            mLocalUser.setPreview(null);
        }
    }
    
    public void startLocalStream() {
        SyncCodec2 syncCodec2 = getSyncCodec2();
        int colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
        MediaFormat encodeFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mSize.getWidth(), mSize.getHeight());
        encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1000000);
        encodeFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
        encodeFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
        encodeFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        syncCodec2.createEncoder(encodeFormat);
        CameraHelper cameraHelper = getCameraHelper();
        cameraHelper.notifyCreateImageReader(reader -> {
            Image image = reader.acquireLatestImage();
            if (image != null) {
                byte[] mImageData = ConvertUtil.toByteArray(image);
                if (mSyncCodec2 != null) {
                    mSyncCodec2.queueData(new Frame(mImageData));
                }
                image.close();
            }
        });
        cameraHelper.notifyCreateDevice();
    }
    
    public void stopLocalStream() {
        if (mSyncCodec2 != null) {
            mSyncCodec2.interrupt();
        }
    }
    
    public TextureView startRemoteView(String userId) {
        LiveUser user = null;
        for (LiveUser remoteUser : mRemoteUsers) {
            if (Objects.equals(remoteUser.getUserId(), userId)) {
                user = remoteUser;
                break;
            }
        }
        if (user == null) {
            return null;
        }
        TextureView textureView = user.getPreview();
        if (textureView != null) {
            return textureView;
        }
        Context context = ContextUtil.getApplicationContext();
        textureView = new TextureView(context);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
                SyncCodec2 syncCodec2 = getSyncCodec2();
                int colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;
                MediaFormat encodeFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mSize.getWidth(), mSize.getHeight());
                encodeFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
                syncCodec2.createDecoder(encodeFormat, new Surface(surfaceTexture));
                syncCodec2.start();
            }
            
            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
            }
            
            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }
            
            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
            }
        });
        user.setPreview(textureView);
        return textureView;
    }
    
    public void stopRemoteView(String userId) {
    }
    
    @Override
    public void onJoinRoom(String roomId) {
        mLocalUser.setRoomId(roomId);
    }
    
    @Override
    public void onLeaveRoom() {
        mLocalUser.setRoomId(null);
    }
    
    @Override
    public void onRemoteOnline(String userId) {
        for (LiveUser remoteUser : mRemoteUsers) {
            if (Objects.equals(remoteUser.getUserId(), userId)) {
                return;
            }
        }
        LiveUser remoteUser = new LiveUser(userId);
        mRemoteUsers.add(remoteUser);
    }
    
    @Override
    public void onRemoteOffline(String userId) {
        for (LiveUser remoteUser : mRemoteUsers) {
            if (Objects.equals(remoteUser.getUserId(), userId)) {
                mRemoteUsers.remove(remoteUser);
                break;
            }
        }
    }
}
