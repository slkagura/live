package xyz.slkagura.reinforce;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Starter extends Application {
    private static final String STARTER_TAG = Starter.class.getSimpleName();
    
    static {
        System.loadLibrary("reinforce");
    }
    
    private Context mContextImpl;
    
    private Application mDelegateApplication;
    
    @NonNull
    private static native String getThread();
    
    @NonNull
    private static native String getCurrentThread();
    
    @NonNull
    private static native String getInstr();
    
    @NonNull
    private static native String getAppName();
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.v(STARTER_TAG, "Starter On Load");
        Log.v(STARTER_TAG, "Set ContextImpl");
        mContextImpl = base;
        Log.v(STARTER_TAG, "Attach Application");
        attachApp();
        Log.v(STARTER_TAG, "Attach Activity");
        attachActivity();
    }
    
    @Override
    public String getPackageName() {
        if (TextUtils.isEmpty(getAppName())) {
            // 如果 AndroidManifest.xml 中配置的 Application 全类名为空，不做任何操作
            return super.getPackageName();
        } else {
            // 如果 AndroidManifest.xml 中配置的 Application 全类名不为空
            // 为了使 ActivityThread 的 installProvider 方法
            // 无法命中如下两个分支
            // 分支一：context.getPackageName().equals(ai.packageName)
            // 分支二：mInitialApplication.getPackageName().equals(ai.packageName)
            // 设置该方法返回值为空，上述两个分支就无法命中
            return "";
        }
    }
    
    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        if (TextUtils.isEmpty(getAppName())) {
            // 如果 AndroidManifest.xml 中配置的 Application 全类名为空
            // 说明没有进行 dex 加密操作，返回父类方法执行即可
            return super.createPackageContext(packageName, flags);
        } else {
            // 只有在创建 ContentProvider 时才调用到该 createPackageContext 方法
            // 如果没有调用到该方法，说明该应用中没有配置 ContentProvider；
            // 该方法不一定会调用到
            // 先进行 Application 替换
            attachApp();
            // Application 替换完成之后，再继续向下执行创建 ContentProvider
            return mDelegateApplication;
        }
    }
    
    @SuppressLint({ "PrivateApi", "DiscouragedPrivateApi" })
    private void attachApp() {
        // 通过反射获取 Application，系统也是进行的反射操作
        Class<?> delegateClass;
        try {
            delegateClass = Class.forName(getAppName());
            // 创建用户真实配置的 Application
            mDelegateApplication = (Application) delegateClass.newInstance();
            // 调用 Application 的 attach 函数
            // 该函数无法直接调用，也需要通过反射调用
            // 这里先通过反射获取 Application 的 attach 函数
            Method attach = Application.class.getDeclaredMethod("attach", Context.class);
            // attach 方法是私有的，设置 attach 方法允许访问
            attach.setAccessible(true);
            // 获取上下文对象
            // 该 Context 是通过调用 Application 的 attachBaseContext 方法传入的 ContextImpl
            // 将该上下文对象传入 Application 的 attach 方法中
            attach.invoke(mDelegateApplication, mContextImpl);
            /*
            参考 : https://hanshuliang.blog.csdn.net/article/details/111569017 博客
            查询应该替换哪些对象中的哪些成员
            截止到此处, Application 创建完毕，下面开始逐个替换下面的 Application
            ① ContextImpl 的 private Context mOuterContext
                成员是 ProxyApplication 对象；
            ② ActivityThread 中的 ArrayList<Application> mAllApplications
                集合中添加了 ProxyApplication 对象；
            ③ LoadedApk 中的 mApplication 成员是 ProxyApplication 对象；
            ④ ActivityThread 中的 Application mInitialApplication
                成员是 ProxyApplication 对象；
             */
            // I. 替换 ① ContextImpl 的 private Context mOuterContext
            // 成员是 ProxyApplication 对象
            Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
            // 获取 ContextImpl 中的 mOuterContext 成员
            Field mOuterContextField = contextImplClass.getDeclaredField("mOuterContext");
            // mOuterContext 成员是私有的，设置可访问性
            mOuterContextField.setAccessible(true);
            // ContextImpl 就是应用的 Context，直接通过 getBaseContext() 获取即可
            mOuterContextField.set(mContextImpl, mDelegateApplication);
            // II. 替换 ④ ActivityThread 中的 Application mInitialApplication
            // 成员是 ProxyApplication 对象；
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            // 获取 ActivityThread 中的 mInitialApplication 成员
            Field mInitialApplicationField = activityThreadClass.getDeclaredField("mInitialApplication");
            // mInitialApplication 成员是私有的，设置可访问性
            mInitialApplicationField.setAccessible(true);
            // 从 ContextImpl 对象中获取其 ActivityThread mMainThread 成员变量
            Field mMainThreadField = contextImplClass.getDeclaredField("mMainThread");
            mMainThreadField.setAccessible(true);
            // ContextImpl 就是本应用的上下文对象，调用 getBaseContext 方法获得
            Object mMainThread = mMainThreadField.get(mContextImpl);
            // ContextImpl 就是应用的 Context，直接通过 getBaseContext() 获取即可
            mInitialApplicationField.set(mMainThread, mDelegateApplication);
            // III. 替换 ② ActivityThread 中的 ArrayList<Application> mAllApplications
            // 集合中添加了 ProxyApplication 对象；
            // 获取 ActivityThread 中的 mAllApplications 成员
            Field mAllApplicationsField = activityThreadClass.getDeclaredField("mAllApplications");
            // mAllApplications 成员是私有的，设置可访问性
            mAllApplicationsField.setAccessible(true);
            // 获取 ActivityThread 中的 ArrayList<Application> mAllApplications 队列
            Object object = mAllApplicationsField.get(mMainThread);
            ArrayList<Application> mAllApplications = null;
            if (object instanceof ArrayList) {
                mAllApplications = new ArrayList<>();
                for (Object element : (ArrayList<?>) object) {
                    if (element instanceof Application) {
                        mAllApplications.add(((Application) element));
                    }
                }
            }
            // 将真实的 Application 添加到上述队列中
            if (mAllApplications == null) {
                return;
            }
            mAllApplications.add(mDelegateApplication);
            // IV. 替换 ③ LoadedApk 中的 mApplication
            // 成员是 ProxyApplication 对象
            // 1. 先获取 LoadedApk 对象
            // LoadedApk 是 ContextImpl 中的 LoadedApk mPackageInfo 成员变量
            // 从 ContextImpl 对象中获取其 LoadedApk mPackageInfo 成员变量
            Field mPackageInfoField = contextImplClass.getDeclaredField("mPackageInfo");
            mPackageInfoField.setAccessible(true);
            // ContextImpl 就是本应用的上下文对象，调用 getBaseContext 方法获得
            Object mPackageInfo = mPackageInfoField.get(mContextImpl);
            // 2. 获取 LoadedApk 对象中的 mApplication 成员
            Class<?> loadedApkClass = Class.forName("android.app.LoadedApk");
            // 获取 ActivityThread 中的 mInitialApplication 成员
            Field mApplicationField = loadedApkClass.getDeclaredField("mApplication");
            // LoadedApk 中的 mApplication 成员是私有的，设置可访问性
            mApplicationField.setAccessible(true);
            // 3. 将 Application 设置给 LoadedApk 中的 mApplication 成员
            mApplicationField.set(mPackageInfo, mDelegateApplication);
            // V. 下一步操作替换替换 ApplicationInfo 中的 className，该操作不是必须的，不替换也不会报错
            // 在应用中可能需要操作获取应用的相关信息，如果希望获取准确的信息，需要替换 ApplicationInfo
            // ApplicationInfo 在 LoadedApk 中
            Field mApplicationInfoField = loadedApkClass.getDeclaredField("mApplicationInfo");
            // 设置该字段可访问
            mApplicationInfoField.setAccessible(true);
            // mPackageInfo 就是 LoadedApk 对象
            // mApplicationInfo 就是从 LoadedApk 对象中获得的 mApplicationInfo 字段
            ApplicationInfo mApplicationInfo = (ApplicationInfo) mApplicationInfoField.get(mPackageInfo);
            // 设置 ApplicationInfo 中的 className 字段值
            if (mApplicationInfo == null) {
                return;
            }
            mApplicationInfo.className = getAppName();
            // 再次调用 onCreate 方法
            mDelegateApplication.onCreate();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException |
            InvocationTargetException | NoSuchFieldException e) {
            // throw new RuntimeException(e);
            throw new RuntimeException("Error");
        }
    }
    
    @SuppressLint({ "PrivateApi", "DiscouragedPrivateApi" })
    public void attachActivity() {
        // 获取当前的ActivityThread对象
        Class<?> activityThreadClass;
        try {
            // PrivateApi
            activityThreadClass = Class.forName(getThread());
            // DiscouragedPrivateApi
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod(getCurrentThread());
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);
            // 拿到在ActivityThread类里面的原始mInstrumentation对象
            // DiscouragedPrivateApi
            Field mInstrumentationField = activityThreadClass.getDeclaredField(getInstr());
            mInstrumentationField.setAccessible(true);
            Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
            // 构建我们的代理对象
            Instrumentation evilInstrumentation = new InstrumentationProxy(mInstrumentation);
            // 通过反射，换掉字段，注意，这里是反射的代码，不是Instrumentation里面的方法
            mInstrumentationField.set(currentActivityThread, evilInstrumentation);
            // 做个标记，方便后面查看
            Log.i(STARTER_TAG, "has go in MyApplication attachContext method");
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException |
            IllegalAccessException e) {
            // throw new RuntimeException(e);
            throw new RuntimeException("Error");
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        // 如果之前没有替换过，执行 Application 替换操作
        // 说明没有调用到 createPackageContext 方法
        // 该 createPackageContext 方法只有在创建 ContentProvider 时才调用到
        // 如果没有调用到，说明 AndroidManifest.xml 中没有配置 ContentProvider
        // 此时需要在此处进行 Application 替换
        if (mDelegateApplication == null) {
            attachApp();
        }
    }
}
