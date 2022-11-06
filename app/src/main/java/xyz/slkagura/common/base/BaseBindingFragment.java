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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.trello.rxlifecycle4.components.support.RxFragment;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

public abstract class BaseBindingFragment<VM extends BaseBindingViewModel, B extends ViewDataBinding> extends RxFragment {
    protected VM mViewModel;
    
    protected B mBinding;
    
    protected int mContainerId;
    
    protected int mVariableId;
    
    protected Context mContext;
    
    protected View mView;
    
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
        mViewModel = initViewModel();
        // 设置 Variable
        mBinding.setVariable(mVariableId, mViewModel);
        // 支持 LiveData 绑定 xml，数据改变，UI 自动会更新
        mBinding.setLifecycleOwner(this);
        // 让 ViewModel 拥有 View 的生命周期感应
        getLifecycle().addObserver(mViewModel);
        initViewDataBinding();
    }
    
    @NonNull
    protected abstract VM initViewModel();
    
    protected void initViewDataBinding() {}
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, mContainerId, container, false);
        mView = mBinding.getRoot();
        mContext = mView.getContext();
        return mView;
    }
}
