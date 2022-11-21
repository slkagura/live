package xyz.slkagura.ui.component;

import androidx.lifecycle.MutableLiveData;

public final class StreamPanel {
    public final MutableLiveData<Integer> mCheckedIndex = new MutableLiveData<>();
    
    public final MutableLiveData<Boolean> mSettingsVisible = new MutableLiveData<>(false);
    
    public void onSettingsClick() {
        Boolean value = mSettingsVisible.getValue();
        mSettingsVisible.setValue(!Boolean.TRUE.equals(value));
    }
    
    public interface IHandler {
        void onCommandClick();
    }
}
