package xyz.slkagura.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Lifecycle;

import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBindingActivity<B extends ViewDataBinding> extends RxAppCompatActivity {
    @NonNull
    protected final List<BaseViewModel> mViewModels = new ArrayList<>();
    
    protected B mBinding;
    
    protected int mLayoutId = initLayoutId();
    
    protected Context mContext;
    
    protected View mRoot;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayoutId);
        initDataBinding(mViewModels);
        // 让 ViewModel 拥有 View 的生命周期感应
        Lifecycle lifecycle = getLifecycle();
        if (!mViewModels.isEmpty()) {
            for (BaseViewModel viewModel : mViewModels) {
                if (viewModel != null) {
                    lifecycle.addObserver(viewModel);
                }
            }
        }
        mBinding = DataBindingUtil.setContentView(this, mLayoutId);
        mRoot = mBinding.getRoot();
        mContext = mRoot.getContext();
        // 支持 LiveData 绑定 xml，数据改变，UI 自动会更新
        mBinding.setLifecycleOwner(this);
        initViewBinding();
    }
    
    protected abstract int initLayoutId();
    
    protected abstract void initDataBinding(List<BaseViewModel> viewModels);
    
    protected abstract void initViewBinding();
}
