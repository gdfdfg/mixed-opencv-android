//
// Created by Administrator on 2017/2/2.
//

#ifndef OPENCV_TEXTIMAGECORRECT_H
#define OPENCV_TEXTIMAGECORRECT_H

#endif //OPENCV_TEXTIMAGECORRECT_H

#include "log.h"
#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jint JNICALL
        Java_com_chuangweizong_opencv_lib_TextImageCorrect_getTextImageCorrectFromeNative(JNIEnv *env,
                                                                                          jclass type,
                                                                                          jobject bitmap,
                                                                                          jlong matAddrGr,
                                                                                          jlong matAddrRgba,jstring path);
#ifdef __cplusplus
}
#endif
