package xyz.slkagura.codec.interfaces;

import android.media.MediaCodec;

import androidx.annotation.NonNull;

public interface ErrorCallback {
    void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e);
}
