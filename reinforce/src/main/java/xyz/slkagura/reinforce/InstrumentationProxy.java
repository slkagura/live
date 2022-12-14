package xyz.slkagura.reinforce;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

public class InstrumentationProxy extends Instrumentation {
    public static final String INSTRUMENTATION_PROXY_TAG = InstrumentationProxy.class.getSimpleName();
    
    // ActivityThread里面原始的Instrumentation对象,这里千万不能写成mInstrumentation,这样写
    //抛出异常，已亲测试，所以这个地方就要注意了
    public Instrumentation oldInstrumentation;
    
    //通过构造函数来传递对象
    public InstrumentationProxy(Instrumentation mInstrumentation) {
        oldInstrumentation = mInstrumentation;
    }
    
    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return oldInstrumentation.newActivity(cl, className, intent);
    }
    
    //这个方法是由于原始方法里面的Instrumentation有execStartActivity方法来定的
    @SuppressLint("DiscouragedPrivateApi")
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options) {
        Log.i(INSTRUMENTATION_PROXY_TAG, "<------------ Hook Method ------------>");
        Log.d(INSTRUMENTATION_PROXY_TAG, "startActivity 相关参数:");
        Log.d(INSTRUMENTATION_PROXY_TAG, "who = [" + who + "]");
        Log.d(INSTRUMENTATION_PROXY_TAG, "contextThread = [" + contextThread + "]");
        Log.d(INSTRUMENTATION_PROXY_TAG, "token = [" + token + "]");
        Log.d(INSTRUMENTATION_PROXY_TAG, "target = [" + target + "]");
        Log.d(INSTRUMENTATION_PROXY_TAG, "intent = [" + intent + "]");
        Log.d(INSTRUMENTATION_PROXY_TAG, "requestCode = [" + requestCode + "]");
        Log.d(INSTRUMENTATION_PROXY_TAG, "options = [" + options + "]");
        Log.i(INSTRUMENTATION_PROXY_TAG, "<------------ Hook Method ------------>");
        Log.i(INSTRUMENTATION_PROXY_TAG, "<------------ Hook Custom ------------>");
        Log.i(INSTRUMENTATION_PROXY_TAG, "这里可以做你在打开StartActivity方法之前的事情");
        Log.i(INSTRUMENTATION_PROXY_TAG, "<------------ Hook Custom ------------>");
        //由于这个方法是隐藏的，所以需要反射来调用，先找到这方法
        try {
            Method execStartActivity = Instrumentation.class.getDeclaredMethod("execStartActivity", Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
            execStartActivity.setAccessible(true);
            return (ActivityResult) execStartActivity.invoke(oldInstrumentation, who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            //如果你在这个类的成员变量Instrumentation的实例写错mInstrument,代码讲会执行到这里来
            throw new RuntimeException("if Instrumentation parameters is mInstrumentation, hook will fail");
        }
    }
}
