package xyz.slkagura.ui.view.live;

import android.view.TextureView;

import java.util.List;

import xyz.slkagura.R;
import xyz.slkagura.camera.interfaces.ICameraHelperCallback;
import xyz.slkagura.common.base.BaseBindingFragment;
import xyz.slkagura.common.base.BaseViewModel;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.databinding.FragmentLiveBinding;
import xyz.slkagura.live.tag.LiveState;
import xyz.slkagura.ui.component.StreamPanel;

public class LiveFragment extends BaseBindingFragment<FragmentLiveBinding> implements LiveViewModel.IHandler, StreamPanel.IHandler, ICameraHelperCallback {
    private LiveViewModel mViewModel;
    
    public static LiveFragment getInstance() {
        return new LiveFragment();
    }
    
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_live;
    }
    
    @Override
    protected void initDataBinding(List<BaseViewModel> viewModels) {
        mViewModel = ViewModelUtil.get(this, LiveViewModel.class);
        viewModels.add(mViewModel);
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
