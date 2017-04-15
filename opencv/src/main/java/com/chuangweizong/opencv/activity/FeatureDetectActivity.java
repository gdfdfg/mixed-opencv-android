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
import com.chuangweizong.opencv.lib.detect.FaceFeatures;
import com.chuangweizong.opencv.lib.detect.LibFeaturesDetector;
import com.chuangweizong.opencv.view.spinner.NiceSpinner;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FeatureDetectActivity extends BaseopencvActivity {

    private NiceSpinner niceSpinner;
    private Button transform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_detect);

        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        transform = (Button)findViewById(R.id.blue_transform);

        List<String> dataset = new LinkedList<>(Arrays.asList("FastFeature","SurfFeature"));
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
                //将彩色图像数据转换为灰度图像数据并存储到grayMat中
                Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_RGB2GRAY);

                switch (choose) {
                    case 0:
                        FaceFeatures.FindFeatures(grayMat.getNativeObjAddr(), originalMat.getNativeObjAddr());
                        break;
                    case 1:
                        LibFeaturesDetector.FeaturesSurf(grayMat.getNativeObjAddr(), originalMat.getNativeObjAddr());
                        break;
                }
                Utils.matToBitmap(originalMat, currentBitmap);
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(currentBitmap);
            }
        });

    }

}
