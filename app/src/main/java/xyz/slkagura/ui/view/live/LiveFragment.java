package xyz.slkagura.ui.view.live;

import android.view.TextureView;

import androidx.annotation.NonNull;

import xyz.slkagura.camera.interfaces.ICameraHelperCallback;
import xyz.slkagura.common.base.BaseBindingFragment;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.live.tag.LiveState;
import xyz.slkagura.ui.BR;
import xyz.slkagura.ui.R;
import xyz.slkagura.ui.component.StreamPanel;
import xyz.slkagura.ui.databinding.FragmentLiveBinding;

public class LiveFragment extends BaseBindingFragment<LiveViewModel, FragmentLiveBinding> implements LiveViewModel.IHandler, StreamPanel.IHandler, ICameraHelperCallback {
    public static LiveFragment getInstance() {
        return new LiveFragment();
    }
    
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_live;
    }
    
    @NonNull
    @Override
    protected LiveViewModel initDataBinding() {
        return ViewModelUtil.get(this, LiveViewModel.class);
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setVm(mViewModel);
        mBinding.setHandler(this);
        mBinding.fragmentMainComponentStream.setVm(new StreamPanel());
        mBinding.fragmentMainComponentStream.setHandler(this);
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
    public void onCommandClick() {
    }
    
    @Override
    public void onLive() {
        if (mViewModel.mPreState == LiveState.CREATED) {
            TextureView textureView = mViewModel.resumeLive();
            if (textureView != null) {
                mBinding.fragmentMainComponentStream.componentStreamFlStream.addView(textureView);
            }
        } else {
            onStopLive();
        }
    }
    
    @Override
    public void onStopLive() {
        mViewModel.pauseLive();
        mBinding.fragmentMainComponentStream.componentStreamFlStream.removeAllViews();
    }
}
