package xyz.slkagura.ui.view.main;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.ui.R;
import xyz.slkagura.ui.databinding.ActivityMainBinding;
import xyz.slkagura.ui.view.camera.CameraActivity;
import xyz.slkagura.ui.view.codec.CodecActivity;
import xyz.slkagura.ui.view.live.LiveFragment;
import xyz.slkagura.ui.view.permission.PermissionActivity;

public class MainActivity extends BaseBindingActivity<MainViewModel, ActivityMainBinding> {
    private static final String MAIN_ACTIVITY_TAG = MainActivity.class.getSimpleName();
    
    @Override
    protected int initLayoutId() {
        return R.layout.activity_main;
    }
    
    @NonNull
    @Override
    protected MainViewModel initDataBinding() {
        return ViewModelUtil.get(this, MainViewModel.class);
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
    
    private void initFragment() {
        Fragment fragment = LiveFragment.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.activity_main_cl_container, fragment).addToBackStack("live").commit();
    }
}
