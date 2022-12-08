package xyz.slkagura.ui.view.permission;

import androidx.annotation.NonNull;

import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.permission.PermissionUtil;
import xyz.slkagura.ui.R;
import xyz.slkagura.ui.databinding.ActivityPermissionBinding;

public class PermissionActivity extends BaseBindingActivity<PermissionViewModel, ActivityPermissionBinding> {
    @Override
    protected int initLayoutId() {
        return R.layout.activity_permission;
    }
    
    @NonNull
    @Override
    protected PermissionViewModel initDataBinding() {
        return ViewModelUtil.get(this, PermissionViewModel.class);
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setV(this);
        mBinding.setVm(mViewModel);
    }
    
    public void onPermissionClick() {
        PermissionUtil.test(this);
    }
}
