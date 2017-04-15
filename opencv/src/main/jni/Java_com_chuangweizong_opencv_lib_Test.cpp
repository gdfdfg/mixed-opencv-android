//
// Created by Administrator on 2016/12/28.
//
#include <jni.h>
#include <vector>
#include <string>
#include <opencv2/core/core.hpp>
#include <cv.h>
#include "android/log.h"
#include <android/bitmap.h>

using namespace std;
using namespace cv;

static const char *TAG="opencv";

#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)


#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jstring JNICALL
        Java_com_chuangweizong_opencv_lib_Test_getStringFromeNative(JNIEnv *env, jclass type);
JNIEXPORT jintArray JNICALL
        Java_com_chuangweizong_opencv_lib_Test_grey(JNIEnv *env, jobject instance, jintArray buf_, jint w, jint h);
JNIEXPORT jintArray JNICALL
        Java_com_chuangweizong_opencv_lib_Test_grey2(JNIEnv *env, jobject instance, jobject img);
#ifdef __cplusplus
}
#endif

IplImage* change4ChannelTo3InIplImage(IplImage * src);
void imageCopy(const Mat& image,Mat& outImage);

JNIEXPORT jstring JNICALL
Java_com_chuangweizong_opencv_lib_Test_getStringFromeNative(JNIEnv *env, jclass type) {

    // TODO

    Mat mat(33, 22, 1);
    LOGI("信息为%d",mat.rows);
    // string s = "Cpp v1 - succ, row#:" + mat.rows;
    string s = "Cpp v1 - succ, row#:";
    return env->NewStringUTF(s.data());
}

JNIEXPORT jintArray JNICALL
Java_com_chuangweizong_opencv_lib_Test_grey(JNIEnv *env, jobject instance, jintArray buf_, jint w, jint h) {
    jint *buf = env->GetIntArrayElements(buf_, false);
    LOGI("picture grey start");
    if (buf == NULL)
        return 0;

//    Mat myImg(h, w, CV_8UC4, (unsigned char *)buf);
//    int i;
//    for (i=0; i<10; i++) {
//        LOGI("ssss2%D",myImg.data[i]);
//    }
//
//    IplImage *temp= cvCreateImage(cvSize(w,h), IPL_DEPTH_8U, 3);
//    Mat tempm = myImg.clone();
//    temp->imageData = (char *)tempm.data;
//
//    LOGI("pixel2 red；%D,blue:%D,green:%D",(unsigned char)temp->imageData[0],temp->imageData[1],temp->imageData[2]);
//    LOGI("init size size is :%D",temp->imageSize);
//
//    IplImage image = IplImage(myImg);
//    IplImage* image3Channel = change4ChannelTo3InIplImage(&image);
//
//    IplImage* pCannyImage = cvCreateImage(cvGetSize(image3Channel), IPL_DEPTH_8U, 1);
//
//    cvCanny(image3Channel, pCannyImage, 50, 150, 3);
//
////    IplImage* destImg = cvCreateImage(cvGetSize(pCannyImage), IPL_DEPTH_8U, 3);
////
////    cvCvtColor(pCannyImage, destImg, CV_GRAY2BGR);
//
//    int* outImage = new int[w * h];
//
//    LOGI("size:%D,imageData size:%D",w*h,pCannyImage->imageSize);
//    for (int i = 0; i < w * h; i ++) {
//            outImage[i] = (int)pCannyImage->imageData[i];
//    }
//
////    for (int i = 0; i < w * h; i ++) {
////        outImage[i] = (int)image.imageData[i];
////    }
//
//    int size = w * h;
//
//    LOGI("size:%D",size);
//
//    jintArray result = env->NewIntArray(size);
//    env->SetIntArrayRegion(result, 0, size, outImage);
//
//    env->ReleaseIntArrayElements(buf_, buf, 0);
//    LOGI("picture grey end!");

    Mat imgData(h, w, CV_8UC4, (unsigned char *) buf);

//    CvMat* _buf = cvCreateMat(h,  w, CV_8U);
//    memcpy( _buf->data.ptr,imgData.ptr(0),  w*h );
//
//    IplImage * CvMat2IplImage=(IplImage *)cvClone(_buf);
//
////    Mat  CvMat2Mat=cv::cvarrToMat(_buf,false);
//    Mat  CvMat2Mat=cv::cvarrToMat(CvMat2IplImage,false);
    IplImage *temp= cvCreateImage(cvSize(w,h), IPL_DEPTH_8S, 4);
    temp->imageData = (char *)imgData.data;


    IplImage* image3Channel = change4ChannelTo3InIplImage(temp);
//    IplImage* pCannyImage = cvCreateImage(cvGetSize(image3Channel), IPL_DEPTH_8U, 1);
    Mat  CvMat2Mat=cv::cvarrToMat(image3Channel,false);

    uchar* ptr = CvMat2Mat.ptr(0);

    int* outImage = new int[w * h];

    for (int i = 0; i < w * h; i ++) {
        int grayScale = (int)(ptr[4*i+2]*0.299 + ptr[4*i+1]*0.587 + ptr[4*i+0]*0.114);
        outImage[i] = grayScale;
    }

//    for(int i = 0; i < w*h; i ++){
//        //计算公式：Y(亮度) = 0.299*R + 0.587*G + 0.114*B
//        //对于一个int四字节，其彩色值存储方式为：BGRA
//        int grayScale = (int)(ptr[4*i+2]*0.299 + ptr[4*i+1]*0.587 + ptr[4*i+0]*0.114);
//        ptr[4*i+1] = grayScale;
//        ptr[4*i+2] = grayScale;
//        ptr[4*i+0] = grayScale;
//    }
//    IplImage* destImg = cvCreateImage(cvGetSize(pCannyImage), IPL_DEPTH_8U, 3);
//
//    cvCvtColor(pCannyImage, destImg, CV_GRAY2BGR);

    int size = w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, outImage);

    env->ReleaseIntArrayElements(buf_, buf, 0);


    return result;
}


