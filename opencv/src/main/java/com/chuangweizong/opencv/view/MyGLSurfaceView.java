package com.chuangweizong.opencv.view;

import java.util.Random;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import android.widget.Toast;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.helper.FloodFillAlgorithm1;
import com.chuangweizong.opencv.lib.imageprocess.Contour;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

@SuppressLint("ClickableViewAccessibility")
public class MyGLSurfaceView extends GLSurfaceView{

        private static final String    TAG = "opencv";


	private TextureRenderer renderer;

	private Context context;

	private Bitmap currentBitmap;

	private FloodFillAlgorithm1 floodFillAlgorithm;

	Random random = new Random();

    private  boolean isInit = false;

    private Mat mask;//掩码

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}

	private void initView(){

        if(!isInit){
            if (!OpenCVLoader.initDebug()) {
                Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, mLoaderCallback);
            } else {
                Log.d(TAG, "OpenCV library found inside package. Using it!");
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
        }

        renderer = new TextureRenderer();

        this.setEGLContextClientVersion(2);
        this.setRenderer(renderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        BitmapFactory.Options opt = new BitmapFactory.Options();

         opt.inPreferredConfig = Bitmap.Config.ARGB_8888;

        currentBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test4,opt);

	}
        private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(context) {
                @Override
                public void onManagerConnected(int status) {
                        switch (status) {
                                case LoaderCallbackInterface.SUCCESS:
                                {
                                        isInit = true;

                                } break;
                                default:
                                {
                                        super.onManagerConnected(status);
                                } break;
                        }
                }
        };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // TODO Auto-generated method stub
        super.surfaceChanged(holder, format, w, h);



        Matrix matrix = new Matrix();

        matrix.postScale(((float) w) / currentBitmap.getWidth(), ((float) h) / currentBitmap.getHeight()); //长和宽放大缩小的比例

        Bitmap resizeBmp = Bitmap.createBitmap(currentBitmap,0,0,currentBitmap.getWidth(),currentBitmap.getHeight(),matrix,true);

        floodFillAlgorithm = new FloodFillAlgorithm1(resizeBmp);

        Mat src = new Mat(resizeBmp.getHeight(),resizeBmp.getWidth(), CvType.CV_8UC4);

        Mat des = new Mat();
        Utils.bitmapToMat(resizeBmp, src);
        Imgproc.cvtColor(src, des, Imgproc.COLOR_BGRA2RGB);

//        mask = new Mat();
//        mask.create(resizeBmp.getHeight() + 2, resizeBmp.getHeight() + 2, CvType.CV_8UC1);

        Mat cannyEdges = new Mat();
        Imgproc.Canny(src, cannyEdges, 10, 100);
        Bitmap processedImage = Bitmap.createBitmap(des.cols(), des.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cannyEdges, processedImage);
        floodFillAlgorithm.setBitmap(processedImage);

        Core.copyMakeBorder(cannyEdges, cannyEdges, 1, 1, 1, 1, Core.BORDER_REPLICATE);
        mask = cannyEdges;

//
//        Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(3,3));
//        Imgproc.erode(des, des, kernelErode);
        Imgproc.threshold(des, des, 230, 255, Imgproc.THRESH_TOZERO);
        Imgproc.medianBlur(des, des, 3);//中值模糊
//        Imgproc.blur(des, des, new Size(3, 3));

        floodFillAlgorithm.setCurrMat(des);

        Utils.matToBitmap(des,resizeBmp);
        renderer.setImageBitmap(resizeBmp);
        this.requestRender();

    }


	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int iAction = event.getAction();
        if (iAction == MotionEvent.ACTION_CANCEL
                || iAction == MotionEvent.ACTION_MOVE)
        {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();

        Log.i(TAG,"isInit:"+isInit);
        if(isInit){
                Mat des=floodFillAlgorithm.getCurrMat();

                Imgproc.floodFill(des, mask, new Point(x, y), new Scalar(100, 100, 100));
                floodFillAlgorithm.setCurrMat(des);

                Mat temp = new Mat();
                des.copyTo(temp);
//                Imgproc.GaussianBlur(temp, temp, new Size(5, 5), 150);//高斯模糊
                Imgproc.medianBlur(temp, temp, 3);//中值模糊
            Imgproc.medianBlur(temp, temp, 3);//中值模糊

                Bitmap processedImage = Bitmap.createBitmap(des.cols(), des.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(temp, processedImage);

                renderer.setImageBitmap(processedImage);
                Log.i(TAG, "renderer");
//                processedImage.recycle();
        }

//        for(int i=0;i<10;i++){
//           Log.i("opencv","----------------->1:"+pix[i]);
//        }
//
//        Bitmap resultImg = Bitmap.createBitmap(bw, bh, Bitmap.Config.RGB_565);
//        int pixresult[] = Contour.floodFill(pix, bw, bh,x,y);
////      int pixresult[] = Contour.floodFill2(resultImg);
//
//        for(int i=0;i<10;i++){
//            Log.i("opencv","----------------->2:"+pixresult[i]);
//        }
//        resultImg.setPixels(pixresult, 0, bw, 0, 0,bw, bh);
//        floodFillAlgorithm.setBitmap(resultImg);

//        renderer.setImageBitmap(resultImg);
    	requestRender();

        queueEvent(new Runnable() {
            // 这个方法会在渲染线程里被调用
            public void run() {

//                mMyRenderer.handleDpadCenter();
            }});
        return true;
	}

	public void replace(){

    }

}
