package xyz.slkagura.ui.view.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.utils.LogUtil;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.thread.TaskQueue;
import xyz.slkagura.ui.R;
import xyz.slkagura.ui.databinding.ActivityMainBinding;
import xyz.slkagura.ui.view.live.LiveFragment;

public class MainActivity extends BaseBindingActivity<MainViewModel, ActivityMainBinding> implements MainViewModel.Handler {
    private static final String MAIN_ACTIVITY_TAG = MainActivity.class.getSimpleName();
    
    @Override
    protected int initLayoutId() {
        return R.layout.activity_main;
    }
    
    @NonNull
    @Override
    protected MainViewModel initDataBinding() {
        return ViewModelUtil.get(this, MainViewModel.class);
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setVm(mViewModel);
        mBinding.setHandler(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // initFragment();
        onStartClick();
    }
    
    @Override
    public void onStartClick() {
        TaskQueue consumer = new TaskQueue();
        for (int i = 0; i < 100; i++) {
            final int id = i;
            boolean isSync = Math.random() < 0.2D;
            String groupId = isSync ? "group-1" : "group-2";
            consumer.offer(() -> {
                LogUtil.d(MAIN_ACTIVITY_TAG, "task: ", id, " group: ", groupId, " sync: ", String.valueOf(isSync), " start: ", System.nanoTime());
                if (isSync) {
                    consumer.unlock(groupId);
                }
                LogUtil.d(MAIN_ACTIVITY_TAG, "task: ", id, " group: ", groupId, " sync: ", String.valueOf(isSync), " end: ", System.nanoTime());
            }, groupId, isSync);
        }
    }
    
    private void initFragment() {
        Fragment fragment = LiveFragment.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.activity_main_cl_container, fragment).addToBackStack("live").commit();
    }
}
