package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.lib.Test;
import com.chuangweizong.opencv.view.spinner.NiceSpinner;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */
public class BlurActivity extends Activity {

    private static final String    TAG = "opencv";

    private NiceSpinner niceSpinner;
    private Button transform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv_blue);

        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        transform = (Button)findViewById(R.id.blue_transform);

        List<String> dataset = new LinkedList<>(Arrays.asList("均值模糊", "高斯模糊", "中值模糊", "锐化"));
        niceSpinner.attachDataSource(dataset);

        transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int choose = niceSpinner.getSelectedIndex();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lena);
                Mat src = new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC4);
                Utils.bitmapToMat(bitmap, src);
                switch (choose){
                    case 0:
                        Imgproc.blur(src, src, new Size(3, 3));
                        break;
                    case 1:
                        Imgproc.GaussianBlur(src, src, new Size(5, 5),0);//高斯模糊
                        break;
                    case 2:
                        Imgproc.medianBlur(src, src, 3);//中值模糊
                        break;
                    case 3:
                        Mat kernel = new Mat(3,3,CvType.CV_16SC1);//锐化
                        kernel.put(0,0,0,-1,0,-1,5,-1,0,-1,0);//自定义核
                        Imgproc.filter2D(src,src,src.depth(),kernel);
                        break;
                }
                Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(src, processedImage);
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(processedImage);
            }
        });



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

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {


                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

}
