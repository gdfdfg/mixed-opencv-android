//
// Created by Administrator on 2016/12/30.
//

#ifndef OPENCV_JAVA_COM_CHUANGWEIZONG_OPENCV_LIB_IMAGEPROCESS_CONTOUR_H
#define OPENCV_JAVA_COM_CHUANGWEIZONG_OPENCV_LIB_IMAGEPROCESS_CONTOUR_H


#endif //OPENCV_JAVA_COM_CHUANGWEIZONG_OPENCV_LIB_IMAGEPROCESS_CONTOUR_H

#include "log.h"
#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jintArray JNICALL Java_com_chuangweizong_opencv_lib_imageprocess_Contour_floodFill(JNIEnv *env, jobject instance,
                                                                         jintArray buf_, jint w, jint h,jint x, jint y);
JNIEXPORT jintArray JNICALL Java_com_chuangweizong_opencv_lib_imageprocess_Contour_floodFill2(JNIEnv *env, jobject instance,
        jobject img);
#ifdef __cplusplus
}
#endif
