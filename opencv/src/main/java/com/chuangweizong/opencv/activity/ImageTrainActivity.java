package com.chuangweizong.opencv.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//OpenCV imports
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.lib.train.DigitRecognizer;

public class ImageTrainActivity extends Activity implements CvCameraViewListener2 {

    private CameraBridgeViewBase mOpenCvCameraView;
    private DigitRecognizer mnist;

    protected String TAG = "opencv";

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                    mnist = new DigitRecognizer("train-images.idx3-ubyte", "train-labels.idx1-ubyte");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    public ImageTrainActivity() {

    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_image_train);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        //Get image size and draw a rectangle on the image for reference
        Mat temp = inputFrame.rgba();
        Imgproc.rectangle(temp, new Point(temp.cols() / 2 - 200, temp.rows() / 2 - 200), new Point(temp.cols() / 2 + 200, temp.rows() / 2 + 200), new Scalar(255, 255, 255), 1);
        Mat digit = temp.submat(temp.rows()/2 - 180, temp.rows() / 2 + 180, temp.cols() / 2 - 180, temp.cols() / 2 + 180).clone();
        Core.transpose(digit,digit);
        mnist.FindMatch(digit);

        return temp;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
