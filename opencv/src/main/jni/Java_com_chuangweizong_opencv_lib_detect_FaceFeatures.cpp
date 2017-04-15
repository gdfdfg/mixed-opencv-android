//
// Created by Administrator on 2017/1/5.
//

#include "Java_com_chuangweizong_opencv_lib_detect_FaceFeatures.h"
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/features2d.hpp>
#include <vector>

using namespace std;
using namespace cv;

JNIEXPORT void JNICALL Java_com_chuangweizong_opencv_lib_detect_FaceFeatures_FindFeatures(JNIEnv*, jobject, jlong addrGray, jlong addrRgba)
{
Mat& mGr  = *(Mat*)addrGray;
Mat& mRgb = *(Mat*)addrRgba;
vector<KeyPoint> v;

Ptr<FeatureDetector> detector = FastFeatureDetector::create(50);
    detector->detect(mGr, v);
for( unsigned int i = 0; i < v.size(); i++ )
{
const KeyPoint& kp = v[i];
circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255,0,0,255));
}
}