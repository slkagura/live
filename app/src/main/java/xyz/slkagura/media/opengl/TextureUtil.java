package xyz.slkagura.media.opengl;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/4/5 23:54
 */
public class TextureUtil {
    public static int buildTextureId(target: Int = GLES11Ext.GL_TEXTURE_EXTERNAL_OES) {
        val ids = IntArray(1)
        GLES20.glGenTextures(1, ids, 0)
        checkGlError("create texture check")
        val id = ids[0]
        bindSetTexture(target, id)
        return id
    }
}
