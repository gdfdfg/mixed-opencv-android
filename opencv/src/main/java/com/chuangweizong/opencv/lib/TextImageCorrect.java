package com.chuangweizong.opencv.lib;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/2/2.
 */
public class TextImageCorrect {

    static {
        System.loadLibrary("myOpenCV");
    }

    public static native int getTextImageCorrectFromeNative(Bitmap bitmap,long matAddrGr, long matAddrRgba);

}
