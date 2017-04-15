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
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class HoughTransformationActivity extends BaseopencvActivity {

    private NiceSpinner niceSpinner;
    private Button transform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hough_transformation);

        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        transform = (Button)findViewById(R.id.blue_transform);

        List<String> dataset = new LinkedList<>(Arrays.asList("直线","圆","轮廓"));
        niceSpinner.attachDataSource(dataset);

        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.build);
        ((ImageView) findViewById(R.id.opencv_start)).setImageBitmap(originalBitmap);

        transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int choose = niceSpinner.getSelectedIndex();
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.build);

                Bitmap tempBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                Mat originalMat = new Mat(tempBitmap.getHeight(), tempBitmap.getWidth(), CvType.CV_8U);//注意这里没有通道
                Utils.bitmapToMat(tempBitmap, originalMat);

                Bitmap currentBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, false);

                Mat grayMat = new Mat();
                Mat cannyEdges = new Mat();


                //将彩色图像数据转换为灰度图像数据并存储到grayMat中
                Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_RGB2GRAY);

                Imgproc.Canny(grayMat, cannyEdges, 10, 100);

                switch (choose){
                    case 0:

                        Mat lines = new Mat();
                        //第一个和第二个参数分别是输入和输出，第三个和第四个参数指定像素中r和角度解析度，倒数两个是阀值和最小值
//                        Imgproc.HoughLinesP(cannyEdges, lines, 1, Math.PI / 180, 50, 20, 20);

                        Imgproc.HoughLinesP(cannyEdges, lines, 1, Math.PI / 2, 10, 5, 20);


                        Mat houghLines = new Mat();
                        houghLines.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC1);

                        //在图像上绘制直线
                        for(int i=0;i<lines.cols();i++){

                            double []points = lines.get(0,i);
                            double x1,y1,x2,y2;

                            x1 = points[0];
                            y1 = points[1];
                            x2 = points[2];
                            y2 = points[3];

                            Point pt1 = new Point(x1,y1);
                            Point pt2 = new Point(x2,y2);

                            Imgproc.line(houghLines,pt1,pt2,new Scalar(255,0,0),1);
                        }

                        Utils.matToBitmap(houghLines, currentBitmap);
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(currentBitmap);
                        break;
                    case 1:

                        Mat circles = new Mat();

                        Imgproc.HoughCircles(cannyEdges, circles, Imgproc.CV_HOUGH_GRADIENT, 1, cannyEdges.rows() / 15);
                        //, cannyEdges.rows() / 8));

                        Mat houghCircles = new Mat();
                        houghCircles.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC1);

                        //在图像上绘制直线
                        for(int i=0;i<circles.cols();i++){

                            double []parameters = circles.get(0,i);
                            double x,y;
                            int r;

                            x =parameters[0];
                            y = parameters[1];
                            r =(int)parameters[2];

                            Point center = new Point(x,y);
                            Imgproc.circle(houghCircles,center,r,new Scalar(255,0,0),1);
                        }

                        Utils.matToBitmap(houghCircles, currentBitmap);
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(currentBitmap);

                        break;

                    case 2:

                        Mat hierarchy = new Mat();

                        //找出轮廓
                        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
                        Imgproc.findContours(cannyEdges,contourList,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

                        //在新的图像上绘制轮廓
                        Mat contours = new Mat();
                        contours.create(cannyEdges.rows(),cannyEdges.cols(),CvType.CV_8UC3);
                        Random r = new Random();
                        for(int i=0;i<contourList.size();i++){
                            Imgproc.drawContours(contours,contourList,i,new Scalar(r.nextInt(255),r.nextInt(255),r.nextInt(255)),-1);
                        }

                        Utils.matToBitmap(contours, currentBitmap);
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(currentBitmap);
                        break;
                }



            }
        });

    }


}
