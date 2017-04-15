package com.daiyinger.carplate;


public class CarPlateDetection {
	public static native byte[] ImageProc(String sdpath,String logpath, String imgpath, String svmpath, String annpath);
}
