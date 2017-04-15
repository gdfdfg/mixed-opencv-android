package com.chuangweizong.opencv.utils;

public class LogUtil {

    @SuppressWarnings("unchecked")
    public static String makeLogTag(Class cls) {
        return "mapapp_" + cls.getSimpleName();
    }
}
