package com.chuangweizong.opencv.activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.view.spinner.NiceSpinner;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ObjectDetectActivity extends BaseopencvActivity {

    private NiceSpinner niceSpinner;
    private Button transform;
    private Bitmap currentBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detect);

        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        transform = (Button)findViewById(R.id.blue_transform);

        List<String> dataset = new LinkedList<>(Arrays.asList("ORB","BRISK","FREAK"));
        niceSpinner.attachDataSource(dataset);

        transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int choose = niceSpinner.getSelectedIndex();
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.build);

                Bitmap tempBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                Mat originalMat = new Mat(tempBitmap.getHeight(), tempBitmap.getWidth(), CvType.CV_8U);//注意这里没有通道
                Utils.bitmapToMat(tempBitmap, originalMat);

                currentBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, false);

                Utils.bitmapToMat(tempBitmap, originalMat);

                ObjectDetectAsyncTask task = new ObjectDetectAsyncTask(choose);

                Mat copMat = new Mat();
                originalMat.copyTo(copMat);
                task.execute(originalMat, copMat);
            }
        });

    }

    private class ObjectDetectAsyncTask extends AsyncTask<Mat,Void,Bitmap>{

        private long startTime,endTime;
        private int type;

        public ObjectDetectAsyncTask(int type){
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startTime = System.currentTimeMillis();
        }

        @Override
        protected Bitmap doInBackground(Mat... mats) {
            Log.i(TAG,"doInBackground");
            return executeTask(mats);
        }

        private Bitmap executeTask(Mat... mats) {

            Log.i(TAG,"executeTask start");
            FeatureDetector detector = null;
            MatOfKeyPoint keyPoint1,keyPoint2;
            DescriptorExtractor descriptorExtractor = null;

            Mat descriptors1 = new Mat();
            Mat descriptors2 = new Mat();

            DescriptorMatcher descriptorMatcher = null ;
            if(type == 0){
                detector = FeatureDetector.create(FeatureDetector.ORB);
                descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
                descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            }else if(type == 1){
                detector = FeatureDetector.create(FeatureDetector.BRISK);
                descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.BRISK);
                descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            }else if(type == 2){
                detector = FeatureDetector.create(FeatureDetector.FAST);
                descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.FREAK);
                descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            }

            Mat src1 = mats[0],src2 = mats[1];

            Point center = new Point(src2.cols()/2f, src2.rows()/2f);
            double angle = -50.0;
            double scale = 0.6;
            Mat rotateMat = Imgproc.getRotationMatrix2D(center, angle, scale);
            Imgproc.warpAffine(src2, src2, rotateMat, src2.size());

            keyPoint1 = new MatOfKeyPoint();
            keyPoint2 = new MatOfKeyPoint();
            detector.detect(src2,keyPoint2);
            detector.detect(src1,keyPoint1);

            int keypointObject1 = keyPoint1.toArray().length;
            int keypointObject2 = keyPoint2.toArray().length;

            descriptorExtractor.compute(src1,keyPoint1,descriptors1);
            descriptorExtractor.compute(src2,keyPoint2,descriptors2);


            MatOfDMatch matches = new MatOfDMatch();
//            descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2);//暴力匹配器

            descriptorMatcher.match(descriptors1,descriptors2,matches);

            Mat resout = drawMatches(src1,keyPoint1,src2,keyPoint2,matches,false);

            Bitmap currentBitmap = Bitmap.createBitmap(resout.cols(), resout.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(resout,currentBitmap);

            Log.i(TAG, "executeTask end");
            return currentBitmap;
        }

        private Mat drawMatches(Mat img1,MatOfKeyPoint key1,Mat img2,MatOfKeyPoint key2,MatOfDMatch matches,boolean imageOnly){

            Mat out = new Mat();
            Mat im1 = new Mat();
            Mat im2 = new Mat();
            Imgproc.cvtColor(img1,im1,Imgproc.COLOR_BGR2RGB);
            Imgproc.cvtColor(img2,im2,Imgproc.COLOR_BGR2RGB);

            if(imageOnly){
                MatOfDMatch emptyMatch = new MatOfDMatch();
                MatOfKeyPoint emptykey1 = new MatOfKeyPoint();
                MatOfKeyPoint emptykey2 = new MatOfKeyPoint();
                Features2d.drawMatches(im1,emptykey1,im2,emptykey2,emptyMatch,out);
            }else{
                Features2d.drawMatches(im1,key1,im2,key2,matches,out);
            }

            Imgproc.cvtColor(out, out, Imgproc.COLOR_BGR2RGB);
            Imgproc.putText(out, "picture ", new Point(img1.width() / 2, 30), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
            Imgproc.putText(out,"match ",new Point(img1.width()+img2.width()/2,30),Core.FONT_HERSHEY_PLAIN,2,new Scalar(255,0,0),3);
            return out;
        };


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            endTime = System.currentTimeMillis();

            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);
        }

    }

}
