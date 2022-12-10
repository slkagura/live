package xyz.slkagura.ui.view.camera;

import android.view.Surface;

import androidx.lifecycle.MutableLiveData;

import xyz.slkagura.camera.CameraHelper;
import xyz.slkagura.common.base.BaseBindingViewModel;
import xyz.slkagura.common.utils.ContextUtil;

public class CameraViewModel extends BaseBindingViewModel {
    private final CameraHelper mCameraHelper = new CameraHelper(ContextUtil.getApplicationContext());
    
    public CameraHelper getCameraHelper() {
        return mCameraHelper;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        mCameraHelper.notifyCloseThread();
    }
}
