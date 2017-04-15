package com.chuangweizong.opencv.activity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.lib.ocr.Tesseract;
import com.chuangweizong.opencv.view.spinner.NiceSpinner;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ChineseDetectActivity extends BaseopencvActivity {

    private static final String    TAG = "opencv";

    private NiceSpinner niceSpinner;
    private Button transform;

    private String tessdataPath;

    private Bitmap bitmap;

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

            InputStream is = asset.open("tesseract/tessdata/chi_sim.traineddata");
            File cascadeDir = getDir("tesseract", Context.MODE_PRIVATE);

            File tessdataDir =new File(cascadeDir.getAbsolutePath()+"/tessdata/");
            if(!tessdataDir.exists()){
                tessdataDir.mkdirs();
            }

            File mCascadeFile = new File(cascadeDir+"/tessdata/","chi_sim.traineddata");

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

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chinese);
        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);
        ((ImageView) findViewById(R.id.idcard)).setImageBitmap(bitmap);

        List<String> dataset = new LinkedList<>(Arrays.asList("汉字识别"));
        niceSpinner.attachDataSource(dataset);

        transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int choose = niceSpinner.getSelectedIndex();


                switch (choose) {
                    case 0:
                        try {

                            byte[] bytes = Tesseract.getText(bitmap,tessdataPath).getBytes("UTF-8");
                            String s1 = new String(bytes,"UTF-8");
                            Toast.makeText(getBaseContext(), "检测到数字:"+s1,Toast.LENGTH_LONG).show();
                            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        break;
                }

            }
        });

    }


}
