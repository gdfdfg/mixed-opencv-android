package com.chuangweizong.opencv.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import com.chuangweizong.opencv.activity.opencvsample.OpencvElegantSampleListActivity;
import com.daiyinger.carplate.OpenERActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpencvSampleListActivity extends ListActivity {

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
                        activityIntent.setClass(getApplicationContext(),BlurActivity.class);
                        break;
                    case 1:
                        activityIntent.setClass(getApplicationContext(),MorphologicalActivity.class);
                        break;
                    case 2:
                        activityIntent.setClass(getApplicationContext(),EdgeDetectActivity.class);
                        break;
                    case 3:
                        activityIntent.setClass(getApplicationContext(),AngularPointActivity.class);
                        break;
                    case 4:
                        activityIntent.setClass(getApplicationContext(),HoughTransformationActivity.class);
                        break;
                    case 5:
                        activityIntent.setClass(getApplicationContext(),ObjectDetectActivity.class);
                        break;
                    case 6:
                        activityIntent.setClass(getApplicationContext(), FeatureDetectActivity.class);
                        break;
                    case 7:
                        activityIntent.setClass(getApplicationContext(),FloodfillActivity.class);
                        break;
                    case 8:
                        activityIntent.setClass(getApplicationContext(),IdCardDetectActivity.class);
                        break;
                    case 9:
                        activityIntent.setClass(getApplicationContext(),OpencvFaceHaarDetectActivity.class);
                        break;
                    case 10:
                        activityIntent.setClass(getApplicationContext(),OpencvFaceLBPDetectActivity.class);
                        break;
                    case 11:
                        activityIntent.setClass(getApplicationContext(),FaceDetectActivity.class);
                        break;
                    case 12:
                        activityIntent.setClass(getApplicationContext(),HogSVMPeopleActivity.class);
                        break;
                    case 13:
                        activityIntent.setClass(getApplicationContext(),FlowAndTrackerActivity.class);
                        break;
                    case 14:
                        activityIntent.setClass(getApplicationContext(),PyramidActivity.class);
                        break;
                    case 15:
                        activityIntent.setClass(getApplicationContext(),ImageTrainActivity.class);
                        break;
                    case 16:
                        activityIntent.setClass(getApplicationContext(),LensActivity.class);
                        break;
                    case 17:
                        activityIntent.setClass(getApplicationContext(), OpenERActivity.class);
                        break;

                    case 18:
                        activityIntent.setClass(getApplicationContext(), SlopeCorrectActivity.class);
                        break;

                    case 19:
                        activityIntent.setClass(getApplicationContext(), CameraActivity.class);
                        break;

                    case 20:
                        activityIntent.setClass(getApplicationContext(), OpencvElegantSampleListActivity.class);
                        break;
                }
                startActivity(activityIntent);
            }
        });
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "模糊与锐化");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "膨胀与腐蚀");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "边缘检测");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "角点检测");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "霍夫变换");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "检测目标");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "特征检测");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "洪水填充");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "身份证识别");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "脸部Haar识别");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "脸部LBP识别");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "Java脸部识别");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "HogSVM行人检测");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "光流法和KLTz追踪器");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "高斯金字塔与拉普拉斯金字塔");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "图片训练");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "文档纠正");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "车辆识别");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "倾斜纠正");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "照相机与人脸识别应用");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "OpencvSample");
        list.add(map);

        return list;
    }
}
