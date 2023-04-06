package xyz.slkagura.ui.view.grafika;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import xyz.slkagura.common.extension.log.Log;
import xyz.slkagura.grafika.gles.FullFrameRect;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/4/5 20:44
 */
public class GLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = GLRenderer.class.getSimpleName();
    
    private GLSurfaceView mGLSurfaceView;
    
    private SurfaceTexture mSurfaceTexture;
    
    private FullFrameRect mFullScreen;
    
    /**
     * width/height of the incoming camera preview frames
     */
    private boolean mIncomingSizeUpdated;
    
    private int mIncomingWidth;
    
    private int mIncomingHeight;
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    
    }
    
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
    
    }
    
    /**
     * Notifies the renderer thread that the activity is pausing.
     * For best results, call this *after* disabling Camera preview.
     */
    public void pause() {
        Log.v(TAG, "pause() Call");
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mFullScreen != null) {
            // assume the GLSurfaceView EGL context is about
            mFullScreen.release(false);
            //  to be destroyed
            mFullScreen = null;
        }
        mIncomingWidth = -1;
        mIncomingHeight = -1;
        Log.v(TAG, "pause() End");
    }
    
    /**
     * Records the size of the incoming camera preview frames.
     * It's not clear whether this is guaranteed to execute before or after onSurfaceCreated(),
     * so we assume it could go either way.  (Fortunately they both run on the same thread,
     * so we at least know that they won't execute concurrently.)
     */
    public void setCameraPreviewSize(int width, int height) {
        Log.v(TAG, "setCameraPreviewSize() Call");
        Log.d(TAG, "setCameraPreviewSize() width=", width, " height=", height);
        mIncomingWidth = width;
        mIncomingHeight = height;
        mIncomingSizeUpdated = true;
        Log.v(TAG, "setCameraPreviewSize() End");
    }
}
