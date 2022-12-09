package xyz.slkagura.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import xyz.slkagura.codec.interfaces.IMediaCodecCallback;
import xyz.slkagura.codec.proxy.MediaCodecCallbackProxy;
import xyz.slkagura.common.utils.Log;

public class AsyncCodec implements IMediaCodecCallback {
    private static final String ASYNC_CODEC_TAG = AsyncCodec.class.getSimpleName();
    
    private final MediaCodecCallbackProxy mProxy = new MediaCodecCallbackProxy(this);
    
    private MediaCodec mCodec;
    
    private boolean mIsEncoder;
    
    private Surface mSurface;
    
    private byte[] mData;
    
    private ArrayBlockingQueue<byte[]> mQueue;
    
    public AsyncCodec(MediaFormat format, Surface surface, boolean isEncoder) {
        if (format == null) {
            return;
        }
        mIsEncoder = isEncoder;
        try {
            if (mIsEncoder) {
                mCodec = MediaCodec.createEncoderByType(format.getString(MediaFormat.KEY_MIME));
                mCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                mSurface = mCodec.createInputSurface();
            } else {
                if (surface == null) {
                    return;
                }
                mSurface = surface;
                mCodec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
                mCodec.configure(format, mSurface, null, 0);
            }
            mCodec.setCallback(mProxy);
            mCodec.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void release() {
        if (mCodec != null) {
            mCodec.stop();
            mCodec.release();
            mCodec = null;
        }
        if (mQueue != null) {
            mQueue.clear();
            mQueue = null;
        }
    }
    
    public Surface getSurface() {
        return mSurface;
    }
    
    public void setData(byte[] data) {
        mData = data;
    }
    
    public void setQueue(ArrayBlockingQueue<byte[]> queue) {
        mQueue = queue;
    }
    
    @Override
    public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
        Log.v(ASYNC_CODEC_TAG, "onInputBufferAvailable()");
        if (mIsEncoder) {
            return;
        }
        ByteBuffer buffer = codec.getInputBuffer(index);
        if (buffer == null) {
            return;
        }
        if (mQueue != null) {
            mData = mQueue.poll();
            if (mData != null) {
                buffer.put(mData);
            }
        }
        codec.queueInputBuffer(index, 0, mData != null ? mData.length : 0, System.currentTimeMillis(), MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
    }
    
    @Override
    public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
        Log.v(ASYNC_CODEC_TAG, "onOutputBufferAvailable()");
        ByteBuffer buffer = codec.getOutputBuffer(index);
        if (buffer == null) {
            return;
        }
        if (mIsEncoder) {
            mData = new byte[buffer.remaining()];
            buffer.get(mData);
            if (mQueue != null) {
                mQueue.offer(mData);
            }
            codec.releaseOutputBuffer(index, false);
        } else {
            codec.releaseOutputBuffer(index, true);
        }
    }
    
    @Override
    public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
        Log.v(ASYNC_CODEC_TAG, "onError()");
        e.printStackTrace();
    }
    
    @Override
    public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
        Log.v(ASYNC_CODEC_TAG, "onOutputFormatChanged()");
    }
}
