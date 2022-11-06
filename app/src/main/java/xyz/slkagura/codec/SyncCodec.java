package xyz.slkagura.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import xyz.slkagura.codec.bean.Frame;

public class SyncCodec extends Thread {
    private static final String TAG = SyncCodec.class.getSimpleName();
    
    private static final long TIMEOUT = 5000;
    
    private final ArrayBlockingQueue<Frame> mEncodeQueue = new ArrayBlockingQueue<>(10);
    
    private final ArrayBlockingQueue<Frame> mDecodeQueue = new ArrayBlockingQueue<>(10);
    
    private final ReentrantLock mEncodeLock = new ReentrantLock();
    
    private final ReentrantLock mDecodeLock = new ReentrantLock();
    
    private MediaFormat mEncodeFormat;
    
    private MediaCodec mEncoder;
    
    private boolean mEncoderReady;
    
    private MediaFormat mDecodeFormat;
    
    private MediaCodec mDecoder;
    
    private boolean mDecoderReady;
    
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    
    private Surface mEncodeInputSurface;
    
    private Surface mDecodeOutputSurface;
    
    public SyncCodec(String name) {
        super(name);
        start();
    }
    
    public void createEncoder(MediaFormat format) {
        if (mEncoder != null || format == null) {
            return;
        }
        mEncodeFormat = format;
        try {
            mEncoder = MediaCodec.createEncoderByType(mEncodeFormat.getString(MediaFormat.KEY_MIME));
            mEncoder.configure(mEncodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            if (mEncoder == null) {
                return;
            }
            mEncoder.start();
            mEncoderReady = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteEncoder() {
        mEncodeQueue.clear();
        if (mEncoder != null) {
            mEncoder.release();
            mEncoder = null;
        }
        mEncodeFormat = null;
        mEncoderReady = false;
    }
    
    public void createDecoder(MediaFormat format, Surface surface) {
        if (mDecoder != null || format == null || surface == null || !surface.isValid()) {
            return;
        }
        mDecodeFormat = format;
        mDecodeOutputSurface = surface;
        try {
            mDecoder = MediaCodec.createDecoderByType(mDecodeFormat.getString(MediaFormat.KEY_MIME));
            mDecoder.configure(mDecodeFormat, mDecodeOutputSurface, null, 0);
            if (mDecoder == null) {
                return;
            }
            mDecoder.start();
            mDecoderReady = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteDecoder() {
        mDecodeQueue.clear();
        if (mDecoder != null) {
            mDecoder.release();
            mDecoder = null;
        }
        if (mDecodeOutputSurface != null) {
            mDecodeOutputSurface.release();
            mDecodeOutputSurface = null;
        }
        mDecodeFormat = null;
        mDecoderReady = false;
    }
    
    public void queueEncode(Frame data) {
        // mEncodeLock.lock();
        try {
            mEncodeQueue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // mEncodeLock.unlock();
    }
    
    public void queueDecode(Frame data) {
        // mDecodeLock.lock();
        try {
            mDecodeQueue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // mDecodeLock.unlock();
    }
    
    @Override
    public void run() {
        try {
            int state = 0;
            while (!isInterrupted()) {
                int tmp = state;
                switch (state) {
                    case 0:
                        if (decodeOutput()) {
                            tmp = 0;
                        } else {
                            tmp = 1;
                        }
                    case 1:
                        if (decodeInput()) {
                            tmp = 0;
                        } else {
                            tmp = 2;
                        }
                    case 2:
                        if (encodeOutput()) {
                            tmp = 1;
                        } else {
                            tmp = 3;
                        }
                    case 3:
                        if (encodeInput()) {
                            tmp = 2;
                        } else {
                            tmp = 3;
                        }
                }
                state = tmp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteDecoder();
            deleteEncoder();
        }
    }
    
    private boolean decodeOutput() {
        if (!mDecoderReady) {
            return false;
        }
        int index = mDecoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT);
        if (index >= 0) {
            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                return false;
            }
            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0 && mBufferInfo.size > 0) {
                mDecoder.releaseOutputBuffer(index, mDecodeOutputSurface != null && mDecodeOutputSurface.isValid());
                return true;
            }
        }
        return false;
    }
    
    private boolean decodeInput() {
        if (!mDecoderReady) {
            return false;
        }
        // mDecodeLock.lock();
        Frame frame = mDecodeQueue.poll();
        // mDecodeLock.unlock();
        if (frame == null) {
            return false;
        }
        int index = mDecoder.dequeueInputBuffer(TIMEOUT);
        if (index >= 0) {
            ByteBuffer buffer = mDecoder.getInputBuffer(index);
            if (buffer != null) {
                buffer.clear();
                buffer.put(frame.mData);
                mDecoder.queueInputBuffer(index, 0, frame.mLength, frame.mPTS, 0);
                return true;
            }
        }
        return false;
    }
    
    private boolean encodeOutput() {
        if (!mEncoderReady) {
            return false;
        }
        int index = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT);
        if (index >= 0) {
            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                return false;
            }
            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0 && mBufferInfo.size > 0) {
                ByteBuffer buffer = mEncoder.getOutputBuffer(index);
                if (buffer != null) {
                    byte[] data = new byte[mBufferInfo.size];
                    buffer.get(data);
                    mEncoder.releaseOutputBuffer(index, false);
                    Frame frame = new Frame(data);
                    mDecodeQueue.offer(frame);
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean encodeInput() {
        if (!mEncoderReady) {
            return false;
        }
        // mEncodeLock.lock();
        Frame frame = mEncodeQueue.poll();
        // mEncodeLock.unlock();
        if (frame == null) {
            return false;
        }
        int index = mEncoder.dequeueInputBuffer(TIMEOUT);
        if (index >= 0) {
            ByteBuffer buffer = mEncoder.getInputBuffer(index);
            buffer.clear();
            buffer.put(frame.mData);
            mEncoder.queueInputBuffer(index, 0, frame.mLength, frame.mPTS, MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
            return true;
        }
        return false;
    }
}
