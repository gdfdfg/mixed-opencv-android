package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.chuangweizhong.opencv.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OpencvFaceHaarDetectActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String   TAG = "opencv";

    private MenuItem             mItemSwitchCamera = null;
    private Mat mRgba;

    private boolean mIsFrontCamera = false;

    private CascadeClassifier haarCascade;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_opencv_face_detect);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    try {

                        InputStream is = getResources()
                            .openRawResource(R.raw.haarcascade_frontalface_alt);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        File mCascadeFile = new File(cascadeDir,"cascade.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        byte [] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1){
                            os.write(buffer,0,bytesRead);
                        }

                        is.close();
                        os.close();

                        haarCascade = new CascadeClassifier(mCascadeFile.getAbsolutePath());

                        if(haarCascade.empty()){
                            Log.i(TAG,"级联分类器加载失败");
                            haarCascade = null;
                        }else {
                            Log.i(TAG,"级联分类器加载成功"+mCascadeFile.getAbsolutePath());
                        }


                    } catch (FileNotFoundException e) {
                        Log.i(TAG, "未找到级联分类器");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "文件异常");
                    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_opencv_face_detect, menu);
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemSwitchCamera = menu.add("Toggle Front/Back camera");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        String toastMesage = "";

        if (item == mItemSwitchCamera) {
            mOpenCvCameraView.setVisibility(SurfaceView.GONE);
            mIsFrontCamera = !mIsFrontCamera;

            if (mIsFrontCamera) {
                mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);
                mOpenCvCameraView.setCameraIndex(1);
                toastMesage = "Front Camera";
            } else {
                mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);
                mOpenCvCameraView.setCameraIndex(-1);
                toastMesage = "Back Camera";
            }

            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
            mOpenCvCameraView.setCvCameraViewListener(this);
            mOpenCvCameraView.enableView();
            Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
            toast.show();
        }
        return true;
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
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        //旋转输入帧
        Mat mGray = inputFrame.gray();
        mRgba = inputFrame.rgba();
        if(mIsFrontCamera){
            Core.flip(mRgba,mRgba,1);
        }

        //在帧中检测人脸
        MatOfRect faces = new MatOfRect();
        if(haarCascade != null){
            haarCascade.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(200, 200), new Size());
        }
        Rect [] facesArray = faces.toArray();

        Log.i(TAG,"face features:"+facesArray.length);
        for (int i = 0;i< facesArray.length;i++){
            Imgproc.rectangle(mRgba,facesArray[i].tl(),facesArray[i].br(),new Scalar(100),3);
        }

        Point center = new Point(mRgba.cols()/2f, mRgba.rows()/2f);
        double angle = -90.0;
        double scale = 1;
        Mat rotateMat = Imgproc.getRotationMatrix2D(center, angle, scale);
        Imgproc.warpAffine(mRgba, mRgba, rotateMat, mRgba.size());

//        Mat kernel = new Mat(3,3,CvType.CV_16SC1);//锐化
//        kernel.put(0,0,0,-1,0,-1,5,-1,0,-1,0);//自定义核
//        Imgproc.filter2D(mRgba, mRgba, mRgba.depth(), kernel);

//        ///////////////////////////////////////////////////////////////////////////
//        Bitmap currentBitmap = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
//
//        myFace = new FaceDetector.Face[numberOfFace];
//        myFaceDetect = new FaceDetector(mRgba.cols(), mRgba.rows(), numberOfFace);
//        numberOfFaceDetected = myFaceDetect.findFaces(currentBitmap, myFace);
//
//        Paint myPaint = new Paint();
//        myPaint.setColor(Color.GREEN);
//        myPaint.setStyle(Paint.Style.STROKE);
//        myPaint.setStrokeWidth(3);
//
//        ArrayList<Rect> bounts = new ArrayList<>();
//        for(int i=0; i < numberOfFaceDetected; i++)
//        {
//            FaceDetector.Face face = myFace[i];
//            PointF myMidPoint = new PointF();
//            face.getMidPoint(myMidPoint);
//            myEyesDistance = face.eyesDistance();
//
//            Rect rect = new Rect();
//            rect.width = (int)myEyesDistance*4;
//            rect.height = (int)myEyesDistance*4;
//            rect.x = (int)(myMidPoint.x - myEyesDistance*2);
//            rect.y = (int)(myMidPoint.y - myEyesDistance*2);
//            bounts.add(rect);
//        }
//
//        faces.fromList(bounts);
//        Rect [] facesArray = faces.toArray();
//
//        Log.i(TAG,"face features:"+facesArray.length);
//        for (int i = 0;i< facesArray.length;i++){
//            Imgproc.rectangle(mRgba,facesArray[i].tl(),facesArray[i].br(),new Scalar(100),3);
//        }

        Mat mRgbaT = new Mat();
        Core.transpose(mRgba,mRgbaT); //转置函数，可以水平的图像变为垂直
        Imgproc.resize(mRgbaT,mRgba, mRgba.size(), 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
        Core.flip(mRgba, mRgba,1); //根据x,y轴翻转，0-x 1-y

        mRgbaT.release();

        return mRgba;
    }
}
