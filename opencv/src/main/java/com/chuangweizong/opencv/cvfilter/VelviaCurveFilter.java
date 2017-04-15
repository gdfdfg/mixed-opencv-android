package com.chuangweizong.opencv.cvfilter;

/**
 * 此滤镜效果可生成深色阴影以及较为鲜艳的色彩，并模拟了Fuji Velvia胶片的色彩特征。
 * 这一效果多用于显示晴朗天气以及日落晚霞时的地表特征
 */
public class VelviaCurveFilter extends CurveFilter{

    public VelviaCurveFilter(){
        super(new double[]{0,128,221,255},//vValIn
                new double[]{0,118,215,255},//vValOut
                new double[]{0,25,122,165,255},//rValIn
                new double[]{0,21,153,206,255},//rValOut
                new double[]{0,25,95,181,255},//gValIn
                new double[]{0,21,102,208,255},//gValOut
                new double[]{0,35,205,255},//bValIn
                new double[]{0,25,227,255} //bValOut
        );
    }


}
