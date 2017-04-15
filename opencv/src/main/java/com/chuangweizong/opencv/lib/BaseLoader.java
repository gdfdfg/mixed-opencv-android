package com.chuangweizong.opencv.lib;

/**
 * Created by Administrator on 2017/3/10.
 */
public  class BaseLoader {

    static {
        try {
            System.loadLibrary("myOpenCV");
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

}
