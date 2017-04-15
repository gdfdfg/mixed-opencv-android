package com.chuangweizong.opencv.lib.imageprocess;

/**
 * Created by Administrator on 2016/12/30.
 */
public class Contour {

    static {
        System.loadLibrary("OpenCV");
    }

    public static native int[] floodFill(int buf[],int w,int h,int x,int y);

    public static native int[] floodFill2(Object bmp);

}
