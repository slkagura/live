package xyz.slkagura.ui.view.sensor;

import androidx.annotation.NonNull;

import java.util.List;

import xyz.slkagura.R;
import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.base.BaseViewModel;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.databinding.ActivitySensorBinding;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/3/26 14:40
 */
public class SensorActivity extends BaseBindingActivity<ActivitySensorBinding> {
    private SensorViewModel mViewModel;
    
    @Override
    protected int initLayoutId() {
        return R.layout.activity_sensor;
    }
    
    @Override
    protected void initDataBinding(@NonNull List<BaseViewModel> list) {
        mViewModel = ViewModelUtil.get(this, SensorViewModel.class);
        list.add(mViewModel);
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setV(this);
        mBinding.setVm(mViewModel);
    }
}
