package xyz.slkagura.media.opengl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Nullable;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/4/5 14:05
 */
public class EGLEnv {
    private static final String TAG = EGLEnv.class.getSimpleName();
    
    private final int mWidth;
    
    private final int mHeight;
    
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    
    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;
    
    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    
    private EGLConfig mEGLConfig;
    
    public EGLEnv(int width, int height) {
        mWidth = width;
        mHeight = height;
    }
    
    public EGLEnv getInstance() {
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            GLUtil.checkEGLError("Can't load EGL display");
        }
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            GLUtil.checkEGLError("Can't initialize EGL environment");
        }
        int[] attributes = {
            EGL14.EGL_BUFFER_SIZE,
            32,
            EGL14.EGL_ALPHA_SIZE,
            8,
            EGL14.EGL_BLUE_SIZE,
            8,
            EGL14.EGL_GREEN_SIZE,
            8,
            EGL14.EGL_RED_SIZE,
            8,
            EGL14.EGL_RENDERABLE_TYPE,
            EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_SURFACE_TYPE,
            EGL14.EGL_WINDOW_BIT,
            EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] configsCount = new int[1];
        if (!EGL14.eglChooseConfig(mEGLDisplay, attributes, 0, configs, 0, configs.length, configsCount, 0)) {
            GLUtil.checkEGLError("Can't choose EGL config");
        }
        mEGLConfig = configs[0];
        attributes = new int[] {
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE
        };
        mEGLContext = EGL14.eglCreateContext(mEGLDisplay, mEGLConfig, EGL14.EGL_NO_CONTEXT, attributes, 0);
        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
            GLUtil.checkEGLError("Can't create EGL context");
        }
        return this;
    }
    
    public EGLEnv createSurface(@Nullable Surface surface) {
        if (surface == null) {
            Log.d(TAG, "build off screen surface");
            return createOffScreenSurface();
        } else {
            Log.d(TAG, "build window surface");
            return createWindowSurface(surface);
        }
    }
    
    /**
     * 创建一个可实际显示的windowSurface
     *
     * @param surface 本地设备屏幕
     */
    public EGLEnv createWindowSurface(Surface surface) {
        int[] format = new int[1];
        if (!EGL14.eglGetConfigAttrib(mEGLDisplay, mEGLConfig, EGL14.EGL_NATIVE_VISUAL_ID, format, 0)) {
            GLUtil.checkEGLError("Can't get EGL config attribute");
        }
        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("EGL already config surface");
        }
        int[] surfaceAttributes = { EGL14.EGL_NONE };
        mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, surface, surfaceAttributes, 0);
        if (mEGLSurface == EGL14.EGL_NO_SURFACE) {
            GLUtil.checkEGLError("Can't crete EGL window surface");
        }
        makeCurrent();
        return this;
    }
    
    /**
     * 创建离线Surface
     */
    public EGLEnv createOffScreenSurface() {
        // EGL 和 OpenGL ES 环境搭建完毕，OpenGL 输出可以获得。接着是 EGL 和设备连接
        // 连接工具是：EGLSurface，这是一个 FrameBuffer
        int[] attributes = {
            EGL14.EGL_WIDTH, mWidth, EGL14.EGL_HEIGHT, mHeight, EGL14.EGL_NONE
        };
        mEGLSurface = EGL14.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig, attributes, 0);
        if (mEGLSurface == EGL14.EGL_NO_SURFACE) {
            GLUtil.checkEGLError("Can't create EGL buffer");
        }
        makeCurrent();
        return this;
    }
    
    /**
     * 为此线程绑定上下文
     */
    private void makeCurrent() {
        Log.d(TAG, " egl make current ");
        if (!EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
            GLUtil.checkEGLError("EGL make current failed");
        }
    }
    
    /**
     * EGL是双缓冲机制，Back Frame Buffer和Front Frame Buffer，正常绘制目标都是Back Frame Buffer
     * 将绘制完毕的FrameBuffer交换到Front Frame Buffer 并显示出来
     */
    public boolean swapBuffers() {
        boolean result = EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
        GLUtil.checkEGLError("eglSwapBuffers");
        return result;
    }
    
    public void release() {
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(mEGLDisplay);
        }
        mEGLSurface = EGL14.EGL_NO_SURFACE;
        mEGLContext = EGL14.EGL_NO_CONTEXT;
        mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    }
    
    /**
     * @param ts 纳秒 10^-9
     */
    public void setPresentationTime(long ts) {
        EGLExt.eglPresentationTimeANDROID(mEGLDisplay, mEGLSurface, ts);
        GLUtil.checkEGLError("eglPresentationTimeANDROID");
    }
}
