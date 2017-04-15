package com.chuangweizong.opencv.cvfilter;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * 该滤镜的效果是将绿色和蓝色转换为青色，并遗留有限的红色和青色调色板。
 * 该滤镜可模拟早期电影和计算机游戏的显示效果.
 */
public class RecolorCMVilter implements Filter {

    private final ArrayList<Mat> mChannels = new ArrayList<>();
    @Override
    public void apply(Mat src, Mat dst) {

        Core.split(src,mChannels);

        final Mat r = mChannels.get(0);
        final Mat g = mChannels.get(1);
        final Mat b = mChannels.get(2);

        Core.max(b,r,b);
        Core.max(b,g,b);

        mChannels.set(3, b);
        Core.merge(mChannels, dst);

    }
}
