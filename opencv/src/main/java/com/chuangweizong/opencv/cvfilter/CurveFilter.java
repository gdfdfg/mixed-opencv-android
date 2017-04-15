package com.chuangweizong.opencv.cvfilter;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;

/**
 * 曲线色移
 * 当观察某一场景时，可根据不同图像区域间的颜色变化寻找某种线索。例如，在晴朗的室外环境，基于反射自蓝天的环境光，阴影通常会包含淡淡的蓝色；
 * 而高光包含淡黄色效果，皆因该对象直接处于阳光的照射下。当观察照片中淡蓝色阴影和黄色高光时，通常会具有”温暖和阳光“这一感觉。
 * 这一类效果通常以自然的方式实现，或者通过滤镜对其予以夸大。
 * 来自《Android OpennCV应用程序设计》
 */
public class CurveFilter implements Filter {

    private final Mat mLUT = new MatOfInt();

    public CurveFilter(final double[] vValIn,final double[] vValOut,final double[]rValIn,final double[] rValOut,final double[] gValIn,final double[] gValOut,final double[] bValIn,final double[] bValOut){

        //create the interpolation functions.
        UnivariateFunction vFunc = newFunc(rValIn,rValOut);
        UnivariateFunction gFunc = newFunc(gValIn,gValOut);
        UnivariateFunction bFunc = newFunc(bValIn,bValOut);
        UnivariateFunction rFunc = newFunc(rValIn,rValOut);

        mLUT.create(256,1, CvType.CV_8UC4);
        for(int i = 0;i<256;i++){
            final double v = vFunc.value(i);
            final double r = rFunc.value(v);
            final double g = gFunc.value(v);
            final double b = bFunc.value(v);

            mLUT.put(i,0,r,g,b,i);
        }
    }

    private UnivariateFunction newFunc(final double[] valIn,final double[] valOut){
        UnivariateInterpolator interpolator;
        if(valIn.length > 2){
            interpolator = new SplineInterpolator();
        }else {
            interpolator = new LinearInterpolator();
        }
        return interpolator.interpolate(valIn,valOut);
    }

    @Override
    public void apply(Mat src, Mat dst) {
        Core.LUT(src,mLUT,dst);
    }
}
