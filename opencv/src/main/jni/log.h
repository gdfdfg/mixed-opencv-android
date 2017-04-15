//
// Created by Administrator on 2016/12/30.
//

#ifndef OPENCV_LOG_H
#define OPENCV_LOG_H

#endif //OPENCV_LOG_H

#include "android/log.h"

static const char *TAG="opencv";

#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

