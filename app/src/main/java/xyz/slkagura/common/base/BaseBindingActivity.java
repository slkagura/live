package xyz.slkagura.common.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public abstract class BaseBindingActivity<T extends BaseBindingViewModel, B extends ViewDataBinding> extends AppCompatActivity {
    protected T mViewModel;
    
    protected B mBinding;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }
    
    protected abstract void initViewModel();
}
