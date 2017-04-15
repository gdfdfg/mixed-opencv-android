package com.chuangweizong.opencv.cvfilter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Administrator on 2017/2/24.
 */
public class StrokeEdgesFilter implements Filter {

    private final Mat mKernel = new MatOfInt(0,0,1,0,0,
            0,1,2,1,0,
            1,2,-16,2,1,
            0,1,2,1,0,
            0,0,1,0,0);


    private final Mat mEdges = new Mat();

    @Override
    public void apply(Mat src, Mat dst) {

        Imgproc.filter2D(src,mEdges,-1,mKernel);

        /**
         * 该方法逆置图像的亮度和颜色，即白色变为黑色，红色变为青色等
         * 由于当前卷积滤波器在黑色区域生产白色边，因而该方法十分有效，尽管目前任务与此相反，即白色区域生产黑色边
         */
        Core.bitwise_not(mEdges, mEdges);

        /**
         * 用于叠加原始图像上的黑色边
         */
        Core.multiply(src,mEdges,dst,1.0/255.0);

    }
}
