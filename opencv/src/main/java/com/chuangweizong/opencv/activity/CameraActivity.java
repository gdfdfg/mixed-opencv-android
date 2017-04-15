package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.content.Context;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.cvfilter.CrossProcessCurveFilter;
import com.chuangweizong.opencv.cvfilter.Filter;
import com.chuangweizong.opencv.cvfilter.ImageDetectionFilter;
import com.chuangweizong.opencv.cvfilter.NoneFilter;
import com.chuangweizong.opencv.cvfilter.PortraCurveFilter;
import com.chuangweizong.opencv.cvfilter.ProviaCurveFilter;
import com.chuangweizong.opencv.cvfilter.RecolorCMVilter;
import com.chuangweizong.opencv.cvfilter.RecolorRCFilter;
import com.chuangweizong.opencv.cvfilter.RecolorRGVilter;
import com.chuangweizong.opencv.cvfilter.StrokeEdgesFilter;
import com.chuangweizong.opencv.cvfilter.VelviaCurveFilter;
import com.chuangweizong.opencv.lib.detect.DetectionBasedTracker;
import com.chuangweizong.opencv.lib.detect.LibFeaturesDetector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    private MenuItem               mItemFace50;
    private MenuItem               mItemFace40;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    private MenuItem               mItemType;
    private MenuItem               mItemCureFilter;
    private MenuItem               mItemMixerFilter;
    private MenuItem               mItemConFilter;
    private MenuItem               mItemDetectionFilter;

    private Mat mRgba;
    private Mat                    mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;

    private CameraBridgeViewBase   mOpenCvCameraView;

    private static final String STATE_IMAGE_DETECTION_FILTER_INDEX = "imageDetectionFilterIndex";
    private static final String STATE_CAMERA_INDEX = "cameraIndex";
    private static final String STATE_CURVE_FILTER_INDEX = "curveFilterIndex";
    private static final String STATE_MIXER_FILTER_INDEX = "mixerFilterIndex";
    private static final String STATE_CONVOLUTION_FILTER_INDEX = "convolutionFilterIndex";
    //The filters.
    private Filter[] mImageDetectionFilters;
    private Filter[] mCurveFilters;
    private Filter[] mMixerFilters;
    private Filter[] mConvolutionFilters;
    //The indices of the active filters.
    private int mImageDetectionFilterIndex;
    private int mCameraIndex;
    private int mCurveFilterIndex;
    private int mMixerFilterIndex;
    private int mConvolutionFilterIndex;
    private Mat mBgr;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("myOpenCV");

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();

                    mBgr = new Mat();

//                    Filter starryNight = null;
//                    try {
//                        starryNight = new ImageDetectionFilter(CameraActivity.this,R.drawable.house);
//                    } catch (IOException e) {
//                        Log.e(TAG, "failed to load drawable:" + "starry_night");
//                        e.printStackTrace();
//                    }

