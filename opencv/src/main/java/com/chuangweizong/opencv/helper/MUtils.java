package com.chuangweizong.opencv.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

public class MUtils {
	
	public static void toast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	public static byte[] getImageBytes(Bitmap mBitmap) {
		byte[] colors = null;
		if (mBitmap != null) {
			int mWidth = mBitmap.getWidth();
			int mHeight = mBitmap.getHeight();
			int[] pixels = new int[mWidth * mHeight];
			mBitmap.getPixels(pixels, 0, mWidth, 0, 0, mWidth, mHeight);
			int count = 0;
			int mArrayColorLengh = mWidth * mHeight;
			colors = new byte[mArrayColorLengh * 3];
			int[] mArrayColor = new int[mArrayColorLengh * 3];
			for (int i = 0; i < mHeight; i++) {
				for (int j = 0; j < mWidth; j++) {
					int color = mBitmap.getPixel(j, i);
					// mArrayColor[count] = color;
					int g = Color.green(color);
					int r = Color.red(color);
					int b = Color.blue(color);
					if ((g == 0) && (r == 0) && (b == 0)) {
						g = 255;
						r = 255;
						b = 255;
					}
					mArrayColor[count * 3] = b;
					mArrayColor[count * 3 + 1] = g;
					mArrayColor[count * 3 + 2] = r;

					colors[count * 3] = (byte) b;
					colors[count * 3 + 1] = (byte) g;
					colors[count * 3 + 2] = (byte) r;
					count++;
				}
			}
		}
		return colors;
	}
	
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

	
}
