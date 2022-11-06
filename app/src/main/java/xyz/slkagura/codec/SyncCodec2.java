package xyz.slkagura.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import xyz.slkagura.codec.bean.Frame;
import xyz.slkagura.codec.interfaces.IMediaCodecCallback;

public class SyncCodec2 extends Thread {
    private static final String TAG = SyncCodec.class.getSimpleName();
    
    private final ArrayBlockingQueue<Frame> mQueue = new ArrayBlockingQueue<>(10);
    
    private final ReentrantLock mLock = new ReentrantLock();
    
    private final Condition mCanConsume = mLock.newCondition();
    
    private final Condition mCanProduce = mLock.newCondition();
    
    private IMediaCodecCallback mCallback;
    
    private MediaFormat mEncodeFormat;
    
    private MediaCodec mEncoder;
    
    private MediaFormat mDecodeFormat;
    
    private MediaCodec mDecoder;
    
    private Surface mSurface;
    
    public SyncCodec2(String name, IMediaCodecCallback callback) {
        super(name);
        mCallback = callback;
    }
    
    public void createEncoder(MediaFormat format) {
        if (mEncoder != null || format == null) {
            return;
        }
        mEncodeFormat = format;
        try {
            mEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mEncoder.configure(mEncodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            if (mEncoder == null) {
                return;
            }
            mEncoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteEncoder() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        mEncodeFormat = null;
    }
    
    public void createDecoder(MediaFormat format, Surface surface) {
        if (mDecoder != null || format == null || surface == null || !surface.isValid()) {
            return;
        }
        mDecodeFormat = format;
        mSurface = surface;
        try {
            mDecoder = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mDecoder.configure(mDecodeFormat, mSurface, null, 0);
            if (mDecoder == null) {
                return;
            }
            mDecoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteDecoder() {
        if (mDecoder != null) {
            mDecoder.stop();
            mDecoder.release();
            mDecoder = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        mDecodeFormat = null;
    }
    
    public void queueData(Frame data) {
        mLock.lock();
        try {
            if (mQueue.size() >= 10) {
                mCanProduce.await();
            }
            mQueue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mLock.unlock();
    }
    
    @Override
    public void run() {
        try {
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            long timeOut = 5000;
            byte[] data = null;
            int state = 0;
            while (!isInterrupted()) {
                if (state == 0) {
                    mLock.lock();
                    Frame frame = mQueue.poll();
                    mCanProduce.signal();
                    mLock.unlock();
                    if (frame == null) {
                        continue;
                    }
                    int index = mEncoder.dequeueInputBuffer(timeOut);
                    if (index >= 0) {
                        ByteBuffer buffer = mEncoder.getInputBuffer(index);
                        buffer.clear();
                        // if (mCallback != null) {
                        //     mCallback.onInputBufferAvailable(mEncoder, index);
                        // }
                        // if (buffer.remaining() > 0) {
                        //     buffer.put(curByte);
                        //     mEncoder.queueInputBuffer(index, 0, buffer.remaining(), System.nanoTime() / 1000, 0);
                        // }
                        buffer.put(frame.mData);
                        mEncoder.queueInputBuffer(index, 0, frame.mLength, frame.mPTS, 0);
                    }
                    state = 1;
                    continue;
                }
                if (state == 1) {
                    int index = mEncoder.dequeueOutputBuffer(bufferInfo, timeOut);
                    if (index != MediaCodec.INFO_TRY_AGAIN_LATER && index != MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            continue;
                        }
                        if (bufferInfo.size > 0) {
                            ByteBuffer buffer = mEncoder.getOutputBuffer(index);
                            data = new byte[buffer.remaining()];
                            buffer.get(data);
                            mEncoder.releaseOutputBuffer(index, false);
                        }
                    }
                    state = 2;
                    continue;
                }
                if (state == 2) {
                    int index = mDecoder.dequeueInputBuffer(timeOut);
                    if (index >= 0) {
                        ByteBuffer buffer = mDecoder.getInputBuffer(index);
                        if (data != null && data.length > 0) {
                            buffer.clear();
                            buffer.put(data);
                            mDecoder.queueInputBuffer(index, 0, buffer.remaining(), System.nanoTime() / 1000, 0);
                        }
                    }
                    state = 3;
                    continue;
                }
                if (state == 3) {
                    int index = mDecoder.dequeueOutputBuffer(bufferInfo, timeOut);
                    if (index != MediaCodec.INFO_TRY_AGAIN_LATER && index != MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            continue;
                        }
                        if (bufferInfo.size > 0) {
                            mDecoder.releaseOutputBuffer(index, true);
                        }
                    }
                    state = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void interrupt() {
        if (!isInterrupted()) {
            deleteEncoder();
            deleteDecoder();
            super.interrupt();
            mCallback = null;
        }
    }
}
