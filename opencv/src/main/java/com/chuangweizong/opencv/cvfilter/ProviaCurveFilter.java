package com.chuangweizong.opencv.cvfilter;

/**
 * 该滤镜的效果可增加阴影和高光之间的对比度，并使图像的色调偏于冷淡。另外，天空、水面以及着色将
 * 着重强调。此类滤镜模拟了Fuji Provia胶片的色彩特征，且常用于地表图像
 */
public class ProviaCurveFilter extends CurveFilter {

    public ProviaCurveFilter(){
        super(new double[]{0,255},//vValIn
                new double[]{0,255},//vValOut
                new double[]{0,59,202,255},//rValIn
                new double[]{0,54,210,255},//rValOut
                new double[]{0,27,196,255},//gValIn
                new double[]{0,21,207,255},//gValOut
                new double[]{0,35,205,255},//bValIn
                new double[]{0,25,227,255} //bValOut
        );
    }

}
