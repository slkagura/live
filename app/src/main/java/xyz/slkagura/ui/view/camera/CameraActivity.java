package xyz.slkagura.ui.view.camera;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.w3c.dom.Text;

import xyz.slkagura.camera.CameraHelper;
import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.ui.R;
import xyz.slkagura.ui.databinding.ActivityCameraBinding;

public class CameraActivity extends BaseBindingActivity<CameraViewModel, ActivityCameraBinding> {
    @Override
    protected int initLayoutId() {
        return R.layout.activity_camera;
    }
    
    @NonNull
    @Override
    protected CameraViewModel initDataBinding() {
        return ViewModelUtil.get(this, CameraViewModel.class);
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setV(this);
        mBinding.setVm(mViewModel);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        onOpenClick();
    }
    
    public void onOpenClick() {
        CameraHelper cameraHelper = new CameraHelper(mContext);
        TextureView textureView = new TextureView(mContext);
        textureView.setId(View.generateViewId());
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                cameraHelper.notifyAddOutputs(new Surface(surface));
                cameraHelper.notifyCreateDevice();
            }
    
            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            }
    
            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }
    
            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        mBinding.activityCameraClRoot.addView(textureView, params);
    }
}
