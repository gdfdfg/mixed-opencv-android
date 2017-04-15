package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.lib.Test;

import com.chuangweizong.opencv.lib.Image;

public class GreyActivity extends Activity {

    private Image image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("opencv", Test.getStringFromeNative());
        Test test = new Test();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.build);

        int w = bitmap.getWidth(), h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int first = pix[0];
        Log.i("opencv", "pixel red:" + Color.red(first)+",blue :"+Color.blue(first)+",green:"+Color.green(first)+",alpa:"+Color.alpha(first));
        Log.i("opencv", "java pix size:"+(w * h));
        Bitmap resultImg = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        resultImg.setPixels(test.grey(pix,w,h), 0, w, 0, 0, w, h);

        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(resultImg);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
