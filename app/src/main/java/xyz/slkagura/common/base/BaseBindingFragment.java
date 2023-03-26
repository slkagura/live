package xyz.slkagura.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Lifecycle;

import com.trello.rxlifecycle4.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

public abstract class BaseBindingFragment<B extends ViewDataBinding> extends RxFragment {
    @NonNull
    protected final List<BaseViewModel> mViewModels = new ArrayList<>();
    
    protected B mBinding;
    
    protected int mLayoutId = initLayoutId();
    
    protected Context mContext;
    
    protected View mRoot;
    
    protected BaseBindingFragment() {
        super();
    }
    
    protected BaseBindingFragment(int contentLayoutId) {
        super(contentLayoutId);
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Observable.interval(1, TimeUnit.SECONDS).compose(bindToLifecycle()).subscribe();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        // 支持 LiveData 绑定 xml，数据改变，UI 自动会更新
        mBinding.setLifecycleOwner(this);
        initViewBinding();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, mLayoutId, container, false);
        mRoot = mBinding.getRoot();
        mContext = mRoot.getContext();
        return mRoot;
    }
    
    protected abstract int initLayoutId();
    
    protected abstract void initDataBinding(List<BaseViewModel> viewModels);
    
    protected void initViewBinding() {}
}
