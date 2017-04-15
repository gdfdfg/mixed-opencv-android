package com.chuangweizong.opencv.cvfilter;

import org.opencv.core.Mat;

/**
 * Created by Administrator on 2017/2/24.
 */
public interface Filter {

    public abstract void apply(final Mat src,final Mat dst);

}
