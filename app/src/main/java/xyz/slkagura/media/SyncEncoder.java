package xyz.slkagura.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.view.Surface;

import java.io.File;
import java.io.IOException;

import xyz.slkagura.common.utils.PathUtil;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/4/5 22:45
 */
public class SyncEncoder {
    private static final String TAG = SyncEncoder.class.getSimpleName();
    
    private MediaFormat mFormat;
    
    private MediaCodec.BufferInfo mBufferInfo;
    
    private MediaCodec mCodec;
    
    private Surface mInputSurface;
    
    private MediaMuxer mMediaMuxer;
    
    private EncoderCore mEncoderCore;
    
    private int mTrackIndex;
    
    private boolean mMuxerStarted = false;
    
    public void create() throws IOException {
        // 初始化编码器
        mCodec = MediaCodec.createEncoderByType(mFormat.getString(MediaFormat.KEY_MIME));
        mFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, 640, 480);
        mFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1800000);
        mFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
        mCodec.configure(mFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = mCodec.createInputSurface();
        // 初始化混合器
        String output = PathUtil.getExternalFilesPath("Record", String.valueOf(System.currentTimeMillis()));
        File file = new File(output);
        file.deleteOnExit();
        file.getParentFile().mkdirs();
        mMediaMuxer = new MediaMuxer(output, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        // 启动编码器
        mCodec.start();
        mEncoderCore.build(mInputSurface);
    }
}