//                    mImageDetectionFilters = new Filter[]{
//                            new NoneFilter(),
//                            starryNight
//                    };

                    mCurveFilters = new Filter[]{
                            new NoneFilter(),
                            new PortraCurveFilter(),
                            new ProviaCurveFilter(),
                            new VelviaCurveFilter(),
                    };

                    mMixerFilters = new Filter[]{
                            new NoneFilter(),
                            new RecolorRCFilter(),
                            new RecolorRGVilter(),
                            new RecolorCMVilter()
                    };
                    mConvolutionFilters = new Filter[]{
                            new NoneFilter(),
                            new StrokeEdgesFilter()
                    };

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_camera_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        if(savedInstanceState != null){
            mCameraIndex = savedInstanceState.getInt(STATE_CAMERA_INDEX,0);
            mImageDetectionFilterIndex = savedInstanceState.getInt(STATE_IMAGE_DETECTION_FILTER_INDEX,0);
            mCurveFilterIndex = savedInstanceState.getInt(STATE_CURVE_FILTER_INDEX,0);
            mMixerFilterIndex = savedInstanceState.getInt(STATE_MIXER_FILTER_INDEX,0);
            mConvolutionFilterIndex = savedInstanceState.getInt(STATE_CONVOLUTION_FILTER_INDEX,0);
        }else{
            mCameraIndex = 0;
            mImageDetectionFilterIndex = 0;
            mCurveFilterIndex = 0;
            mMixerFilterIndex = 0;
            mConvolutionFilterIndex = 0;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState) {
        saveInstanceState.putInt(STATE_IMAGE_DETECTION_FILTER_INDEX,mImageDetectionFilterIndex);
        saveInstanceState.putInt(STATE_CAMERA_INDEX,mCameraIndex);
        saveInstanceState.putInt(STATE_CURVE_FILTER_INDEX,mCurveFilterIndex);
        saveInstanceState.putInt(STATE_MIXER_FILTER_INDEX,mMixerFilterIndex);
        saveInstanceState.putInt(STATE_CONVOLUTION_FILTER_INDEX,mConvolutionFilterIndex);
        super.onSaveInstanceState(saveInstanceState);

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
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if(mImageDetectionFilters != null){
            mImageDetectionFilters[mImageDetectionFilterIndex].apply(mRgba,mRgba);
        }

        mCurveFilters[mCurveFilterIndex].apply(mRgba,mRgba);
        mMixerFilters[mMixerFilterIndex].apply(mRgba,mRgba);
        mConvolutionFilters[mConvolutionFilterIndex].apply(mRgba,mRgba);

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

//        Point center = new Point(mRgba.cols()/2f, mRgba.rows()/2f);
//        double angle = -90.0;
//        double scale = 1;
//        Mat rotateMat = Imgproc.getRotationMatrix2D(center, angle, scale);
//        Imgproc.warpAffine(mRgba, mRgba, rotateMat, mRgba.size());
//        Core.flip(mRgba.t(),mRgba,1);

        Mat mRgbaT = new Mat();
        Core.transpose(mRgba, mRgbaT); //转置函数，可以水平的图像变为垂直
        Imgproc.resize(mRgbaT,mRgba, mRgba.size(), 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
        Core.flip(mRgba, mRgba,1); //根据x,y轴翻转，0-x 1-y

        mRgbaT.release();
        return mRgba;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemFace50 = menu.add("Face size 50%");
        mItemFace40 = menu.add("Face size 40%");
        mItemFace30 = menu.add("Face size 30%");
        mItemFace20 = menu.add("Face size 20%");
        mItemType   = menu.add(mDetectorName[mDetectorType]);
        mItemCureFilter = menu.add("CureFilter");
        mItemMixerFilter = menu.add("MixerFilte");
        mItemConFilter = menu.add("ConFilter");
        mItemDetectionFilter = menu.add("DetectionFilter");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemFace50)
            setMinFaceSize(0.5f);
        else if (item == mItemFace40)
            setMinFaceSize(0.4f);
        else if (item == mItemFace30)
            setMinFaceSize(0.3f);
        else if (item == mItemFace20)
            setMinFaceSize(0.2f);
        else if (item == mItemType) {
            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorName[tmpDetectorType]);
            setDetectorType(tmpDetectorType);
        }else if (item == mItemCureFilter) {
            mCurveFilterIndex++;
            if(mCurveFilterIndex == mCurveFilters.length)
                mCurveFilterIndex = 0;
        }else  if (item == mItemMixerFilter){
               mMixerFilterIndex ++;
            if(mMixerFilterIndex == mMixerFilters.length){
                mMixerFilterIndex = 0;
            }
        }else if (item == mItemConFilter){
            mConvolutionFilterIndex++;
            if(mConvolutionFilterIndex == mConvolutionFilters.length){
                mConvolutionFilterIndex = 0;
            }
        }else if (item == mItemDetectionFilter){
            mImageDetectionFilterIndex++;
            if(mImageDetectionFilterIndex == mImageDetectionFilters.length){
                mImageDetectionFilterIndex = 0;
            }
        }

        return true;
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;

            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetector.start();
            } else {
                Log.i(TAG, "Cascade detector enabled");
                mNativeDetector.stop();
            }
        }
    }
}
