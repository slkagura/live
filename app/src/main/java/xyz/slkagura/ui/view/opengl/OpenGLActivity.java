package xyz.slkagura.ui.view.opengl;

import android.opengl.GLSurfaceView;

import java.util.List;

import xyz.slkagura.R;
import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.base.BaseViewModel;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.databinding.ActivityOpenglBinding;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/3/26 13:53
 */
public class OpenGLActivity extends BaseBindingActivity<ActivityOpenglBinding> {
    private OpenGLViewModel mViewModel;
    
    @Override
    protected int initLayoutId() {
        return R.layout.activity_opengl;
    }
    
    @Override
    protected void initDataBinding(List<BaseViewModel> viewModels) {
        mViewModel = ViewModelUtil.get(this, OpenGLViewModel.class);
        viewModels.add(mViewModel);
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setV(this);
        mBinding.setVm(mViewModel);
        mBinding.actOpenglSurfaceView.setEGLContextClientVersion(3);
        // 设置 renderer
        mBinding.actOpenglSurfaceView.setRenderer(new GLRenderer());
        // 渲染方式
        // RENDERMODE_WHEN_DIRTY 表示被动渲染，只有在调用 requestRender 或者 onResume 等方法时才会进行渲染。
        // RENDERMODE_CONTINUOUSLY 表示持续渲染
        mBinding.actOpenglSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mBinding.actOpenglSurfaceView.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mBinding.actOpenglSurfaceView.onPause();
    }
}
