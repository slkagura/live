package xyz.slkagura.ui.view.grafika;

import android.opengl.GLSurfaceView;

import androidx.annotation.NonNull;

import java.util.List;

import xyz.slkagura.R;
import xyz.slkagura.camera.CameraHelper;
import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.base.BaseViewModel;
import xyz.slkagura.common.extension.log.Log;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.databinding.ActivityGrafikaBinding;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/4/5 20:19
 */
public class GrafikaActivity extends BaseBindingActivity<ActivityGrafikaBinding> {
    private static final String TAG = GrafikaActivity.class.getSimpleName();
    
    private GrafikaViewModel mViewModel;
    
    private GLRenderer mRenderer;
    
    private CameraHelper mCameraHelper;
    
    @Override
    protected int initLayoutId() {
        return R.layout.activity_grafika;
    }
    
    @Override
    protected void initDataBinding(@NonNull List<BaseViewModel> list) {
        mViewModel = ViewModelUtil.get(this, GrafikaViewModel.class);
        list.add(mViewModel);
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setV(this);
        mBinding.setVm(mViewModel);
        // use OpenGLES 2.0 version
        mBinding.glSurfaceView.setEGLContextClientVersion(2);
        // set GLSurface renderer
        mRenderer = new GLRenderer();
        mBinding.glSurfaceView.setRenderer(mRenderer);
        mBinding.glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    
    @Override
    protected void onResume() {
        Log.v(TAG, "onResume() Call");
        super.onResume();
        mBinding.glSurfaceView.onResume();
        mBinding.glSurfaceView.queueEvent(() -> mRenderer.setCameraPreviewSize(1280, 720));
        mCameraHelper.notifyCreateDevice();
        Log.v(TAG, "onResume() End");
    }
    
    @Override
    protected void onPause() {
        Log.v(TAG, "onPause() Call");
        super.onPause();
        mCameraHelper.notifyDeleteDevice();
        mBinding.glSurfaceView.queueEvent(() -> mRenderer.pause());
        mBinding.glSurfaceView.onPause();
        Log.v(TAG, "onPause() End");
    }
    
    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy() Call");
        super.onDestroy();
        Log.v(TAG, "onDestroy() End");
    }
}
