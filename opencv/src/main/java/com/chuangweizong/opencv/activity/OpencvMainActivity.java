package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.chuangweizhong.opencv.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

public class OpencvMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_opencv_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13,this,mOpenCVCallBack);
    }

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this){

        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);

            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    System.loadLibrary("OpenCV");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }

        }
    };

}
