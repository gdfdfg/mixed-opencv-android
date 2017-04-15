package com.chuangweizong.opencv.lib.ocr;

import android.graphics.Bitmap;
import android.util.Log;

import com.chuangweizong.opencv.utils.AssetsCopyTOSDcard;
import com.googlecode.tesseract.android.TessBaseAPI;

//get the words from picture through Tesseract engine
public class Tesseract { 
	private static final String tag="Tesseract";
	
	public static String getText(Bitmap bmSrc,String path){

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.init(path
				, "eng");
		baseApi.setImage(bmSrc);
		String text = baseApi.getUTF8Text();
		baseApi.clear();
		baseApi.end();
		Log.i(tag, "Rcognition completed");
		return text;
	}
	
}