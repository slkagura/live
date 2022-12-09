package xyz.slkagura.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;

import xyz.slkagura.codec.interfaces.ChangeCallback;
import xyz.slkagura.codec.interfaces.ErrorCallback;
import xyz.slkagura.codec.interfaces.IMediaCodecCallback;
import xyz.slkagura.codec.interfaces.InputCallback;
import xyz.slkagura.codec.interfaces.OutputCallback;
import xyz.slkagura.codec.proxy.MediaCodecCallbackProxy;
import xyz.slkagura.common.extension.log.Log;
import xyz.slkagura.common.interfaces.PCallback;

public class AsyncCodec implements IMediaCodecCallback {
    private static final String ASYNC_CODEC_TAG = AsyncCodec.class.getSimpleName();
    
    private final ArrayBlockingQueue<byte[]> mQueue;
    
    private final PCallback<byte[]> mCallback;
    
    private final InputCallback mOnInput;
    
    private final OutputCallback mOnOutput;
    
    private final ChangeCallback mOnChanged;
    
    private final ErrorCallback mOnError;
    
    private final MediaCodec mCodec;
    
    private AsyncCodec(@NonNull MediaFormat format, @NonNull ArrayBlockingQueue<byte[]> queue, PCallback<byte[]> callback, Surface surface) {
        mQueue = queue;
        mCallback = callback;
        MediaCodec codec = null;
        boolean isEncoder = true;
        try {
            if (surface != null) {
                codec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
                codec.configure(format, surface, null, 0);
                isEncoder = false;
            } else if (mCallback != null) {
                MediaCodec mediaCodec = MediaCodec.createEncoderByType(format.getString(MediaFormat.KEY_MIME));
                mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            }
            if (codec != null) {
                codec.setCallback(new MediaCodecCallbackProxy(this));
                codec.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCodec = codec;
        if (isEncoder) {
            mOnInput = this::onEncoderInputBufferAvailable;
            mOnOutput = this::onEncoderOutputBufferAvailable;
            mOnError = this::onEncoderError;
            mOnChanged = this::onEncoderOutputFormatChanged;
        } else {
            mOnInput = this::onDecoderInputBufferAvailable;
            mOnOutput = this::onDecoderOutputBufferAvailable;
            mOnError = this::onDecoderError;
            mOnChanged = this::onDecoderOutputFormatChanged;
        }
    }
    
    public static AsyncCodec create(@NonNull MediaFormat format, @NonNull PCallback<byte[]> callback) {
        return new AsyncCodec(format, new ArrayBlockingQueue<>(25), callback, null);
    }
    
    public static AsyncCodec create(@NonNull MediaFormat format, @NonNull Surface surface) {
        return new AsyncCodec(format, new ArrayBlockingQueue<>(125), null, surface);
    }
    
    public void release() {
        mQueue.clear();
        mCodec.stop();
        mCodec.release();
    }
    
    @Override
    public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
        mOnInput.onInputBufferAvailable(codec, index);
    }
    
    @Override
    public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
        mOnOutput.onOutputBufferAvailable(codec, index, info);
    }
    
    @Override
    public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
        mOnError.onError(codec, e);
    }
    
    @Override
    public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
        mOnChanged.onOutputFormatChanged(codec,format);
    }
    
    private void onEncoderInputBufferAvailable(@NonNull MediaCodec codec, int index) {
        Log.v(ASYNC_CODEC_TAG, "onEncoderInputBufferAvailable()");
    }
    
    public void onEncoderOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
        Log.v(ASYNC_CODEC_TAG, "onEncoderOutputBufferAvailable()");
    }
    
    private void onEncoderError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
        Log.v(ASYNC_CODEC_TAG, "onEncoderError()");
        e.printStackTrace();
    }
    
    private void onEncoderOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
        Log.v(ASYNC_CODEC_TAG, "onEncoderOutputFormatChanged()");
    }
    
    private void onDecoderInputBufferAvailable(@NonNull MediaCodec codec, int index) {
        Log.v(ASYNC_CODEC_TAG, "onDecoderInputBufferAvailable()");
    }
    
    public void onDecoderOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
        Log.v(ASYNC_CODEC_TAG, "onDecoderOutputBufferAvailable()");
    }
    
    private void onDecoderError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
        Log.v(ASYNC_CODEC_TAG, "onDecoderError()");
        e.printStackTrace();
    }
    
    private void onDecoderOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
        Log.v(ASYNC_CODEC_TAG, "onDecoderOutputFormatChanged()");
    }
}
