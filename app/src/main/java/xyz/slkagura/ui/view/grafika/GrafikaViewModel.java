package xyz.slkagura.ui.view.grafika;

import androidx.lifecycle.MutableLiveData;

import xyz.slkagura.common.base.BaseViewModel;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/4/5 20:20
 */
public class GrafikaViewModel extends BaseViewModel {
    private final MutableLiveData<Boolean> mRecording = new MutableLiveData<>(false);
    
    public MutableLiveData<Boolean> getRecording() {
        return mRecording;
    }
}
