package xyz.slkagura.ui;

import android.app.Application;

import xyz.slkagura.common.utils.ContextUtil;
import xyz.slkagura.common.utils.SizeUtil;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtil.init(this);
        SizeUtil.init(this);
    }
}
