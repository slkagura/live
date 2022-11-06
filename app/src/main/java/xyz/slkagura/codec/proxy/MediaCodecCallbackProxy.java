package xyz.slkagura.codec.proxy;

import android.media.MediaCodec;
import android.media.MediaFormat;

import androidx.annotation.NonNull;

import xyz.slkagura.codec.interfaces.IMediaCodecCallback;

public class MediaCodecCallbackProxy extends MediaCodec.Callback {
    private final IMediaCodecCallback mCallback;
    
    public MediaCodecCallbackProxy(IMediaCodecCallback callback) {
        mCallback = callback;
    }
    
    @Override
    public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
        if (mCallback != null) {
            mCallback.onInputBufferAvailable(codec, index);
        }
    }
    
    @Override
    public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
        if (mCallback != null) {
            mCallback.onOutputBufferAvailable(codec, index, info);
        }
    }
    
    @Override
    public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
        if (mCallback != null) {
            mCallback.onError(codec, e);
        }
    }
    
    @Override
    public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
        if (mCallback != null) {
            mCallback.onOutputFormatChanged(codec, format);
        }
    }
}
