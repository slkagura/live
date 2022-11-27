package xyz.slkagura.codec.interfaces;

import android.media.MediaCodec;
import android.media.MediaFormat;

import androidx.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;

public interface IMediaCodecCallback {
    default void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
    }
    
    default void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
    }
    
    default void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
    }
    
    default void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
    }
}
