package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.view.spinner.NiceSpinner;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class HogSVMPeopleActivity extends BaseopencvActivity {

    private NiceSpinner niceSpinner;
    private Button transform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hog_svmpeople);

        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        transform = (Button)findViewById(R.id.blue_transform);

        List<String> dataset = new LinkedList<>(Arrays.asList("行人检测"));
        niceSpinner.attachDataSource(dataset);

        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.peoples);
        ((ImageView) findViewById(R.id.opencv_start)).setImageBitmap(originalBitmap);

        transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int choose = niceSpinner.getSelectedIndex();
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.peoples);

                Bitmap tempBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                Mat originalMat = new Mat(tempBitmap.getHeight(), tempBitmap.getWidth(), CvType.CV_8U);//注意这里没有通道
                Utils.bitmapToMat(tempBitmap, originalMat);

                Bitmap currentBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, false);

                Mat grayMat = new Mat();
                Mat cannyEdges = new Mat();


                //将彩色图像数据转换为灰度图像数据并存储到grayMat中
                Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_RGB2GRAY);

//                Imgproc.Canny(grayMat, cannyEdges, 10, 100);

                switch (choose) {
                    case 0:

                        HOGDescriptor hog = new HOGDescriptor();
                        hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());

                        MatOfRect faces = new MatOfRect();
                        MatOfDouble weights = new MatOfDouble();

                        Mat people = new Mat();

                        hog.detectMultiScale(grayMat,faces,weights);
                        originalMat.copyTo(people);
                        //在图像上绘制行人的包围框
                        Rect[] facesArray = faces.toArray();
                        for(int i = 0;i < facesArray.length;i++){
                            Imgproc.rectangle(people,facesArray[i].tl(),facesArray[i].br(),new Scalar(100),3);
                        }

                        Imgproc.rectangle(people,facesArray[0].tl(),facesArray[0].br(),new Scalar(100),3);
                        //将Mat转换回位图
                        Utils.matToBitmap(people,currentBitmap);

                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(currentBitmap);
                        break;
                }
            }
        });
    }
}
