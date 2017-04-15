package com.chuangweizong.opencv.cvfilter;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * 该滤镜的效果是将绿色和蓝色转换为青色，并遗留有限的红色和青色调色板。
 * 该滤镜可模拟早期电影和计算机游戏的显示效果.
 */
public class RecolorRCFilter implements Filter {

    private final ArrayList<Mat> mChannels = new ArrayList<>();
    @Override
    public void apply(Mat src, Mat dst) {

        Core.split(src,mChannels);
        final Mat g = mChannels.get(1);
        final Mat b = mChannels.get(2);
        //dst.g = 0.5*src.g + 0.5 *src.b
        Core.addWeighted(g,0.5,b,0.5,0.0,g);
        //dst.b = dst.g
        mChannels.set(2,g);
        Core.merge(mChannels, dst);

    }
}
