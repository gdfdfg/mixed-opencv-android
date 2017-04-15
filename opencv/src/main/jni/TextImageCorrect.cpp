//
// Created by Administrator on 2017/2/2.
//

#include "TextImageCorrect.h"

#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <android/bitmap.h>
#include "easypr/util/util.h"
#include <vector>
#include <iostream>
#include <easypr/config.h>
#include <opencv2/imgcodecs.hpp>

using namespace std;
using namespace cv;

void Java_org_opencv_android_Utils_nMatToBitmap2
        (JNIEnv * env, jclass, Mat m_addr, jobject bitmap, jboolean needPremultiplyAlpha);


JNIEXPORT jint JNICALL
Java_com_chuangweizong_opencv_lib_TextImageCorrect_getTextImageCorrectFromeNative(JNIEnv *env,
                                                                                  jclass type,
                                                                                  jobject bitmap,
                                                                                  jlong matAddrGr,
                                                                                  jlong matAddrRgba,jstring path) {
    Mat& srcImg  = *(Mat*)matAddrGr;
    Mat& srcGray = *(Mat*)matAddrRgba;

    if(srcImg.empty())
        return -1;

    Point center(srcImg.cols/2, srcImg.rows/2);

#ifdef DEGREE
    //Rotate source image
    Mat rotMatS = getRotationMatrix2D(center, DEGREE, 1.0);
    warpAffine(srcImg, srcImg, rotMatS, srcImg.size(), 1, 0, Scalar(255,255,255));
    imshow("RotatedSrc", srcImg);
    //imwrite("imageText_R.jpg",srcImg);
#endif

    //Expand image to an optimal size, for faster processing speed
    //Set widths of borders in four directions
    //If borderType==BORDER_CONSTANT, fill the borders with (0,0,0)
    Mat padded;
    int opWidth = getOptimalDFTSize(srcImg.rows);
    int opHeight = getOptimalDFTSize(srcImg.cols);
    copyMakeBorder(srcImg, padded, 0, opWidth-srcImg.rows, 0, opHeight-srcImg.cols, BORDER_CONSTANT, Scalar::all(0));

    Mat planes[] = {Mat_<float>(padded), Mat::zeros(padded.size(), CV_32F)};
    Mat comImg;
    //Merge into a double-channel image
    merge(planes,2,comImg);

    //Use the same image as input and output,
    //so that the results can fit in Mat well
    dft(comImg, comImg);

    //Compute the magnitude
    //planes[0]=Re(DFT(I)), planes[1]=Im(DFT(I))
    //magnitude=sqrt(Re^2+Im^2)
    split(comImg, planes);
    magnitude(planes[0], planes[1], planes[0]);

    //Switch to logarithmic scale, for better visual results
    //M2=log(1+M1)
    Mat magMat = planes[0];
    magMat += Scalar::all(1);
    log(magMat, magMat);

    //Crop the spectrum
    //Width and height of magMat should be even, so that they can be divided by 2
    //-2 is 11111110 in binary system, operator & make sure width and height are always even
    magMat = magMat(Rect(0, 0, magMat.cols & -2, magMat.rows & -2));

    //Rearrange the quadrants of Fourier image,
    //so that the origin is at the center of image,
    //and move the high frequency to the corners
    int cx = magMat.cols/2;
    int cy = magMat.rows/2;

    Mat q0(magMat, Rect(0, 0, cx, cy));
    Mat q1(magMat, Rect(0, cy, cx, cy));
    Mat q2(magMat, Rect(cx, cy, cx, cy));
    Mat q3(magMat, Rect(cx, 0, cx, cy));

    Mat tmp;
    q0.copyTo(tmp);
    q2.copyTo(q0);
    tmp.copyTo(q2);

    q1.copyTo(tmp);
    q3.copyTo(q1);
    tmp.copyTo(q3);

    //Normalize the magnitude to [0,1], then to[0,255]
    normalize(magMat, magMat, 0, 1, CV_MINMAX);
    Mat magImg(magMat.size(), CV_8UC1);
    magMat.convertTo(magImg,CV_8UC1,255,0);
    //imwrite("imageText_mag.jpg",magImg);

    //Turn into binary image
    threshold(magImg,magImg, 155,255,CV_THRESH_BINARY);
    //imwrite("imageText_bin.jpg",magImg);

    //Find lines with Hough Transformation
    vector<Vec2f> lines;
    float pi180 = (float)CV_PI/180;
    Mat linImg(magImg.size(),CV_8UC3);
    HoughLines(magImg,lines,1,pi180,100,0,0);
    int numLines = lines.size();
    for(int l=0; l<numLines; l++)
    {
        float rho = lines[l][0], theta = lines[l][1];
        Point pt1, pt2;
        double a = cos(theta), b = sin(theta);
        double x0 = a*rho, y0 = b*rho;
        pt1.x = cvRound(x0 + 1000*(-b));
        pt1.y = cvRound(y0 + 1000*(a));
        pt2.x = cvRound(x0 - 1000*(-b));
        pt2.y = cvRound(y0 - 1000*(a));
        line(linImg,pt1,pt2,Scalar(255,0,0),3,8,0);
    }
    imwrite("imageText_line.jpg",linImg);
    if(lines.size() == 3){
        LOGI("found three angels:");
        LOGI("%D %D %D",lines[0][1]*180/CV_PI,lines[1][1]*180/CV_PI,lines[2][1]*180/CV_PI);
    }

    //Find the proper angel from the three found angels
    float angel=0;
    float piThresh = (float)CV_PI/90;
    float pi2 = CV_PI/2;
    for(int l=0; l<numLines; l++)
    {
        float theta = lines[l][1];
        if(abs(theta) < piThresh || abs(theta-pi2) < piThresh)
            continue;
        else{
            angel = theta;
            break;
        }
    }

    //Calculate the rotation angel
    //The image has to be square,
    //so that the rotation angel can be calculate right
    angel = angel<pi2 ? angel : angel-CV_PI;
    if(angel != pi2){
        float angelT = srcImg.rows*tan(angel)/srcImg.cols;
        angel = atan(angelT);
    }
    float angelD = angel*180/(float)CV_PI;

    LOGI("the rotation angel to be applied: %F",angelD);

    //Rotate the image to recover
    Mat rotMat = getRotationMatrix2D(center,angelD,1.0);
    Mat dstImg = Mat::ones(srcImg.size(),CV_8UC3);
    warpAffine(srcImg,dstImg,rotMat,srcImg.size(),1,0,Scalar(255,255,255));

//    char* psdpath = jstring2str(env,path);
//    utils::imwrite(psdpath + "/inputgray2.jpg", dstImg);

    Java_org_opencv_android_Utils_nMatToBitmap2(env,type,dstImg,bitmap, false);

    return 0;
}


