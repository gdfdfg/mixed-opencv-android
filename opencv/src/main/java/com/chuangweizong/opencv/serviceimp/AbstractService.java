package com.chuangweizong.opencv.serviceimp;


import com.chuangweizong.opencv.service.BackService;

public abstract class AbstractService {

	protected BackService backService;
	
	public AbstractService(BackService backService){
		this.backService = backService;
	}

}
