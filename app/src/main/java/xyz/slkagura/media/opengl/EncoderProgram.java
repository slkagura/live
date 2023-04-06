package xyz.slkagura.media.opengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/4/5 14:51
 */
public class EncoderProgram {
    private static final String TAG = EncoderProgram.class.getSimpleName();
    
    private int mProgram = 0;
    
    private FloatBuffer mVertexBuffer;
    
    private IntBuffer mIndexBuffer;
    
    private FloatBuffer mTextureBuffer;
    
    private int mTextureId = 0;
    
    private int mPosHandle = -1;
    
    private int mTextureHandle = -1;
    
    private int mTexture2Handle = -1;
    
    public EncoderProgram() {
        float[] vertex = new float[] {
            -1f, 1f, 0f, -1f, -1f, 0f, 1f, -1f, 0f, 1f, 1f, 0f
        };
        mVertexBuffer = ByteBuffer.allocateDirect(vertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(vertex).position(0);
        int[] index = new int[] { 0, 1, 2, 0, 3, 2 };
        mIndexBuffer = IntBuffer.allocate(index.length).put(index);
        mIndexBuffer.position(0);
        float[] texture = new float[] {
            0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f
        };
        mTextureBuffer = ByteBuffer.allocateDirect(texture.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer.put(texture).position(0);
    }
    
    public void build() {
        mProgram = GLUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        mPosHandle = GLUtil.getAttribLocation(program, "position");
        mTextureHandle = GLUtil.getAttribLocation(program, "aTexCoord");
        mTexture2Handle = GLUtil.getUniformLocation(program, "texture");
        mTextureId = buildTextureId(GLES20.GL_TEXTURE_2D);
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        // 开启纹理透明混合，这样才能绘制透明图片
        // GLES20.glEnable(GL10.GL_BLEND)
        // GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glViewport(0, 0, size.width, size.height);
    }
    
    fun renderBitmap(b:Bitmap) {
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT)
        
        // 设置当前活动的纹理单元为纹理单元0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // 将纹理ID绑定到当前活动的纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID)
        
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, b, 0)
        b.recycle()
        
        // 顶点坐标
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT,
            false, 12, vertexBuffer)
        
        // 纹理坐标
        GLES20.glEnableVertexAttribArray(texHandle)
        GLES20.glVertexAttribPointer(texHandle, 2, GLES20.GL_FLOAT,
            false, 0, texBuffer)
        
        GLES20.glUniform1i(texHandle, 0)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6,
            GLES20.GL_UNSIGNED_INT, indexBuffer)
        unBindTexture(GLES20.GL_TEXTURE_2D)
    }
    
    public void release() {
        releaseTexture(intArrayOf(textureID));
    }
}
