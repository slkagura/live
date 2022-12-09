package xyz.slkagura.codec.interfaces;

import android.media.MediaCodec;

import androidx.annotation.NonNull;

public interface InputCallback {
    void onInputBufferAvailable(@NonNull MediaCodec codec, int index);
}