IplImage* change4ChannelTo3InIplImage(IplImage * src) {
    if (src -> nChannels != 4) {
        return NULL;
    }


    IplImage* destImg = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 3);
//
//    cvCvtColor(src , destImg , CV_RGBA2RGB);
    for (int row = 0; row < src->height; row ++) {
        for (int col = 0; col < src -> width; col ++) {
            CvScalar s = cvGet2D(src, row, col);
            cvSet2D(destImg, row, col, s);
        }
    }

    return destImg;
}

JNIEXPORT jintArray JNICALL
Java_com_chuangweizong_opencv_lib_Test_grey2(JNIEnv *env, jobject instance,
                                                                  jobject img){
    LOGI("picture floodFill2 start");
    AndroidBitmapInfo bmp1info;
    void* bmp1pixels;
    int ret;
    if ((ret = AndroidBitmap_getInfo(env, img, &bmp1info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return 0;
    }

    LOGI("Bitmap format is :%D",bmp1info.format);
//    if (bmp1info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 || bmp1info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
//        LOGE("Bitmap format is not RGBA_8888!");
//        return 0;
//    }
    int res = AndroidBitmap_lockPixels(env, img, &bmp1pixels);

    int h = bmp1info.height;
    int w = bmp1info.width;

    Mat myImg(h, w, CV_8UC4, (unsigned char *)bmp1pixels);

    IplImage *temp= cvCreateImage(cvSize(w,h), IPL_DEPTH_8S, 4);
    temp->imageData = (char *)myImg.data;

    LOGI("pixel2 red；%D,blue:%D,green:%D",temp->imageData[0],temp->imageData[1],temp->imageData[2]);
    LOGI("init size size is :%D",temp->imageSize);

    IplImage image = IplImage(myImg);
    IplImage* image3Channel = change4ChannelTo3InIplImage(&image);

    IplImage* pCannyImage = cvCreateImage(cvGetSize(image3Channel), IPL_DEPTH_8U, 1);

    cvCanny(image3Channel, pCannyImage, 50, 150, 3);

    IplImage* destImg = cvCreateImage(cvGetSize(pCannyImage), IPL_DEPTH_8U, 3);

    cvCvtColor(pCannyImage, destImg, CV_GRAY2BGR);

    LOGI("size:%D,imageData size:%D",w*h,pCannyImage->imageSize);

    int* outImage = new int[w * h];

    for (int i = 0; i < w * h; i ++) {
        outImage[i] = (int)pCannyImage->imageData[i];
    }

//    for (int i = 0; i < w * h; i ++) {
//        outImage[i] = (int)image.imageData[i];
//    }

    int size = w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, outImage);
    LOGI("picture grey end!");
    return result;
}