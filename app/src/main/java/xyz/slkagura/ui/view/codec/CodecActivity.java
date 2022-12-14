package xyz.slkagura.ui.view.codec;

import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

import xyz.slkagura.R;
import xyz.slkagura.camera.CameraHelper;
import xyz.slkagura.codec.AsyncCodec;
import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.utils.ContextUtil;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.databinding.ActivityCodecBinding;

public class CodecActivity extends BaseBindingActivity<CodecViewModel, ActivityCodecBinding> {
    private final CameraHelper mCameraHelper = new CameraHelper(ContextUtil.getApplicationContext());
    
    private Surface mCameraSurface;
    
    private Surface mEncoderInputSurface;
    
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
                mCameraHelper.notifyRemoveOutputs(mCameraSurface);
                mCameraSurface = null;
                return false;
            }
            
            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });
        mBinding.activityCodecFlCameraPreview.addView(textureView);
    }
    
    public void onCodecClick() {
        String type = MediaFormat.MIMETYPE_VIDEO_HEVC;
        MediaFormat encodeFormat = MediaFormat.createVideoFormat(type, 1920, 1080);
        encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 500000);
        encodeFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
        encodeFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        encodeFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        MediaFormat decodeFormat = MediaFormat.createVideoFormat(type, 1920, 1080);
        decodeFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        TextureView textureView = new TextureView(mContext);
        textureView.setId(View.generateViewId());
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                mDecodeOutputSurface = new Surface(surface);
                mDecoder = AsyncCodec.create(decodeFormat, mDecodeOutputSurface);
                mEncoder = AsyncCodec.create(encodeFormat, param -> {
                    if (mDecoder != null) {
                        mDecoder.offer(param);
                    }
                });
                mEncoderInputSurface = mEncoder.getSurface();
                mCameraHelper.notifyAddOutputs(mEncoderInputSurface);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraHelper != null) {
            mCameraHelper.notifyCloseThread();
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
