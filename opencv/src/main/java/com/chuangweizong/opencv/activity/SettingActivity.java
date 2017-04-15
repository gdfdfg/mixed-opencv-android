package com.chuangweizong.opencv.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.widget.Button;

import com.chuangweizhong.opencv.R;
import com.chuangweizong.opencv.activity.opencvsample.OpencvElegantSampleListActivity;

/**
 * Created by ywjiang on 11/18/15.
 */
public class SettingActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ((Button)findViewById(R.id.bt_exit)).setText(getResources().getString(R.string.exit).toUpperCase());
        ((Button)findViewById(R.id.settings)).setText(getResources().getString(R.string.settings).toUpperCase());
    }
    public void exit(View v){
        finish();
    }
    public void goSetting(View v){
        ForwardUtil.gotoSettings(SettingActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PermissionChecker.checkSelfPermission(SettingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(SettingActivity.this, OpencvElegantSampleListActivity.class));
            finish();
        }
    }
}
