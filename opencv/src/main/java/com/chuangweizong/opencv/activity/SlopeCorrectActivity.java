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
import com.chuangweizong.opencv.lib.TextImageCorrect;
import com.chuangweizong.opencv.view.spinner.NiceSpinner;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SlopeCorrectActivity extends BaseopencvActivity {

    private NiceSpinner niceSpinner;
    private Button transform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slope_correct);

        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        transform = (Button)findViewById(R.id.blue_transform);

        List<String> dataset = new LinkedList<>(Arrays.asList("膨胀", "腐蚀"));
        niceSpinner.attachDataSource(dataset);

        transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int choose = niceSpinner.getSelectedIndex();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.incline);
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


                        break;
                }

                Bitmap kernel = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
                TextImageCorrect.getTextImageCorrectFromeNative(kernel,grayMat.nativeObj,src.nativeObj);

                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(kernel);
            }
        });


    }

}
