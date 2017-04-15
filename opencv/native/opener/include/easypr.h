#ifndef EASYPR_EASYPR_H
#define EASYPR_EASYPR_H

#include <android/log.h>
#include <string>
#define LOG_TAG "System.out"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


#include "easypr/core/plate_recognize.h"
#include "easypr/train/svm_train.h"
#include "easypr/train/ann_train.h"
#include "easypr/preprocess/mc_data.h"
#include "easypr/preprocess/gdts.h"
#include "easypr/preprocess/deface.h"
#include "easypr/util/util.h"
#include "easypr/util/program_options.h"
#include "easypr/api.hpp"
#include "easypr/config.h"

#endif //EASYPR_EASYPR_H
