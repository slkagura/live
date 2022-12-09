package xyz.slkagura.camera.components;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.slkagura.common.utils.Log;

public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private int mRatioWidth = 0;
    
    private int mRatioHeight = 0;
    
    private SurfaceTextureListener mListener;
    
    private SurfaceTexture mSurfaceTexture;
    
    private Surface mSurface;
    
    public CameraTextureView(@NonNull Context context) {
        super(context);
        super.setSurfaceTextureListener(this);
    }
    
    public CameraTextureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setSurfaceTextureListener(this);
    }
    
    public CameraTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setSurfaceTextureListener(this);
    }
    
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            Log.e("CameraTextureView", "Size cannot be negative.");
            return;
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }
    
    public Surface getSurface() {
        return mSurface;
    }
    
    @Override
    public void setSurfaceTextureListener(@Nullable SurfaceTextureListener listener) {
        mListener = listener;
    }
    
    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
        mSurfaceTexture = surfaceTexture;
        mSurfaceTexture.setDefaultBufferSize(mRatioWidth, mRatioHeight);
        mSurface = new Surface(surfaceTexture);
        if (mListener != null) {
            mListener.onSurfaceTextureAvailable(surfaceTexture, width, height);
        }
    }
    
    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
        if (mListener != null) {
            mListener.onSurfaceTextureSizeChanged(surfaceTexture, width, height);
        }
    }
    
    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mListener != null) {
            return mListener.onSurfaceTextureDestroyed(surfaceTexture);
        }
        return true;
    }
    
    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
        if (mListener != null) {
            mListener.onSurfaceTextureDestroyed(surfaceTexture);
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                height = width * mRatioHeight / mRatioWidth;
            } else {
                width = height * mRatioWidth / mRatioHeight;
            }
            setMeasuredDimension(width, height);
            if (mSurfaceTexture != null) {
                mSurfaceTexture.setDefaultBufferSize(width, height);
            }
        }
    }
}
