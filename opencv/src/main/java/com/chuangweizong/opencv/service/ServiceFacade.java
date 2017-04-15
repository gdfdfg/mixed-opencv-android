package com.chuangweizong.opencv.service;


public class ServiceFacade extends IServiceFacade.Stub {


	private BackService backService;

	public ServiceFacade(BackService backService) {
		this.backService = backService;
		initServices();
	}

	/**
	 * 初始化各个service
	 */
	private void initServices() {
		// locationService = new LocationServiceImp(backService);
		// groupService = new GroupServiceImp(backService);
	}
	
	
	

	public void onCreate() {

	}

	public void onDestroy() {

	}

	public void onUnbind() {

	}

}
