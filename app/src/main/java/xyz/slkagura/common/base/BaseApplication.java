package xyz.slkagura.common.base;

import android.app.Application;
import android.content.Context;

import xyz.slkagura.common.utils.ContextUtil;
import xyz.slkagura.common.utils.SizeUtil;
import xyz.slkagura.permission.PermissionManager;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtil.init(this);
        SizeUtil.init(this);
        PermissionManager.init(this);
    }
}
