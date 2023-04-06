package xyz.slkagura.media.opengl;

import android.opengl.EGL14;
import android.opengl.GLES20;

import xyz.slkagura.common.extension.log.Log;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/4/5 23:10
 */
public class GLUtil {
    private static final String TAG = GLUtil.class.getSimpleName();
    
    /**
     * 顶点着色器
     */
    private static final String COMMON_VERTEX_SHADER = "attribute vec4 vPosition;\n" + "attribute vec4 vTexCoordinate;\n" + "uniform mat4 textureTransform;\n" + "varying vec2 v_TexCoordinate;\n" + "void main () {\n" + "    v_TexCoordinate = (textureTransform * vTexCoordinate).xy;\n" + "    gl_Position = vPosition;\n" + "}";
    
    // 片元着色器
    private static final String COMMON_FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
        "precision highp float;\n" +
        "uniform samplerExternalOES texture;\n" +
        "varying highp vec2 v_TexCoordinate;\n" +
        "void main () {\n" +
        "    gl_FragColor = texture2D(texture, v_TexCoordinate);\n" +
        "}";
    
    public static int createCommonProgram() {
        return createProgram(COMMON_VERTEX_SHADER, COMMON_FRAGMENT_SHADER);
    }
    
    /**
     * 创建一个显卡可执行程序，运行在GPU
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        int[] maxVertexAttribute = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_ATTRIBS, maxVertexAttribute, 0);
        Log.d(TAG, "createProgram() max vertex attribute: ", maxVertexAttribute[0]);
        
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        Log.d(TAG, "createProgram() vertex shader: ", vertexShader);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        Log.d(TAG, "createProgram() vertex shader: ", vertexShader);
        if (pixelShader == 0) {
            return 0;
        }
        
        // 创建一个显卡可执行程序
        var program = GLES20.glCreateProgram();
        if (program == 0) {
            Log.e(TAG, "Could not create program");
        }
        // 将编译好的shader着色器加载到这个可执行程序上
        GLES20.glAttachShader(program, vertexShader);
        checkEGLError("glAttachShader");
        GLES20.glAttachShader(program, pixelShader);
        checkEGLError("glAttachShader");
        // 链接程序
        GLES20.glLinkProgram(program);
        // 检查程序状态，第三个参数是返回值，返回1就是成功，返回0就是失败
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        if (program == 0) {
            throw new RuntimeException("create GPU program failed");
        }
        GLES20.glUseProgram(program);
        return program;
    }
    
    public static int loadShader(int shaderType, String source) {
        // 创建一个对象，作为shader容器，此函数返回容器对象地址
        int shader = GLES20.glCreateShader(shaderType);
        checkEGLError("glCreateShader type=$shaderType");
        // 为shader添加源代码，shader content（着色器程序，根据GLSL语法和内嵌函数编写）
        // 将开发者编写的着色器程序加载到着色器对象的内存中
        GLES20.glShaderSource(shader, source);
        // 编译这个着色器
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        // 验证是否编译成功.第二个参数是需要验证shader的状态值。第三个参数是返回值，返回1说明成功，返回0则不成功
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader $shaderType:");
            // 创建失败，打印日志
            Log.e(TAG, "shader id: ", GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }
    
    public static void checkLocation(int location, String label) {
        if (location < 0) {
            throw new RuntimeException("Unable to locate " + label + " in program");
        }
    }
    
    public static void checkEGLError(String message) {
        int error = EGL14.eglGetError();
        if (error != EGL14.EGL_SUCCESS) {
            throw new RuntimeException("EGL error: 0x" + Integer.toHexString(error) + " message: " + message);
        }
    }
    
    public static void checkGLError(String message) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            throw new RuntimeException("OpenGL error: 0x" + Integer.toHexString(error) + " message: " + message);
        }
    }
    
    public static int getAttribLocation(int program, String name) {
        int location = GLES20.glGetAttribLocation(program, name);
        checkLocation(location, name);
        return location;
    }
    
    public static int getUniformLocation(int program, String name) {
        int uniform = GLES20.glGetUniformLocation(program, name);
        checkLocation(uniform, name);
        return uniform;
    }
}
