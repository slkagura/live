package xyz.slkagura.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import xyz.slkagura.codec.interfaces.ChangeCallback;
import xyz.slkagura.codec.interfaces.ErrorCallback;
import xyz.slkagura.codec.interfaces.InputCallback;
import xyz.slkagura.codec.interfaces.OutputCallback;
import xyz.slkagura.common.extension.log.Log;
import xyz.slkagura.common.interfaces.PCallback;

public class AsyncCodec {
    private static final String ASYNC_CODEC_TAG = AsyncCodec.class.getSimpleName();
    
    private final ArrayBlockingQueue<byte[]> mQueue;
    
    private final Surface mSurface;
    
    private final PCallback<byte[]> mCallback;
    
    private final InputCallback mOnInput;
    
    private final OutputCallback mOnOutput;
    
    private final ChangeCallback mOnChanged;
    
    private final ErrorCallback mOnError;
    
    private final MediaCodec mCodec;
    
    private byte[] mNALU;
    
    private AsyncCodec(@NonNull MediaFormat format, @NonNull ArrayBlockingQueue<byte[]> queue, Surface surface, PCallback<byte[]> callback) {
        mQueue = queue;
        mCallback = callback;
        MediaCodec codec = null;
        Surface tmpSurface = null;
        boolean isEncoder = true;
        try {
            if (mCallback != null) {
                // codec = MediaCodec.createEncoderByType(format.getString(MediaFormat.KEY_MIME));
                codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);
                codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                tmpSurface = codec.createInputSurface();
            } else {
                codec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
                codec.configure(format, surface, null, 0);
                isEncoder = false;
                tmpSurface = null;
            }
            codec.setCallback(new MediaCodec.Callback() {
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
                    mOnChanged.onOutputFormatChanged(codec, format);
                }
            });
            codec.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSurface = tmpSurface;
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
        return new AsyncCodec(format, new ArrayBlockingQueue<>(25), null, callback);
    }
    
    public static AsyncCodec create(@NonNull MediaFormat format, @NonNull Surface surface) {
        return new AsyncCodec(format, new ArrayBlockingQueue<>(125), surface, null);
    }
    
    public Surface getSurface() {
        return mSurface;
    }
    
    public void offer(byte[] frame) {
        mQueue.offer(frame);
    }
    
    public void release() {
        mQueue.clear();
        mCodec.stop();
        mCodec.release();
    }
    
    private void onEncoderInputBufferAvailable(@NonNull MediaCodec codec, int index) {
        Log.v(ASYNC_CODEC_TAG, "onEncoderInputBufferAvailable() Enter");
        if (index < 1) {
            return;
        }
        ByteBuffer buffer = codec.getInputBuffer(index);
        if (buffer == null) {
            return;
        }
        byte[] frame = null;
        try {
            frame = mQueue.take();
        } catch (InterruptedException e) {
            Log.e(ASYNC_CODEC_TAG, e.getMessage());
        }
        if (frame == null) {
            codec.queueInputBuffer(index, 0, 0, 0, 0);
            return;
        }
        buffer.clear();
        buffer.put(frame);
        codec.queueInputBuffer(index, 0, frame.length, System.currentTimeMillis(), 0);
        Log.v(ASYNC_CODEC_TAG, "onEncoderInputBufferAvailable() Leave");
    }
    
    public void onEncoderOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
        Log.v(ASYNC_CODEC_TAG, "onEncoderOutputBufferAvailable() Enter");
        if (index < 1) {
            return;
        }
        ByteBuffer buffer = codec.getOutputBuffer(index);
        if (buffer == null) {
            return;
        }
        byte[] frame;
        if ((info.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
            Log.v(ASYNC_CODEC_TAG, "onEncoderOutputBufferAvailable() Encode Key Frame");
            frame = new byte[mNALU.length + buffer.remaining()];
            System.arraycopy(mNALU, 0, frame, 0, mNALU.length);
            buffer.get(frame, frame.length, buffer.remaining());
        } else {
            Log.v(ASYNC_CODEC_TAG, "onEncoderOutputBufferAvailable() Encode Other Frame");
            frame = new byte[buffer.remaining()];
            buffer.get(frame);
        }
        Log.v(ASYNC_CODEC_TAG, "onEncoderOutputBufferAvailable() frame: ", Arrays.toString(frame));
        buffer.clear();
        if (mCallback != null) {
            mCallback.callback(frame);
        }
        codec.releaseOutputBuffer(index, false);
        Log.v(ASYNC_CODEC_TAG, "onEncoderOutputBufferAvailable() Leave");
    }
    
    private void onEncoderError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
        Log.v(ASYNC_CODEC_TAG, "onEncoderError() Enter");
        e.printStackTrace();
        Log.v(ASYNC_CODEC_TAG, "onEncoderError() Leave");
    }
    
    private void onEncoderOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
        Log.v(ASYNC_CODEC_TAG, "onEncoderOutputFormatChanged() Enter");
        ByteBuffer buffer = format.getByteBuffer("csd-0");
        if (buffer != null) {
            mNALU = new byte[buffer.remaining()];
            buffer.get(mNALU);
        }
        Log.v(ASYNC_CODEC_TAG, "onEncoderOutputFormatChanged() Leave");
    }
    
    private void onDecoderInputBufferAvailable(@NonNull MediaCodec codec, int index) {
        Log.v(ASYNC_CODEC_TAG, "onDecoderInputBufferAvailable() Enter");
        if (index < 0) {
            return;
        }
        ByteBuffer buffer = mCodec.getInputBuffer(index);
        if (buffer == null) {
            return;
        }
        byte[] frame = null;
        try {
            frame = mQueue.take();
        } catch (InterruptedException e) {
            Log.e(ASYNC_CODEC_TAG, e.getMessage());
        }
        if (frame == null) {
            codec.queueInputBuffer(index, 0, 0, 0, 0);
            return;
        }
        buffer.clear();
        buffer.put(frame);
        mCodec.queueInputBuffer(index, 0, frame.length, System.currentTimeMillis(), 0);
        Log.v(ASYNC_CODEC_TAG, "onDecoderInputBufferAvailable() Leave");
    }
    
    public void onDecoderOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
        Log.v(ASYNC_CODEC_TAG, "onDecoderOutputBufferAvailable() Enter");
        if (index < 1) {
            return;
        }
        ByteBuffer buffer = codec.getOutputBuffer(index);
        if (buffer == null) {
            return;
        }
        codec.releaseOutputBuffer(index, true);
        Log.v(ASYNC_CODEC_TAG, "onDecoderOutputBufferAvailable() Leave");
    }
    
    private void onDecoderError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
        Log.v(ASYNC_CODEC_TAG, "onDecoderError() Enter");
        e.printStackTrace();
        Log.v(ASYNC_CODEC_TAG, "onDecoderError() Leave");
    }
    
    private void onDecoderOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
        Log.v(ASYNC_CODEC_TAG, "onDecoderOutputFormatChanged() Enter");
        Log.v(ASYNC_CODEC_TAG, "onDecoderOutputFormatChanged() Leave");
    }
}
