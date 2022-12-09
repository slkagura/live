package xyz.slkagura.codec.interfaces;

import android.media.MediaCodec;
import android.media.MediaFormat;

import androidx.annotation.NonNull;

public interface ChangeCallback {
    void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format);
}
