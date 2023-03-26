package xyz.slkagura.common.base;

import android.content.Context;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel extends ViewModel implements LifecycleObserver {
    protected BaseRepository mDataSource;
    
    public BaseViewModel() {}
    
    protected BaseRepository initDataSource(Context context) {
        return null;
    }
}
