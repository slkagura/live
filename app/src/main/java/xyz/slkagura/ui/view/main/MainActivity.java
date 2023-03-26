package xyz.slkagura.ui.view.main;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.List;

import xyz.slkagura.R;
import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.base.BaseViewModel;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.databinding.ActivityMainBinding;
import xyz.slkagura.ui.view.camera.CameraActivity;
import xyz.slkagura.ui.view.click.ClickActivity;
import xyz.slkagura.ui.view.codec.CodecActivity;
import xyz.slkagura.ui.view.live.LiveFragment;
import xyz.slkagura.ui.view.opengl.OpenGLActivity;
import xyz.slkagura.ui.view.permission.PermissionActivity;
import xyz.slkagura.ui.view.sensor.SensorActivity;

public class MainActivity extends BaseBindingActivity<ActivityMainBinding> {
    private static final String TAG = MainActivity.class.getSimpleName();
    
    private MainViewModel mViewModel;
    
    @Override
    protected int initLayoutId() {
        return R.layout.activity_main;
    }
    
    @Override
    protected void initDataBinding(List<BaseViewModel> viewModels) {
        mViewModel = ViewModelUtil.get(this, MainViewModel.class);
        viewModels.add(mViewModel);
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setV(this);
        mBinding.setVm(mViewModel);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    public void onCameraClick() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
    
    public void onCodecClick() {
        Intent intent = new Intent(this, CodecActivity.class);
        startActivity(intent);
    }
    
    public void onPermissionClick() {
        Intent intent = new Intent(this, PermissionActivity.class);
        startActivity(intent);
    }
    
    public void onMultiClick() {
        Intent intent = new Intent(this, ClickActivity.class);
        startActivity(intent);
    }
    
    public void onSensorClick() {
        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);
    }
    
    public void onOpenGLClick() {
        Intent intent = new Intent(this, OpenGLActivity.class);
        startActivity(intent);
    }
    
    private void initFragment() {
        Fragment fragment = LiveFragment.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.activity_main_cl_container, fragment).addToBackStack("live").commit();
    }
}
