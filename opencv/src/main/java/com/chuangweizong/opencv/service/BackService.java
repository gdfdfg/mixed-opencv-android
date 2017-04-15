package com.chuangweizong.opencv.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackService extends Service {

	public final String ACTION_BACKSERVICE="com.chengqin.app.service.BackService";
	
	private ServiceFacade serviceFacade;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
//		return serviceFacade;
		return  serviceFacade;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		serviceFacade = new ServiceFacade(this);
		serviceFacade.onCreate();
	}
	

	@Override
	public void onDestroy() {
		serviceFacade.onDestroy();
		super.onDestroy();
	}


	@Override
	public boolean onUnbind(Intent intent) {
		serviceFacade.onUnbind();
		return super.onUnbind(intent);
	}
	

}
