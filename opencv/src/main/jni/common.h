#ifndef __JCOMMON__
#define __JCOMMON__

#include <android/log.h>

#define  LOG_TAG    "TClip" 

#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__) 
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__) 
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__) 

#endif

#include "opencv2/core/utility.hpp"

#include "converters.h"

#ifdef _MSC_VER
#  pragma warning(disable:4800 4244)
#endif

