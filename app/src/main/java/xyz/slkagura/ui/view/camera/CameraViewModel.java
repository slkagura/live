package xyz.slkagura.ui.view.camera;

import xyz.slkagura.camera.CameraHelper;
import xyz.slkagura.common.base.BaseViewModel;
import xyz.slkagura.common.utils.ContextUtil;

public class CameraViewModel extends BaseViewModel {
    private final CameraHelper mCameraHelper = new CameraHelper(ContextUtil.getApplicationContext());
    
    @Override
    protected void onCleared() {
        super.onCleared();
        mCameraHelper.notifyCloseThread();
    }
    
    public CameraHelper getCameraHelper() {
        return mCameraHelper;
    }
}
