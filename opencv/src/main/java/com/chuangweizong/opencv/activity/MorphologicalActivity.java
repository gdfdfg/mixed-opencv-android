package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.view.spinner.NiceSpinner;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MorphologicalActivity extends BaseopencvActivity {

    private NiceSpinner niceSpinner;
    private Button transform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morphological);

        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        transform = (Button)findViewById(R.id.blue_transform);

        List<String> dataset = new LinkedList<>(Arrays.asList("膨胀", "腐蚀"));
        niceSpinner.attachDataSource(dataset);

        transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int choose = niceSpinner.getSelectedIndex();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.swall);
                Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
                Utils.bitmapToMat(bitmap, src);

                Mat grayMat = new Mat();
                //将彩色图像数据转换为灰度图像数据并存储到grayMat中
                Imgproc.cvtColor(src, grayMat, Imgproc.COLOR_RGB2GRAY);

                Bitmap grayBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                Utils.matToBitmap(grayMat, grayBmp);
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(grayBmp);

                switch (choose) {
                    case 0:
                        //MORPH_RECT,MORPH_CROSS,MORPH_ELLIPSE
                        Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(3,3));
                        Imgproc.erode(src, src, kernelErode);
                        break;
                    case 1:
                        //MORPH_RECT,MORPH_CROSS,MORPH_ELLIPSE]
                        Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3));
                        Imgproc.dilate(src, src, kernelDilate);
                        break;
                }
                Bitmap kernel = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                Utils.matToBitmap(src, kernel);
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(kernel);
            }
        });

    }



}
