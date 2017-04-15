package com.chuangweizong.opencv.activity;

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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EdgeDetectActivity extends BaseopencvActivity {

    private NiceSpinner niceSpinner;
    private Button transform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edge_detect);
        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        transform = (Button)findViewById(R.id.blue_transform);

        List<String> dataset = new LinkedList<>(Arrays.asList("Canny", "Sobel"));
        niceSpinner.attachDataSource(dataset);

        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.build);
        ((ImageView) findViewById(R.id.opencv_start)).setImageBitmap(originalBitmap);

        transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int choose = niceSpinner.getSelectedIndex();
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.build);

                Bitmap tempBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);

                Mat originalMat = new Mat(tempBitmap.getHeight(), tempBitmap.getWidth(), CvType.CV_8U);//注意这里没有通道
                Utils.bitmapToMat(tempBitmap, originalMat);

                Bitmap currentBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, false);

                Mat grayMat = new Mat();
                //将彩色图像数据转换为灰度图像数据并存储到grayMat中
                Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_RGB2GRAY);

//                Utils.matToBitmap(grayMat, currentBitmap);

                Mat cannyEdges = new Mat();
                switch (choose) {
                    case 0:
                        Imgproc.Canny(grayMat,cannyEdges,10,100);
                        break;
                    case 1:
                        Mat grad_x = new Mat();
                        Mat abs_grad_x =new Mat();

                        Mat grad_y = new Mat();
                        Mat abs_grad_y =new Mat();

                        //计算水平方向的梯度
                        Imgproc.Sobel(grayMat,grad_x,CvType.CV_16S,1,0,3,1,0);

                        //计算垂直方向的梯度
                        Imgproc.Sobel(grayMat,grad_y,CvType.CV_16S,0,1,3,1,0);

                        //计算两个方向向上的梯度绝对值
                        Core.convertScaleAbs(grad_x,abs_grad_x);
                        Core.convertScaleAbs(grad_y,abs_grad_y);

                        //计算结果梯度
                        Core.addWeighted(abs_grad_x,0.5,abs_grad_y,0.5,1,cannyEdges);
                        break;
                }
                Utils.matToBitmap(cannyEdges, currentBitmap);
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(currentBitmap);
            }
        });

    }

}
