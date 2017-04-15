#include <jni.h>

#ifndef __JTCLIP__
#define __JTCLIP__

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_opencv_TClip
 * Method:    crop
 * Signature: (Ljava/lang/String;Landroid/graphics/Bitmap;II)Landroid/graphics/Bitmap;
 */
JNIEXPORT jobject JNICALL Java_com_chuangweizong_opencv_lib_detect_TClip_crop(JNIEnv *env, jobject instance, jstring config_,
                                                            jobject src, jint width, jint height);
#ifdef __cplusplus
}
#endif

#endif
