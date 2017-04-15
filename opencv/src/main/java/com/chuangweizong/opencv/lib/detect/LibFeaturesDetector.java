package com.chuangweizong.opencv.lib.detect;

import com.chuangweizong.opencv.lib.BaseLoaderClass;

/**
 * Created by Administrator on 2017/3/10.
 */
public class LibFeaturesDetector extends BaseLoaderClass{

    public native static void FeaturesSurf(long matAddrGr, long matAddrRgba);

}
