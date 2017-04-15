package com.chuangweizong.opencv.cvfilter;

/**
 * 该效果体现了阴影中较为强烈的蓝、绿色调，以及高光中的黄、绿色调。
 * 此类滤镜模拟了交叉处理技术，有时用于生产模特、明星等不修边幅的外观。
 */
public class CrossProcessCurveFilter extends CurveFilter {

    public CrossProcessCurveFilter(){
        super(new double[]{0,255.0},//vValIn
                new double[]{0,255.0},//vValOut
                new double[]{0,56,211,255.0},//rValIn
                new double[]{0,22,255.0,255.0},//rValOut
                new double[]{0,56,208,255.0},//gValIn
                new double[]{0,39,226,255.0},//gValOut
                new double[]{0,255.0},//bValIn
                new double[]{20,235} //bValOut
        );
    }

}
