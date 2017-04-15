package com.chuangweizong.opencv.lib;

/**
 * Created by Administrator on 2016/12/28.
 */
public class Test {
//
    static {
        System.loadLibrary("myOpenCV");
    }

    public static native String getStringFromeNative();

    public native int[] grey(int buf[],int w,int h);

    public native int[] grey2(Object img);

}
