#include<com_daiyinger_carplate_CarPlateDetection.h>
#include <vector>
#include "easypr.h"
#include "stdlib.h"
#include "stdio.h"
#include "unistd.h"
#include "easypr/config.h"

resourcePath path;

using namespace easypr;

#define __FILE_DEBUG__  
#ifdef __FILE_DEBUG__  
#define FILE_DEBUG(x,format,...) do{if(x){fprintf(x,format,##__VA_ARGS__);}}while(0)  
#else  
#define FILE_DEBUG(x,format,...)  
#endif  

 
using std::vector;

char* jstring2str(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("GB2312");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);
	return rtn;
}


JNIEXPORT jbyteArray JNICALL Java_com_daiyinger_carplate_CarPlateDetection_ImageProc
  (JNIEnv *env, jclass obj, jstring sdpath, jstring logpath, jstring imgpath, jstring svmpath,
		jstring annpath){
	CPlateRecognize pr;
	FILE *fp_log = NULL;
	int i;
	char* log = jstring2str(env,logpath);
	char* img = jstring2str(env,imgpath);
	char* svm = jstring2str(env,svmpath);
	char* ann = jstring2str(env,annpath);
	char* psdpath = jstring2str(env,sdpath);

	path.defaultSdcardPath = psdpath;
	path.defaultImgPath = path.defaultSdcardPath+"/ai/tmp/";
	
	fp_log = fopen(log,"a+");	//打开日志文件
	FILE_DEBUG(fp_log,"\r\n============= start ===========\r\n");
	
	LOGD("%s\n%s\n%s",img,svm,ann);
	FILE_DEBUG(fp_log,"%s\r\n%s\r\n%s\r\n",img,svm,ann);
	
	vector < string > plateVec;
	try
	{
		//进行车牌识别
		plateVec = easypr::api::plate_recognize(img, svm, ann);
	}
	catch(...)
	{
		LOGD("error occured!");
		FILE_DEBUG(fp_log,"error occured!\r\n");
	}
	string str = "0";
	LOGD("enter 3");
	usleep(10);
	if (plateVec.size() > 0) 
	{
		str = plateVec[0];
		for(i = 0; i< plateVec.size(); i++)
		{
			FILE_DEBUG(fp_log, "%s\r\n", plateVec[i].c_str());
		}
	}
	else
	{
		FILE_DEBUG(fp_log,"鏈兘璇嗗埆!\r\n");	//识别失败 UTF-8编码
		fclose(fp_log);
		return NULL;
	}
	LOGD("get result");
	char *result = new char[str.length() + 1];
	strcpy(result, str.c_str());
	jbyte *by = (jbyte*) result;
	jbyteArray jarray = env->NewByteArray(strlen(result));
	env->SetByteArrayRegion(jarray, 0, strlen(result), by);
	fclose(fp_log);
	return jarray;
}
