package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.view.MyGLSurfaceView;

public class FloodfillActivity extends Activity{

	private MyGLSurfaceView mEffectView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_imagefull);
        mEffectView = (MyGLSurfaceView) findViewById(R.id.effectsview);
    }
    
//    public void replace(View view){
//    	
//    	mEffectView.replace();
//    	 
//    }
    
}
