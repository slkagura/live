package xyz.slkagura.ui.view.click;

import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import xyz.slkagura.R;
import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.base.BaseViewModel;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.databinding.ActivityClickBinding;

public class ClickActivity extends BaseBindingActivity<ActivityClickBinding> {
    private ClickViewModel mViewModel;
    
    @Override
    protected int initLayoutId() {
        return R.layout.activity_click;
    }
    
    @Override
    protected void initDataBinding(List<BaseViewModel> viewModels) {
        mViewModel = ViewModelUtil.get(this, ClickViewModel.class);
        viewModels.add(mViewModel);
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setV(this);
        mBinding.setVm(mViewModel);
        mBinding.activityClickBtnMulti.setOnClickListener(new View.OnClickListener() {
            private static final int COUNT = 5;
            
            private static final long DURATION = 2000L;
            
            private final long[] mHits = new long[COUNT];
            
            @Override
            public void onClick(View v) {
                // 每次点击时，数组向前移动一位
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                // 为数组最后一位赋值
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    // 重新初始化数组
                    for (int i = 0; i < COUNT; i++) {
                        mHits[i] = 0;
                    }
                    Toast.makeText(ClickActivity.this, "触发连点事件", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
