package com.chuangweizong.opencv.service;

public interface ConnectListener {

	public void onSuccess(int requestCode, String content) ;

	public void onFailure(int requestCode, String content) ;

}
