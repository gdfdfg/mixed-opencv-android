package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.chuangweizhong.opencv.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.util.List;

public class FlowAndTrackerActivity extends Activity  implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "opencv";

    private static final int VIEW_MODE_KLT_TRACKER = 0;
    private static final int VIEW_MODE_OPTICAL_FLOW = 1;

    private int mViewMode;
    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;
    private Mat mPrevGray;


    MatOfPoint2f prevFeatures,nextFeatures;
    MatOfPoint features;

    MatOfByte status;
    MatOfFloat err;

    private MenuItem mItemPreviewOpticalFlow,mItemPreviewKLT;

    private CameraBridgeViewBase mOpenCvCameraView;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_object);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_trace_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trace_object, menu);

        mItemPreviewKLT = menu.add("KLT Tracker");
        mItemPreviewOpticalFlow = menu.add("Optical Flow");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(item == mItemPreviewOpticalFlow){
            mViewMode = VIEW_MODE_OPTICAL_FLOW;
            resetVars();
        }else if(item == mItemPreviewKLT){
            mViewMode = VIEW_MODE_KLT_TRACKER;
            resetVars();
        }
        return true;
    }

    private void resetVars(){

        mPrevGray = new Mat(mGray.rows(),mGray.cols(), CvType.CV_8UC1);
        features = new MatOfPoint();

        prevFeatures = new MatOfPoint2f();
        nextFeatures = new MatOfPoint2f();
        status = new MatOfByte();
        err = new MatOfFloat();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height,width,CvType.CV_8UC4);
        mIntermediateMat = new Mat(height,width,CvType.CV_8UC4);
        mGray = new Mat(height,width,CvType.CV_8UC1);
        resetVars();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        final int viewMode = mViewMode;
        switch (viewMode){
            case VIEW_MODE_OPTICAL_FLOW:
                mGray = inputFrame.gray();
                if(features.toArray().length==0){
                    int rowStep = 50, colStep = 100;
                    int nRows = mGray.rows()/rowStep, nCols = mGray.cols()/colStep;

//                    Log.d("com.packtpub.masteringopencvandroid.chapter5.grid", "\nRows: "+nRows+"\nCols: "+nCols+"\n");

                    Point points[] = new Point[nRows*nCols];
                    for(int i=0; i<nRows; i++){
                        for(int j=0; j<nCols; j++){
                            points[i*nCols+j]=new Point(j*colStep, i*rowStep);
//                            Log.d("com.packtpub.masteringopencvandroid.chapter5.grid", "\nRow: "+i*rowStep+"\nCol: "+j*colStep+"\n: ");
                        }
                    }

                    features.fromArray(points);

                    prevFeatures.fromList(features.toList());
                    mPrevGray = mGray.clone();
                    break;
                }

                nextFeatures.fromArray(prevFeatures.toArray());
                Video.calcOpticalFlowPyrLK(mPrevGray, mGray, prevFeatures, nextFeatures, status, err);

                List<Point> prevList=features.toList(), nextList=nextFeatures.toList();
                Scalar color = new Scalar(255);

                for(int i = 0; i<prevList.size(); i++){
//                    Core.circle(mGray, prevList.get(i), 5, color);
                    Imgproc.line(mGray, prevList.get(i), nextList.get(i), color);
                }

                mPrevGray = mGray.clone();
                break;
            case VIEW_MODE_KLT_TRACKER:
                mGray = inputFrame.gray();

                if(features.toArray().length==0){
                    Imgproc.goodFeaturesToTrack(mGray, features, 10, 0.01, 10);
                    Log.d("com.packtpub.masteringopencvandroid.chapter5.JNI", features.toList().size()+"");
                    prevFeatures.fromList(features.toList());
                    mPrevGray = mGray.clone();
//                    prevFeatures.fromList(nextFeatures.toList());
                    break;
                }

//                OpticalFlow(mPrevGray.getNativeObjAddr(), mGray.getNativeObjAddr(), prevFeatures.getNativeObjAddr(), nextFeatures.getNativeObjAddr());
                Video.calcOpticalFlowPyrLK(mPrevGray, mGray, prevFeatures, nextFeatures, status, err);
                List<Point> drawFeature = nextFeatures.toList();
//                Log.d("com.packtpub.masteringopencvandroid.chapter5.JNI", drawFeature.size()+"");
                for(int i = 0; i<drawFeature.size(); i++){
                    Point p = drawFeature.get(i);
                    Imgproc.circle(mGray, p, 5, new Scalar(255));
                }
                mPrevGray = mGray.clone();
                prevFeatures.fromList(nextFeatures.toList());
                break;
            default: mViewMode = VIEW_MODE_KLT_TRACKER;
        }
        return mGray;
    }
}
