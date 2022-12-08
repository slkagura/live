package xyz.slkagura.common.base;

import android.app.Application;

import xyz.slkagura.common.utils.ContextUtil;
import xyz.slkagura.common.utils.SizeUtil;
import xyz.slkagura.permission.PermissionUtil;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtil.init(this);
        SizeUtil.init(this);
        PermissionUtil.init(this);
    }
}
