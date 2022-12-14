package xyz.slkagura.common.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import xyz.slkagura.common.lifecycle.interfaces.NonnullObserver;

public class NonnullLiveData<T> extends MutableLiveData<T> {
    public NonnullLiveData(@NonNull T value) {
        super(value);
    }
    
    @NonNull
    @Override
    public T getValue() {
        T value = super.getValue();
        if (value == null) {
            throw new NullPointerException();
        }
        return value;
    }
    
    @Override
    public void setValue(@NonNull T value) {
        super.setValue(value);
    }
    
    @Override
    public void postValue(@NonNull T value) {
        super.postValue(value);
    }
    
    public void observe(@NonNull LifecycleOwner owner, @NonNull NonnullObserver<? super T> observer) {
        super.observe(owner, t -> {
            if (t != null) {
                observer.onChanged(t);
            }
        });
    }
}