void Java_org_opencv_android_Utils_nMatToBitmap2
        (JNIEnv * env, jclass, Mat src, jobject bitmap, jboolean needPremultiplyAlpha)
{
    AndroidBitmapInfo  info;
    void*              pixels = 0;

    try {
        LOGD("nMatToBitmap");
        CV_Assert( AndroidBitmap_getInfo(env, bitmap, &info) >= 0 );
        CV_Assert( info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                   info.format == ANDROID_BITMAP_FORMAT_RGB_565 );
        CV_Assert( src.dims == 2 && info.height == (uint32_t)src.rows && info.width == (uint32_t)src.cols );
        CV_Assert( src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4 );
        CV_Assert( AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0 );
        CV_Assert( pixels );
        if( info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 )
        {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if(src.type() == CV_8UC1)
            {
                LOGD("nMatToBitmap: CV_8UC1 -> RGBA_8888");
                cvtColor(src, tmp, COLOR_GRAY2RGBA);
            } else if(src.type() == CV_8UC3){
                LOGD("nMatToBitmap: CV_8UC3 -> RGBA_8888");
                cvtColor(src, tmp, COLOR_RGB2RGBA);
            } else if(src.type() == CV_8UC4){
                LOGD("nMatToBitmap: CV_8UC4 -> RGBA_8888");
                if(needPremultiplyAlpha) cvtColor(src, tmp, COLOR_RGBA2mRGBA);
                else src.copyTo(tmp);
            }
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if(src.type() == CV_8UC1)
            {
                LOGD("nMatToBitmap: CV_8UC1 -> RGB_565");
                cvtColor(src, tmp, COLOR_GRAY2BGR565);
            } else if(src.type() == CV_8UC3){
                LOGD("nMatToBitmap: CV_8UC3 -> RGB_565");
                cvtColor(src, tmp, COLOR_RGB2BGR565);
            } else if(src.type() == CV_8UC4){
                LOGD("nMatToBitmap: CV_8UC4 -> RGB_565");
                cvtColor(src, tmp, COLOR_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch(const cv::Exception& e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nMatToBitmap catched cv::Exception: %s", e.what());
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nMatToBitmap catched unknown exception (...)");
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return;
    }
}