/*
 * Copyright (C) 2013  WhiteCat 白猫 (www.thinkandroid.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chuangweizong.opencv.compoent.netstat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import com.chuangweizong.opencv.log.Logger;

import java.util.ArrayList;

/**
 * @Title NetworkStateReceiver
 * @Package com.ta.util.netstate
 * @Description 是一个检测网络状态改变的，需要配�? <receiver
 *              android:name="com.ta.util.netstate.TANetworkStateReceiver" >
 *              <intent-filter> <action
 *              android:name="android.net.conn.CONNECTIVITY_CHANGE" /> <action
 *              android:name="android.gzcpc.conn.CONNECTIVITY_CHANGE" />
 *              </intent-filter> </receiver>
 * 
 *              �?要开启权�? <uses-permission
 *              android:name="android.permission.CHANGE_NETWORK_STATE" />
 *              <uses-permission
 *              android:name="android.permission.CHANGE_WIFI_STATE" />
 *              <uses-permission
 *              android:name="android.permission.ACCESS_NETWORK_STATE" />
 *              <uses-permission
 *              android:name="android.permission.ACCESS_WIFI_STATE" />
 * @author 白猫
 * @date 2013-5-5 下午 22:47
 * @version V1.2
 */
public class NetworkStateReceiver extends BroadcastReceiver {
	private static Boolean networkAvailable = false;
	private static NetWorkUtil.netType netType;
	private static ArrayList<NetChangeObserver> netChangeObserverArrayList = new ArrayList<NetChangeObserver>();
	private final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public final static String TA_ANDROID_NET_CHANGE_ACTION = "ta.android.net.conn.CONNECTIVITY_CHANGE";
	private static BroadcastReceiver receiver;

	private static BroadcastReceiver getReceiver() {
		if (receiver == null) {
			receiver = new NetworkStateReceiver();
		}
		return receiver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		receiver = NetworkStateReceiver.this;
		if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)
				|| intent.getAction().equalsIgnoreCase(
						TA_ANDROID_NET_CHANGE_ACTION)) {
			Logger.i(NetworkStateReceiver.this, "网络状�?�改�?.");
			if (!NetWorkUtil.isNetworkAvailable(context)) {
				Logger.i(NetworkStateReceiver.this, "没有网络连接.");
				networkAvailable = false;
			} else {
				Logger.i(NetworkStateReceiver.this, "网络连接成功.");
				netType = NetWorkUtil.getAPNType(context);
				// 当网络连接成功后，就发�??1s的延时，通知服务器，客户端已经上线了
				// if (scl == null) {
				// scl = this;
				// MyPushMessageReceiver.setListener(scl);
				// }
				// PushManager.startWork(context,
				// PushConstants.LOGIN_TYPE_API_KEY,
				// Utils.getMetaValue(context, "api_key"));// �?始百度上�?
				networkAvailable = true;
			}
			notifyObserver();
		}
	}

	/**
	 * 注册网络状�?�广�?
	 * 
	 * @param mContext
	 */
	public static void registerNetworkStateReceiver(Context mContext) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(TA_ANDROID_NET_CHANGE_ACTION);
		filter.addAction(ANDROID_NET_CHANGE_ACTION);
		mContext.getApplicationContext()
				.registerReceiver(getReceiver(), filter);
	}

	/**
	 * �?查网络状�?
	 * 
	 * @param mContext
	 */
	public static void checkNetworkState(Context mContext) {
		Intent intent = new Intent();
		intent.setAction(TA_ANDROID_NET_CHANGE_ACTION);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 注销网络状�?�广�?
	 * 
	 * @param mContext
	 */
	public static void unRegisterNetworkStateReceiver(Context mContext) {
		if (receiver != null) {
			try {
				mContext.getApplicationContext().unregisterReceiver(receiver);
			} catch (Exception e) {
				// TODO: handle exception
				Logger.d("TANetworkStateReceiver", e.getMessage());
			}
		}

	}

	/**
	 * 获取当前网络状�?�，true为网络连接成功，否则网络连接失败
	 * 
	 * @return
	 */
	public static Boolean isNetworkAvailable() {
		return networkAvailable;
	}

	public static NetWorkUtil.netType getAPNType() {
		return netType;
	}

	private void notifyObserver() {

		for (int i = 0; i < netChangeObserverArrayList.size(); i++) {
			NetChangeObserver observer = netChangeObserverArrayList.get(i);
			if (observer != null) {
				if (isNetworkAvailable()) {
					observer.onConnect(netType);
				} else {
					observer.onDisConnect();
				}
			}
		}

	}

	/**
	 * 注册网络连接观察�?
	 * 
	 *            observerKey
	 */
	public static void registerObserver(NetChangeObserver observer) {
		if (netChangeObserverArrayList == null) {
			netChangeObserverArrayList = new ArrayList<NetChangeObserver>();
		}
		netChangeObserverArrayList.add(observer);
	}

	/**
	 * 注销网络连接观察�?
	 * 
	 *            observerKey
	 */
	public static void removeRegisterObserver(NetChangeObserver observer) {
		if (netChangeObserverArrayList != null) {
			netChangeObserverArrayList.remove(observer);
		}
	}

}