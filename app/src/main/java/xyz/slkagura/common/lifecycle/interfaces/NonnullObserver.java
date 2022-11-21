package xyz.slkagura.common.lifecycle.interfaces;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

public interface NonnullObserver<T> extends Observer<T> {
    void onChanged(@NonNull T t);
}
