package com.chuangweizong.opencv.serviceimp;

import android.os.CountDownTimer;

import com.chuangweizong.opencv.service.BackService;

public class CommonServiceImp extends AbstractService{
	
	public CommonServiceImp(BackService backService) {
		super(backService);
		// TODO Auto-generated constructor stub
	}


	public interface CountDownTimerListener{
		public void onFinish(String msg);
		public void onTick(String msg);
	}
	private CodeTimer mResendTimer;
	private CodeTimer  mChangeTimer;
	
	private CountDownTimerListener mTimerListener,mChangeTimerListener;
	
	public void setCountDownTimerListener(CountDownTimerListener listener){
		mTimerListener = listener;
	}
	
	public void setChangePassTimerListener(CountDownTimerListener listener){
		mChangeTimerListener = listener;
	}
	
	
	
	public void startTimer(){
		mResendTimer = new CodeTimer(60000, 1000, mTimerListener);
		mResendTimer.start();
	}
	
	public void startChangeTimer(){
		mChangeTimer = new CodeTimer(60000, 1000, mChangeTimerListener);
		mChangeTimer.start();
	}
	
	
	class CodeTimer extends CountDownTimer {
		private  CountDownTimerListener listener;
		public int IN_RUNNING = 1001;
		public int END_RUNNING = 1002;

		/**
		 * @param millisInFuture
		 *            // 倒计时的时长
		 * @param countDownInterval
		 *            // 间隔时间
		 * @param handler
		 *            // 通知进度的Handler
		 */
		public CodeTimer(long millisInFuture, long countDownInterval,
				CountDownTimerListener listener) {
			super(millisInFuture, countDownInterval);
			this.listener = listener;
		}

		// 结束
		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			if (listener != null)
				listener.onFinish("获取验证码");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			if (listener != null)
				listener.onTick((millisUntilFinished / 1000) + "s 后重发");
		}

	}

}
