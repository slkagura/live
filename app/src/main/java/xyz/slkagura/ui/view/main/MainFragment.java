package xyz.slkagura.ui.view.main;

import android.view.TextureView;

import androidx.annotation.NonNull;

import xyz.slkagura.camera.interfaces.ICameraHelperCallback;
import xyz.slkagura.common.base.BaseBindingFragment;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.live.tag.LiveState;
import xyz.slkagura.ui.BR;
import xyz.slkagura.ui.R;
import xyz.slkagura.ui.databinding.FragmentMainBinding;

public class MainFragment extends BaseBindingFragment<MainViewModel, FragmentMainBinding> implements MainViewModel.IHandler, ICameraHelperCallback {
    public static MainFragment getInstance() {
        MainFragment fragment = new MainFragment();
        fragment.mContainerId = R.layout.fragment_main;
        fragment.mVariableId = BR.vm;
        return fragment;
    }
    
    @NonNull
    @Override
    protected MainViewModel initViewModel() {
        return ViewModelUtil.get(this, MainViewModel.class);
    }
    
    @Override
    protected void initViewDataBinding() {
        mBinding.setVm(mViewModel);
        mBinding.setHandler(this);
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }
    
    @Override
    public void onLive() {
        if (mViewModel.mPreState == LiveState.CREATED) {
            TextureView textureView = mViewModel.resumeLive();
            if (textureView != null) {
                mBinding.fragmentDecodeFlPreview.addView(textureView);
            }
        } else {
            onStopLive();
        }
    }
    
    @Override
    public void onStopLive() {
        mViewModel.pauseLive();
        mBinding.fragmentDecodeFlPreview.removeAllViews();
    }
}
