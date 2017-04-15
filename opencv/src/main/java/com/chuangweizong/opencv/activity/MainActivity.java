package com.chuangweizong.opencv.activity;

import com.chuangweizhong.opencv.R;
import com.hanvon.HWCloudManager;
import com.hanvon.utils.BitmapUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 示例说明： 1、运行demo前，请到汉王开发者中心注册并申请云身份证服务,在key管理页面创建android_key，并修改替换
 * your_android_key 2、本demo基于汉王云识别，需要在联网的情况下运行 3、云识别的返回结果是json形式，具体请参照开发文档
 */
public class MainActivity extends Activity {

	private Button button1;
	private Button button2;
	private ImageView iv_image;
	private TextView testView;
	private ProgressDialog pd;
	private DiscernHandler discernHandler;

	String picPath = null;
	String result = null;
	private HWCloudManager hwCloudManagerIdcard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_hanwang);

		/**
		 * your_android_key 是您在开发者中心申请的android_key 并 申请了云身份证识别服务
		 * 开发者中心：http://developer.hanvon.com/
		 */
		hwCloudManagerIdcard = new HWCloudManager(this, "d999eb27-3af7-4c69-8962-06b6c9cc1d4e");

		discernHandler = new DiscernHandler();

		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		iv_image = (ImageView) findViewById(R.id.iv_image);
		testView = (TextView) findViewById(R.id.result);

		button1.setOnClickListener(listener);
		button2.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.button1:
				// 激活系统图库，选择一张图片
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, 0);
				break;

			case R.id.button2:
				// 识别
				testView.setText("");
				pd = ProgressDialog
						.show(MainActivity.this, "", "正在识别请稍后......");
				DiscernThread discernThread = new DiscernThread();
				new Thread(discernThread).start();
				break;
			}
		}
	};

	public class DiscernThread implements Runnable {

		@Override
		public void run() {
			try {
				/**
				 * 调用汉王云身份证识别方法
				 */
				result = hwCloudManagerIdcard.idCardLanguage(picPath);//普通版
//				result = hwCloudManagerIdcard.idCardLanguage(picBitmap);
				// result = hwCloudManagerIdcard.idCardLanguage4Https(picPath);
//				result = hwCloudManagerIdcard.idCardCropLanguage(picPath);//切图版
//				result = hwCloudManagerIdcard.idCardCropLanguage(picBitmap);
			} catch (Exception e) {
				// TODO: handle exception
			}
			Bundle mBundle = new Bundle();
			mBundle.putString("responce", result);
			Message msg = new Message();
			msg.setData(mBundle);
			discernHandler.sendMessage(msg);
		}
	}

	public class DiscernHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pd.dismiss();
			Bundle bundle = msg.getData();
			String responce = bundle.getString("responce");
			testView.setText(responce);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Uri uri = data.getData();
			// 通过uri获取图片路径
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(uri, proj, null, null,
					null);
			if(cursor!=null) {           	
            	int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            	cursor.moveToFirst();
            	picPath = cursor.getString(column_index);
            }
            else
            {
            	picPath = data.getData().getPath();
            }
			System.out.println(picPath);

			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(picPath, options);
			options.inSampleSize = BitmapUtil.calculateInSampleSize(options,
					1280, 720);
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);
			iv_image.setImageBitmap(bitmap);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
