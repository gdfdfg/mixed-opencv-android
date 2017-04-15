package com.chuangweizong.opencv.lib.detect;

/**
 * Created by Administrator on 2017/1/5.
 */
public class FaceFeatures {

    static {
        try {
            System.loadLibrary("myOpenCV");
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public native static void FindFeatures(long matAddrGr, long matAddrRgba);

}
