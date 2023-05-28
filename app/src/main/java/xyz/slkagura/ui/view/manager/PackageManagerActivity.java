package xyz.slkagura.ui.view.manager;

import android.content.pm.PackageInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import xyz.slkagura.R;
import xyz.slkagura.common.base.BaseBindingActivity;
import xyz.slkagura.common.base.BaseViewModel;
import xyz.slkagura.common.utils.ToastUtil;
import xyz.slkagura.common.utils.ViewModelUtil;
import xyz.slkagura.databinding.ActivityPackageManagerBinding;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/5/14 17:12
 */
public class PackageManagerActivity extends BaseBindingActivity<ActivityPackageManagerBinding> {
    private final PackagesAdapter mPackagesAdapter = new PackagesAdapter();
    
    private PackageManagerViewModel mViewModel;
    
    public void onRefreshClick() {
        mViewModel.refresh();
    }
    
    public void onSaveClick() {
        mViewModel.save();
    }
    
    @Override
    protected int initLayoutId() {
        return R.layout.activity_package_manager;
    }
    
    @Override
    protected void initDataBinding(@NonNull List<BaseViewModel> list) {
        mViewModel = ViewModelUtil.get(this, PackageManagerViewModel.class);
        if (mViewModel != null) {
            mViewModel.getPackages().observe(this, this::onPackagesUpdate);
            list.add(mViewModel);
        }
    }
    
    @Override
    protected void initViewBinding() {
        mBinding.setV(this);
        mBinding.setVm(mViewModel);
        mPackagesAdapter.setOnItemClickListener((adapter, view, position) -> {
            PackageInfo info = (PackageInfo) adapter.getItem(position);
            if (info != null) {
                ToastUtil.show(info.packageName, true);
            }
        });
        mBinding.rvPackages.setAdapter(mPackagesAdapter);
        mBinding.rvPackages.setLayoutManager(new GridLayoutManager(mContext, 1));
    }
    
    private void onPackagesUpdate(List<PackageInfo> packages) {
        mPackagesAdapter.setList(packages);
    }
    
    private class PackagesAdapter extends BaseQuickAdapter<PackageInfo, BaseViewHolder> {
        public PackagesAdapter() {
            super(R.layout.item_package);
        }
        
        @Override
        protected void convert(@NonNull BaseViewHolder holder, PackageInfo info) {
            holder.setText(R.id.tv_package, info.packageName);
            holder.setText(R.id.tv_label, mViewModel.loadLabel(info));
        }
    }
}
