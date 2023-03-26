package xyz.slkagura.ui.view.opengl;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/3/26 14:21
 */
public class GLRenderer implements GLSurfaceView.Renderer {
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 关闭抗抖动
        gl.glDisable(GL10.GL_DITHER);
        // 设置系统对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        // 清空屏幕所用的颜色
        gl.glClearColor(0, 0, 0, 0);
    }
    
    /**
     * 渲染窗口大小发生改变或者屏幕方法发生变化时候回调
     *
     * @param gl     the GL interface. Use <code>instanceof</code> to
     *               test if the interface supports GL11 or higher interfaces.
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置适口尺寸
        GLES20.glViewport(0, 0, width, height);
    }
    
    /**
     * 执行渲染工作
     *
     * @param gl the GL interface. Use <code>instanceof</code> to
     *           test if the interface supports GL11 or higher interfaces.
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // glClear 使用默认颜色擦除屏幕
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        // glClearColor 使用指定的颜色擦除屏幕
        // GLES30.glClearColor(GLES20.GL_COLOR_BUFFER_BIT, 0f, 0f, 0f);
    }
}
