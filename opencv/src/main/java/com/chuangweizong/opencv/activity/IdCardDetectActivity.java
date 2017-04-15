package com.chuangweizong.opencv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.lib.ocr.Tesseract;
import com.chuangweizong.opencv.utils.AssetsCopyTOSDcard;
import com.chuangweizong.opencv.view.spinner.NiceSpinner;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class IdCardDetectActivity extends BaseopencvActivity {

    private static final String    TAG = "opencv";

    private NiceSpinner niceSpinner;
    private Button transform;

    private String tessdataPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card_detect);

//        String path="tesseract/tessdata/eng.traineddata";
//        AssetsCopyTOSDcard assetsCopyTOSDcard=new AssetsCopyTOSDcard(getApplicationContext());
//        assetsCopyTOSDcard.AssetToSD(path, android.os.Environment
//                .getExternalStorageDirectory()
//                .getAbsolutePath()
//                + "/tesseract/");

        try {

            AssetManager asset=getAssets();

            InputStream is = asset.open("tesseract/tessdata/eng.traineddata");
            File cascadeDir = getDir("tesseract", Context.MODE_PRIVATE);

            File tessdataDir =new File(cascadeDir.getAbsolutePath()+"/tessdata/");
            if(!tessdataDir.exists()){
                tessdataDir.mkdirs();
            }

            File mCascadeFile = new File(cascadeDir+"/tessdata/","eng.traineddata");

            tessdataPath = cascadeDir.getAbsolutePath();
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte [] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1){
                os.write(buffer,0,bytesRead);
            }

            is.close();
            os.close();

        } catch (FileNotFoundException e) {
            Log.i(TAG, "未找到级联分类器");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "文件异常");
        }


        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        transform = (Button)findViewById(R.id.blue_transform);

        List<String> dataset = new LinkedList<>(Arrays.asList("身份证"));
        niceSpinner.attachDataSource(dataset);

        transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int choose = niceSpinner.getSelectedIndex();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.idcard);
                Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
                Utils.bitmapToMat(bitmap, src);

                //转换为灰度图像
                Mat grayMat = new Mat();
                //将彩色图像数据转换为灰度图像数据并存储到grayMat中
                Imgproc.cvtColor(src, grayMat, Imgproc.COLOR_RGB2GRAY);
                //将图像阀值化


                switch (choose) {
                    case 0:
                        Imgproc.threshold(grayMat, grayMat, 200, 255, Imgproc.THRESH_BINARY);//设置阀值
                        //去除噪声
                        Imgproc.medianBlur(grayMat, grayMat, 3);//中值模糊
                        Imgproc.medianBlur(grayMat, grayMat, 3);//中值模糊
                        //将图像膨胀
                        Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(8,8));
                        Imgproc.erode(grayMat, grayMat, kernelErode);
                        Imgproc.erode(grayMat, grayMat, kernelErode);
                        Imgproc.erode(grayMat, grayMat, kernelErode);
                        Imgproc.erode(grayMat, grayMat, kernelErode);

//                        Imgproc.Canny(grayMat, grayMat, 10, 100);

                        Mat hierarchy = new Mat();

                        //找出轮廓
                        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
                        Imgproc.findContours(grayMat,contourList,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

                        //在新的图像上绘制轮廓
                        Mat contours = new Mat();
                        contours.create(grayMat.rows(),grayMat.cols(),CvType.CV_8UC3);
                        Random r = new Random();

                        int maxLengthIndex=1;
                        int maxLength=0;
                        //找到最长的轮廓
                        int x=0,y=0,width=0,heigh=0;
                        for(int i=0;i<contourList.size();i++){
                            MatOfPoint point = contourList.get(i);
                            Rect rect=Imgproc.boundingRect(point);//得到轮廓的外矩形区域

                            if(maxLength<rect.width&&rect.width<grayMat.cols()*(3/4f)){
                                maxLength = rect.width;
                                maxLengthIndex = i;
                                x = rect.x;
                                y = rect.y;
                                width = rect.width;
                                heigh = rect.height;
                            }
                        }

//                        Imgproc.drawContours(contours,contourList,maxLengthIndex,new Scalar(r.nextInt(255),r.nextInt(255),r.nextInt(255)),-1);

                        //选取感兴趣的区域
                        Mat idcardArea= new Mat(src,new Rect(x,y,width,heigh));
                        Bitmap processedImage = Bitmap.createBitmap(idcardArea.cols(), idcardArea.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(idcardArea, processedImage);
//                        Bitmap processedImage = Bitmap.createBitmap(grayMat.cols(), grayMat.rows(), Bitmap.Config.ARGB_8888);
//                        Utils.matToBitmap(grayMat, processedImage);

                        Toast.makeText(getBaseContext(), "检测到数字:"+Tesseract.getText(processedImage,tessdataPath),Toast.LENGTH_LONG).show();
                                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(processedImage);
                        break;
                }

            }
        });

    }


}
