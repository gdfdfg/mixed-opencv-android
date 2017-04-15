//
// Created by Administrator on 2016/12/30.
//
#include <jni.h>
#include <vector>
#include <string>
#include <opencv2/core/core.hpp>
#include <cv.h>
#include <android/bitmap.h>
#include "Java_com_chuangweizong_opencv_lib_imageprocess_Contour.h"

using namespace std;
using namespace cv;


JNIEXPORT jintArray JNICALL
Java_com_chuangweizong_opencv_lib_imageprocess_Contour_floodFill(JNIEnv *env, jobject instance,
                                                                 jintArray buf_, jint w, jint h,jint x, jint y) {
    jint *buf = env->GetIntArrayElements(buf_, NULL);

    LOGI("picture floodfill start");
    if (buf == NULL)
        return 0;
    int i;
    string string1;
    for (i=0; i<10; i++) {
        //string1 += buf[i]+",";
        LOGI("sssss%D",buf[i]);
    }

    Mat myImg(h, w, CV_8UC4, (unsigned char *)buf);

    IplImage *temp = cvCreateImage(cvSize(w,h), IPL_DEPTH_8U, 3);
    temp->imageData = (char *)myImg.data;

    string string2;
    for (i=0; i<10; i++) {
        LOGI("ssss2%D",myImg.data[i]);
        string2 += temp->imageData[i]+",";
    }
    LOGI("string2：%s",string2.c_str());

    IplImage image = IplImage(myImg);

    IplImage* image3Channel = cvCreateImage(cvGetSize(&image), IPL_DEPTH_8U, 3);
    cvCvtColor(&image , image3Channel , CV_RGBA2RGB);

    Point seed(x,y);
    Scalar newVal = Scalar(0, 0, 0);
    Rect ccomp;
    int newMaskVal = 255;

    int loDiff = 20, upDiff = 20;

    int lo = loDiff;
    int up = upDiff;

    IplImage *mask= cvCreateImage(cvSize(w,h), IPL_DEPTH_8U, 3);

    int flags = 8 + (newMaskVal << 8) + CV_FLOODFILL_FIXED_RANGE;

    cvFloodFill(image3Channel, seed, newVal, cvScalar(lo, lo, lo), cvScalar(up, up, up),NULL, flags,NULL);

    int* outImage = new int[w * h];
    for (int i = 0; i < w * h; i ++) {
        outImage[i] = (int)image3Channel->imageData[i];
    }

    int size = w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, outImage);

    env->ReleaseIntArrayElements(buf_, buf, 0);
    LOGI("picture floodfill end");

    return result;
}

JNIEXPORT jintArray JNICALL
        Java_com_chuangweizong_opencv_lib_imageprocess_Contour_floodFill2(JNIEnv *env, jobject instance,
                                                                         jobject img){
    LOGI("picture floodfill2 start");
    AndroidBitmapInfo bmp1info;
    void* bmp1pixels;
    int ret;
    if ((ret = AndroidBitmap_getInfo(env, img, &bmp1info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return 0;
    }

//    LOGI("Bitmap format is :%D",bmp1info.format);
//    if (bmp1info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 || bmp1info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
//        LOGE("Bitmap format is not RGBA_8888!");
//        return 0;
//    }

    int res = AndroidBitmap_lockPixels(env, img, &bmp1pixels);

    int h = bmp1info.height;
    int w = bmp1info.width;

    Mat myImg(h, w, CV_8UC3, bmp1pixels);

    IplImage image = IplImage(myImg);

    int* outImage = new int[w * h];
    for (int i = 0; i < w * h; i ++) {
        outImage[i] = (int)image.imageData[i];
    }

    int size = w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, outImage);

    LOGI("picture floodfill2 end");
}