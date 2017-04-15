/**
 * Copyright (C) 2013-2014 yunzhong Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chuangweizong.opencv;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;


import com.chuangweizong.opencv.compoent.imagecache.ImageMemoryCache;
import com.chuangweizong.opencv.compoent.netstat.NetworkStateReceiver;
import com.chuangweizong.opencv.compoent.universalimg.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyApplication extends Application {

	private static final Intent SERVICE_INTENT = new Intent();
	public static List<Bitmap> serviceBitmap = new ArrayList<Bitmap>();
	public static Bitmap headBitmap;
	
	
	static {
		SERVICE_INTENT.setComponent(new ComponentName("com.chengqin.app",
				"com.chengqin.app.service.BackService"));
	}
	public static Context applicationContext;
	private static MyApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";

	public static ImageMemoryCache imageMemoryCache;
	
	/**
	 * 当前用户nickname,为了苹果推�?�不是userid而是昵称
	 */
	public static String currentUserNick = "";

	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		super.onCreate();
		if (Constants.Config.DEVELOPER_MODE
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyDeath().build());
		}

		super.onCreate();
		applicationContext = this;
		instance = this;
		initImageLoader(getApplicationContext());
		imageMemoryCache = new ImageMemoryCache(applicationContext);
		// 初始化软件的网络
		NetworkStateReceiver.checkNetworkState(this);
		Thread.getDefaultUncaughtExceptionHandler();
		startBackService();
		//
	}

	private void startBackService() {
		startService(SERVICE_INTENT);
	}
	
	public Intent getService(){
		return SERVICE_INTENT;
	}

	public static MyApplication getInstance() {
		return instance;
	}

	public void setSharedPreferenceValue(String key, String value) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value).commit();
	}
	
	public void setSharedPreferenceValue(String key, boolean value) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, value).commit();
	}

	public String getSharedPreferenceValue(String key) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		return preferences.getString(key, "");
	}
	public boolean getBooleanValue(String key) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		return preferences.getBoolean(key, false);
	}

	public void setInt(String key, int value) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value).commit();
	}
	
	public int getInt(String key){
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		return preferences.getInt(key, 0);
	}
	

	/**
	 * 获取当前登陆用户�?
	 *
	 * @return
	 */
	public String getUserName() {
	    return "";
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return "";
	}

	/**
	 * 设置用户�?
	 *
	 * @param username
	 */
	public void setUserName(String username) {
	}

	/**
	 * 设置密码 下面的实例代�? 只是demo，实际的应用中需要加password 加密后存�? preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
	}

//	/**
//	 * �?出登�?,清空数据
//	 */
//	public void logout(final boolean isGCM,final EMCallBack emCallBack) {
//		// 先调用sdk logout，在清理app中自己的数据
//	}
	
	@SuppressWarnings({ "unused", "rawtypes" })
	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
					.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm
							.getApplicationInfo(info.processName,
									PackageManager.GET_META_DATA));
					// Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
					// info.processName +"  Label: "+c.toString());
					// processName = c.toString();
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}

	/*****************************************************************/
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	

}
