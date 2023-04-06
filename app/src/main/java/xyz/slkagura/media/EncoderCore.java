package xyz.slkagura.media;

import android.graphics.Bitmap;
import android.view.Surface;

import xyz.slkagura.media.opengl.EGLEnv;
import xyz.slkagura.media.opengl.EncoderProgram;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/4/5 13:36
 */
public class EncoderCore {
    private EGLEnv mEGLEnv;
    
    private EncoderProgram mEncoderProgram;
    
    public void build(Surface surface) {
        EGLEnv env = mEGLEnv.getInstance();
        if (env != null) {
            env.createWindowSurface(surface);
        }
        mEncoderProgram.build();
    }
    
    public void draw(Bitmap bitmap, long presentTime) {
        mEncoderProgram.renderBitmap(bitmap);
        // 给渲染的这一帧设置一个时间戳
        mEGLEnv.setPresentationTime(presentTime);
        mEGLEnv.swapBuffers();
    }
    
    public void release() {
        mEGLEnv.release();
        mEncoderProgram.release();
    }
}
