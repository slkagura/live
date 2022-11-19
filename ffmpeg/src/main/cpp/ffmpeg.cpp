#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_xyz_slkagura_ffmpeg_NativeLib_stringFromJNI(JNIEnv *env, jobject ) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}