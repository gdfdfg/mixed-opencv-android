package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chuangweizhong.opencv.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LensActivity extends Activity {

    String errorMsg;
    private final int CLICK_PHOTO = 1, LOAD_PHOTO = 2;
    private Uri fileUri;
    private ImageView ivImage;
    Mat src, srcOrig;
    private static final String FILE_LOCATION = Environment.getExternalStorageDirectory() + "/Download/PacktBook/Chapter10/";
    private static int scaleFactor;

    protected String TAG = "opencv";

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    //DO YOUR WORK/STUFF HERE
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lens);
        ivImage = (ImageView)findViewById(R.id.ivImage);
        Button bClickImage, bLoadImage;

        bClickImage = (Button)findViewById(R.id.bClickImage);
        bLoadImage = (Button)findViewById(R.id.bLoadImage);
        // bDone = (Button)findViewById(R.id.bDone);

        bClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                errorMsg = null;
                File imagesFolder = new File(FILE_LOCATION);
                imagesFolder.mkdirs();
                File image = new File(imagesFolder, "image_10.jpg");
                fileUri = Uri.fromFile(image);
                Log.d("LensActivity", "File URI = " + fileUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CLICK_PHOTO);
            }
        });
        bLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                errorMsg = null;
                startActivityForResult(intent, LOAD_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Log.d("LensActivity", requestCode + " " + CLICK_PHOTO + " " + resultCode + " " + RESULT_OK);

        switch(requestCode) {
            case CLICK_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        Log.d("LensActivity", fileUri.toString());
                        final InputStream imageStream = getContentResolver().openInputStream(fileUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        srcOrig = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
                        src = new Mat();
                        Utils.bitmapToMat(selectedImage, srcOrig);

                        scaleFactor = calcScaleFactor(srcOrig.rows(), srcOrig.cols());

                        Imgproc.resize(srcOrig, src, new Size(srcOrig.rows()/scaleFactor, srcOrig.cols()/scaleFactor));
                        getPage();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case LOAD_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        InputStream stream = getContentResolver().openInputStream(imageReturnedIntent.getData());
                        final Bitmap selectedImage = BitmapFactory.decodeStream(stream);
                        stream.close();
                        ivImage.setImageBitmap(selectedImage);
                        srcOrig = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
                        Utils.bitmapToMat(selectedImage, srcOrig);
                        scaleFactor = calcScaleFactor(srcOrig.rows(), srcOrig.cols());
                        src = new Mat();
                        Imgproc.resize(srcOrig, src, new Size(srcOrig.rows()/scaleFactor, srcOrig.cols()/scaleFactor));
                        Imgproc.GaussianBlur(src, src, new Size(5,5), 1);
                        getPage();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void getPage(){

        new AsyncTask<Void, Void, Bitmap>() {
            ProgressDialog dialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(LensActivity.this, "Grabbing page", "Please Wait");
            }

            @Override
            protected Bitmap doInBackground(Void... params) {

                Mat srcRes = new Mat( src.size(), src.type() );
                Mat srcGray = new Mat();

                Mat samples = new Mat(src.rows() * src.cols(), 3, CvType.CV_32F);
                for( int y = 0; y < src.rows(); y++ ) {
                    for( int x = 0; x < src.cols(); x++ ) {
                        for( int z = 0; z < 3; z++) {
                            samples.put(x + y*src.cols(), z, src.get(y,x)[z]);
                        }
                    }
                }

                int clusterCount = 2;
                Mat labels = new Mat();
                int attempts = 5;
                Mat centers = new Mat();

                Core.kmeans(samples, clusterCount, labels, new TermCriteria(TermCriteria.MAX_ITER | TermCriteria.EPS, 10000, 0.0001), attempts, Core.KMEANS_PP_CENTERS, centers);

                double dstCenter0 = calcWhiteDist(centers.get(0, 0)[0], centers.get(0, 1)[0], centers.get(0, 2)[0]);
                double dstCenter1 = calcWhiteDist(centers.get(1, 0)[0], centers.get(1, 1)[0], centers.get(1, 2)[0]);

                int paperCluster = (dstCenter0 < dstCenter1)?0:1;
//
//                double[] black = {0, 0, 0};
//                double[] white = {255, 255, 255};

                for( int y = 0; y < src.rows(); y++ ) {
                    for( int x = 0; x < src.cols(); x++ )
                    {
                        int cluster_idx = (int)labels.get(x + y*src.cols(),0)[0];
                        if(cluster_idx != paperCluster){
                            srcRes.put(y,x, 0, 0, 0, 255);
                        } else {
                            srcRes.put(y,x, 255, 255, 255, 255);
                        }
                    }
                }

                // Imgproc.medianBlur(srcRes, srcRes, 5);

//                Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(10, 10));

//                // Opening to remove small noise pixels
//                Imgproc.erode(srcRes, srcRes, kernel);
//                Imgproc.dilate(srcRes, srcRes, kernel);
//
//                // Closing to fill in gaps
//                Imgproc.dilate(srcRes, srcRes, kernel);
//                Imgproc.erode(srcRes, srcRes, kernel);


                // TODO Potential error in opencv
                Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
                Imgproc.Canny(srcGray, srcGray, 50, 150);
                List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                Mat hierarchy = new Mat();

                Imgproc.findContours(srcGray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

                int index = 0;
                double maxim = Imgproc.contourArea(contours.get(0));

                for (int contourIdx = 1; contourIdx < contours.size(); contourIdx++) {
                    double temp;
                    temp=Imgproc.contourArea(contours.get(contourIdx));
                    if(maxim<temp)
                    {
                        maxim=temp;
                        index=contourIdx;
                    }
                }

                Mat drawing = Mat.zeros(srcRes.size(), CvType.CV_8UC1);
                Imgproc.drawContours(drawing, contours, index, new Scalar(255), 1);

                Mat lines = new Mat();
                Imgproc.HoughLinesP(drawing, lines, 1, Math.PI/180, 70, 30, 10);

                ArrayList<Point> corners = new ArrayList<Point>();
                for (int i = 0; i < lines.cols(); i++)
                {
                    for (int j = i+1; j < lines.cols(); j++) {
                        double[] line1 = lines.get(0, i);
                        double[] line2 = lines.get(0, j);

                        Point pt = findIntersection(line1, line2);
                        Log.d("com.packtpub.chapter10", pt.x+" "+pt.y);
                        if (pt.x >= 0 && pt.y >= 0 && pt.x <= drawing.cols() && pt.y <= drawing.rows()){
                            if(!exists(corners, pt)){
                                corners.add(pt);
                            }
                        }
                    }
                }

                if(corners.size() != 4){
                    errorMsg =  "Cannot detect perfect corners";
                    Bitmap bitmap = Bitmap.createBitmap(drawing.cols(), drawing.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(drawing, bitmap);

                    return bitmap;
//                    return null;
                }

                sortCorners(corners);

                if(corners.size() == 0){
                    errorMsg = "Cannot sort corners";
                    return null;
                }

                double top = Math.sqrt(Math.pow(corners.get(0).x - corners.get(1).x, 2) + Math.pow(corners.get(0).y - corners.get(1).y, 2));
                double right = Math.sqrt(Math.pow(corners.get(1).x - corners.get(2).x, 2) + Math.pow(corners.get(1).y - corners.get(2).y, 2));
                double bottom = Math.sqrt(Math.pow(corners.get(2).x - corners.get(3).x, 2) + Math.pow(corners.get(2).y - corners.get(3).y, 2));
                double left = Math.sqrt(Math.pow(corners.get(3).x - corners.get(1).x, 2) + Math.pow(corners.get(3).y - corners.get(1).y, 2));

                Mat quad = Mat.zeros(new Size(Math.max(top, bottom), Math.max(left, right)), CvType.CV_8UC3);

                ArrayList<Point> result_pts = new ArrayList<Point>();
                result_pts.add(new Point(0, 0));
                result_pts.add(new Point(quad.cols(), 0));
                result_pts.add(new Point(quad.cols(), quad.rows()));
                result_pts.add(new Point(0, quad.rows()));


                Mat cornerPts = Converters.vector_Point2f_to_Mat(corners);
                Mat resultPts = Converters.vector_Point2f_to_Mat(result_pts);

                Log.d("com.packtpub.chapter10", cornerPts.checkVector(2, CvType.CV_32F)+" "+resultPts.checkVector(2, CvType.CV_32F)+" "+CvType.CV_32F+" "+cornerPts.type());

                Mat transformation = Imgproc.getPerspectiveTransform(cornerPts, resultPts);
                Imgproc.warpPerspective(srcOrig, quad, transformation, quad.size());

                Bitmap bitmap = Bitmap.createBitmap(quad.cols(), quad.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(quad, bitmap);

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                dialog.dismiss();
                if(bitmap!=null) {
                    ivImage.setImageBitmap(bitmap);
                } else if (errorMsg != null){
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mOpenCVCallBack);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mOpenCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    static double calcWhiteDist(double r, double g, double b){
        return Math.sqrt(Math.pow(255 - r, 2) + Math.pow(255 - g, 2) + Math.pow(255 - b, 2));
    }

    static Point findIntersection(double[] line1, double[] line2) {
        double start_x1 = line1[0], start_y1 = line1[1], end_x1 = line1[2], end_y1 = line1[3], start_x2 = line2[0], start_y2 = line2[1], end_x2 = line2[2], end_y2 = line2[3];
        double denominator = ((start_x1 - end_x1) * (start_y2 - end_y2)) - ((start_y1 - end_y1) * (start_x2 - end_x2));

        if (denominator!=0)
        {
            Point pt = new Point();
            pt.x = ((start_x1 * end_y1 - start_y1 * end_x1) * (start_x2 - end_x2) - (start_x1 - end_x1) * (start_x2 * end_y2 - start_y2 * end_x2)) / denominator;
            pt.y = ((start_x1 * end_y1 - start_y1 * end_x1) * (start_y2 - end_y2) - (start_y1 - end_y1) * (start_x2 * end_y2 - start_y2 * end_x2)) / denominator;
            return pt;
        }
        else
            return new Point(-1, -1);
    }

    static boolean exists(ArrayList<Point> corners, Point pt){
        for(int i=0; i<corners.size(); i++){
            if(Math.sqrt(Math.pow(corners.get(i).x-pt.x, 2)+Math.pow(corners.get(i).y-pt.y, 2)) < 10){
                return true;
            }
        }
        return false;
    }

    static void sortCorners(ArrayList<Point> corners)
    {
        ArrayList<Point> top, bottom;

        top = new ArrayList<Point>();
        bottom = new ArrayList<Point>();

        Point center = new Point();

        for(int i=0; i<corners.size(); i++){
            center.x += corners.get(i).x/corners.size();
            center.y += corners.get(i).y/corners.size();
        }

        for (int i = 0; i < corners.size(); i++)
        {
            if (corners.get(i).y < center.y)
                top.add(corners.get(i));
            else
                bottom.add(corners.get(i));
        }
        corners.clear();

        if (top.size() == 2 && bottom.size() == 2){
            Point top_left = top.get(0).x > top.get(1).x ? top.get(1) : top.get(0);
            Point top_right = top.get(0).x > top.get(1).x ? top.get(0) : top.get(1);
            Point bottom_left = bottom.get(0).x > bottom.get(1).x ? bottom.get(1) : bottom.get(0);
            Point bottom_right = bottom.get(0).x > bottom.get(1).x ? bottom.get(0) : bottom.get(1);

            top_left.x *= scaleFactor;
            top_left.y *= scaleFactor;

            top_right.x *= scaleFactor;
            top_right.y *= scaleFactor;

            bottom_left.x *= scaleFactor;
            bottom_left.y *= scaleFactor;

            bottom_right.x *= scaleFactor;
            bottom_right.y *= scaleFactor;

            corners.add(top_left);
            corners.add(top_right);
            corners.add(bottom_right);
            corners.add(bottom_left);
        }
    }

    private static int calcScaleFactor(int rows, int cols){
        int idealRow, idealCol;
        if(rows<cols){
            idealRow = 240;
            idealCol = 320;
        } else {
            idealCol = 240;
            idealRow = 320;
        }
        int val = Math.min(rows / idealRow, cols / idealCol);
        if(val<=0){
            return 1;
        } else {
            return val;
        }
    }

}
