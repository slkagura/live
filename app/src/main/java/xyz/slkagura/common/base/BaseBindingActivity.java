package xyz.slkagura.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import xyz.slkagura.ui.R;

public abstract class BaseBindingActivity<VM extends BaseBindingViewModel, B extends ViewDataBinding> extends RxAppCompatActivity {
    protected VM mViewModel;
    
    protected B mBinding;
    
    protected int mLayoutId = initLayoutId();
    
    protected Context mContext;
    
    protected View mRoot;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayoutId);
        mViewModel = initDataBinding();
        mBinding = DataBindingUtil.setContentView(this, mLayoutId);
        mRoot = mBinding.getRoot();
        mContext = mRoot.getContext();
        // 支持 LiveData 绑定 xml，数据改变，UI 自动会更新
        mBinding.setLifecycleOwner(this);
        // 让 ViewModel 拥有 View 的生命周期感应
        getLifecycle().addObserver(mViewModel);
        initViewBinding();
    }
    
    protected abstract int initLayoutId();
    
    @NonNull
    protected abstract VM initDataBinding();
    
    protected abstract void initViewBinding();
}
