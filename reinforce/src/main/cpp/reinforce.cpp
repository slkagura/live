//
// Created by Kagura on 2022/12/15.
//

// STD
#include <jni.h>
#include <string>

// Custom
#include "reinforce.h"

extern "C" JNIEXPORT jstring JNICALL
Java_xyz_slkagura_reinforce_Starter_getThread(JNIEnv *env, jclass clazz) {
    std::string result = "android.app.ActivityThread";
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_xyz_slkagura_reinforce_Starter_getCurrentThread(JNIEnv *env, jclass clazz) {
    std::string result = "currentActivityThread";
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_xyz_slkagura_reinforce_Starter_getInstr(JNIEnv *env, jclass clazz) {
    std::string result = "mInstrumentation";
    return env->NewStringUTF(result.c_str());
}
extern "C" JNIEXPORT jstring JNICALL
Java_xyz_slkagura_reinforce_Starter_getAppName(JNIEnv *env, jclass clazz) {
    std::string result = "xyz.slkagura.ui.LiveApplication";
    return env->NewStringUTF(result.c_str());
}