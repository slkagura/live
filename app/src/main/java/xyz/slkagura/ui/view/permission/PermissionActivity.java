package xyz.slkagura.ui.view.permission;

import java.util.List;

import xyz.slkagura.R;
import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.base.BaseViewModel;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.databinding.ActivityPermissionBinding;
import xyz.slkagura.permission.PermissionUtil;

public class PermissionActivity extends BaseBindingActivity<ActivityPermissionBinding> {
    private PermissionViewModel mViewModel;
    
    @Override
    protected int initLayoutId() {
        return R.layout.activity_permission;
    }
    
    @Override
    protected void initDataBinding(List<BaseViewModel> viewModels) {
        mViewModel = ViewModelUtil.get(this, PermissionViewModel.class);
        viewModels.add(mViewModel);
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
