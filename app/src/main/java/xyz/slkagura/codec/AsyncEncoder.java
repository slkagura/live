package xyz.slkagura.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import xyz.slkagura.codec.bean.Frame;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/4/5 10:26
 */
public class AsyncEncoder {
    private static final String TAG = AsyncEncoder.class.getSimpleName();
    
    private final ArrayBlockingQueue<Frame> mQueue = new ArrayBlockingQueue<>(10);
    
    private MediaFormat mFormat;
    
    private MediaCodec mCodec;
    
    private boolean mReady;
    
    private MediaCodec.BufferInfo mBufferInfo;
    
    private Surface mSurface;
    
    public void create(MediaFormat format) {
        if (mCodec != null || format == null) {
            return;
        }
        mFormat = format;
        try {
            mCodec = MediaCodec.createEncoderByType(mFormat.getString(MediaFormat.KEY_MIME));
            mCodec.configure(mFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            if (mCodec == null) {
                return;
            }
            mCodec.start();
            mReady = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
