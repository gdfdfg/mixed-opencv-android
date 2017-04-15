package com.chuangweizong.opencv.activity.opencvsample;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import com.chuangweizong.opencv.activity.AngularPointActivity;
import com.chuangweizong.opencv.activity.BlurActivity;
import com.chuangweizong.opencv.activity.EdgeDetectActivity;
import com.chuangweizong.opencv.activity.HoughTransformationActivity;
import com.chuangweizong.opencv.activity.MorphologicalActivity;
import com.chuangweizong.opencv.activity.ObjectDetectActivity;
import com.chuangweizong.opencv.activity.SettingActivity;
import com.chuangweizong.opencv.activity.opencvsample.cameracalibration.CameraCalibrationActivity;
import com.chuangweizong.opencv.activity.opencvsample.colorblobdetection.ColorBlobDetectionActivity;
import com.chuangweizong.opencv.activity.opencvsample.imagemanipulations.ImageManipulationsActivity;
import com.chuangweizong.opencv.activity.opencvsample.puzzle.Puzzle15Activity;
import com.chuangweizong.opencv.activity.opencvsample.tutorial1.Tutorial1Activity;
import com.chuangweizong.opencv.activity.opencvsample.tutorial2.Tutorial2Activity;
import com.chuangweizong.opencv.activity.opencvsample.tutorial3.Tutorial3Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpencvElegantSampleListActivity extends ListActivity {

    protected static HashMap<String, String[]> permissionMap = new HashMap<String, String[]>();
    private static final String KEY_NAME = "com.chuangweizong.opencv";

    static {
        //TODO:Define Activity class and permission here
        permissionMap.put(KEY_NAME, new String[]{
//                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleAdapter baseAdapter = new SimpleAdapter(this,getData(),android.R.layout.simple_list_item_1,new String[]{"title"},new int[]{android.R.id.text1});
        setListAdapter(baseAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent activityIntent = new Intent();
                switch (i){
                    case 0:
                        activityIntent.setClass(getApplicationContext(),Puzzle15Activity.class);
                        break;
                    case 1:
                        activityIntent.setClass(getApplicationContext(),CameraCalibrationActivity.class);
                        break;
                    case 2:
                        activityIntent.setClass(getApplicationContext(),ColorBlobDetectionActivity.class);
                        break;
                    case 4:
                        activityIntent.setClass(getApplicationContext(),ImageManipulationsActivity.class);
                        break;
                    case 5:
                        activityIntent.setClass(getApplicationContext(),Tutorial1Activity.class);
                        break;
                    case 6:
                        activityIntent.setClass(getApplicationContext(),Tutorial2Activity.class);
                        break;
                    case 7:
                        activityIntent.setClass(getApplicationContext(),Tutorial3Activity.class);
                        break;
                }
                startActivity(activityIntent);
            }
        });
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "15-puzzle");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "camera-calibration");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "color-blob-detection");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "face-detection");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "image-manipulations");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "tutorial-1-camerapreview");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "tutorial-2-mixedprocessing");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "tutorial-3-cameracontrol");
        list.add(map);

        return list;
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if(Build.VERSION.SDK_INT < 23) {
//            startActivity(new Intent(Compass.this, CompassMainActivity.class));
//            finish();
//            return;
//        }
        if(PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                startActivity(new Intent(OpencvElegantSampleListActivity.this, SettingActivity.class));
                finish();
            }
        }
    }

    static public boolean isAllPerimissionAlown(Activity activity){
        if(Build.VERSION.SDK_INT < 23) {
            return true;
        }
        String[] permissions = permissionMap.get(KEY_NAME);
        for(String perm : permissions){
            if(PermissionChecker.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(activity,perm+" Denied",Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
}
