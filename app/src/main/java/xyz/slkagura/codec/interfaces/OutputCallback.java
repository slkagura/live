package xyz.slkagura.codec.interfaces;

import android.media.MediaCodec;

import androidx.annotation.NonNull;

public interface OutputCallback {
    void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info);
}
