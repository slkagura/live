package xyz.slkagura.ui.view.codec;

import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;

import xyz.slkagura.camera.CameraHelper;
import xyz.slkagura.codec.AsyncCodec;
import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.ui.R;
import xyz.slkagura.ui.databinding.ActivityCodecBinding;

public class CodecActivity extends BaseBindingActivity<CodecViewModel, ActivityCodecBinding> {
    private CameraHelper mCameraHelper;
    
    private Surface mCameraSurface;
    
    private Surface mEncodeInputSurface;
    
    private Surface mDecodeOutputSurface;
    
    private AsyncCodec mEncoder;
    
    private AsyncCodec mDecoder;
    
    @Override
    protected int initLayoutId() {
        return R.layout.activity_codec;
    }
    
    @NonNull
    @Override
    protected CodecViewModel initDataBinding() {
        return ViewModelUtil.get(this, CodecViewModel.class);
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setV(this);
        mBinding.setVm(mViewModel);
    }
    
    public void onOpenClick() {
        mCameraHelper = new CameraHelper(mContext);
        TextureView textureView = new TextureView(mContext);
        textureView.setId(View.generateViewId());
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                mCameraSurface = new Surface(surface);
                mCameraHelper.notifyAddOutputs(mCameraSurface);
                mCameraHelper.notifyCreateDevice();
            }
            
            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            }
            
            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }
            
            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });
        mBinding.activityCodecFlCameraPreview.addView(textureView);
    }
    
    public void onCodecClick() {
        int colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;
        String type = MediaFormat.MIMETYPE_VIDEO_AVC;
        MediaFormat encodeFormat = MediaFormat.createVideoFormat(type, 1920, 1080);
        encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 500000);
        encodeFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
        encodeFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
        encodeFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        MediaFormat decodeFormat = MediaFormat.createVideoFormat(type, 1920, 1080);
        decodeFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
        TextureView textureView = new TextureView(mContext);
        textureView.setId(View.generateViewId());
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(10);
                mDecodeOutputSurface = new Surface(surface);
                mDecoder = new AsyncCodec(decodeFormat, mDecodeOutputSurface, false);
                mDecoder.setQueue(queue);
                mEncoder = new AsyncCodec(encodeFormat, null, true);
                mEncodeInputSurface = mEncoder.getSurface();
                mEncoder.setQueue(queue);
                if (mCameraHelper == null) {
                    mCameraHelper = new CameraHelper(mContext);
                }
                mCameraHelper.notifyAddOutputs(mEncodeInputSurface);
                mCameraHelper.notifyCreateDevice();
            }
            
            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            }
            
            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }
            
            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });
        mBinding.activityCodecFlCodecPreview.addView(textureView);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraHelper != null) {
            mCameraHelper.notifyCloseCamera();
        }
        if (mEncoder != null) {
            mEncoder.release();
            mEncoder = null;
        }
        if (mDecoder != null) {
            mDecoder.release();
            mDecoder = null;
        }
    }
}
