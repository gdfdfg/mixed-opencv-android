package com.daiyinger.carplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangweizhong.opencv.R;

public class OpenERActivity extends Activity {
	
    private int window_width, window_height;// �ؼ����
	private int state_height;// ״̬���ĸ߶�
    private DragImageView  mZoomView = null;
    private ViewTreeObserver viewTreeObserver;
	private Bitmap bmp = null; 
	private Button btnTrain = null;
	private Button btnPic = null;
	private TextView m_text = null;
	private String path = null; //SDCARD ��Ŀ¼
	String imgpath = null;
	boolean selected_img_flag = false;

	private String TAG = "opencv";

	@SuppressWarnings({ "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opener);
		m_text = (TextView) findViewById(R.id.myshow);
		btnTrain = (Button) findViewById(R.id.btn_plate);
		btnPic = (Button) findViewById(R.id.btn_pick);
		
		mZoomView = (DragImageView)findViewById(R.id.imageview);  
		
		btnTrain.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 if(selected_img_flag == false)
				 {
					 File file=new File(imgpath);
					 if(!file.exists())
					 {
						 SendMsgText("δѡ��ͼƬ ��Ĭ��·�� "+imgpath+"ͼƬ������!", 2);
						 return;
					 }
					 bmp = getLoacalBitmap(imgpath);
					 mZoomView.setImageBitmap(bmp);
					 selected_img_flag = true;
				 }
				 new MyTask().execute();
			}});
		    btnPic.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
				Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				  
				startActivityForResult(i, 1);
			}});
	    //����������ͼ����س����в�������ʾ
		 bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ai);
		 mZoomView.setImageBitmap(bmp);
		 /** ��ȡ��Ҋ����߶� **/
		 WindowManager manager = getWindowManager();
		 window_width = manager.getDefaultDisplay().getWidth();
		 window_height = manager.getDefaultDisplay().getHeight();
		 mZoomView.setmActivity(this);//ע��Activity.
		 /** ����״̬���߶� **/
		 viewTreeObserver = mZoomView.getViewTreeObserver();
		 viewTreeObserver
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (state_height == 0) {
							// ��ȡ״�����߶�
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);
							state_height = frame.top;
							mZoomView.setScreen_H(window_height);
							mZoomView.setScreen_W(window_width);
						}

					}
				});
	     path = Environment.getExternalStorageDirectory().getAbsolutePath();//��ȡ��Ŀ¼ 
	     imgpath = path+"/ai/plate_locate.jpg";
	     System.out.println(path);
	     InitEnv();
	}
	
	//��ѯxml��Դ�Ƿ���� ������������assets���п���
	@SuppressLint("ShowToast")
	void InitEnv()
	{
		 try {
			 String lastVersion = null;
			 SharedPreferences sharedPreferences;
	         sharedPreferences = getSharedPreferences("info",Activity.MODE_PRIVATE); 
	         if(sharedPreferences.contains("version") == true)
	         {    	
	        	lastVersion = sharedPreferences.getString("version","0.0");
	         }
			 String curVersion = PlaneUtil.getVersion(getApplicationContext());
			 if(!curVersion.equals(lastVersion))
			 {
				 String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
				 File dir = new File(sdpath + "/ai");
				 if(!dir.isDirectory())
				 {
					 dir.mkdir();
				 }
				 PlaneUtil.copyBigDataToSD(getApplicationContext(),
					 "ann.xml",sdpath + "/ai/ann.xml");
				 PlaneUtil.copyBigDataToSD(getApplicationContext(),
						 "svm.xml",sdpath + "/ai/svm.xml");
				 PlaneUtil.copyBigDataToSD(getApplicationContext(),
						 "plate_locate.jpg",sdpath + "/ai/plate_locate.jpg");
				 dir = new File(sdpath + "/ai/etc/");
				 if(!dir.isDirectory())
				 {
					 dir.mkdir();
				 }
				 PlaneUtil.copyBigDataToSD(getApplicationContext(),
						 "province_mapping",sdpath + "/ai/etc/province_mapping");
				 
				 SharedPreferences.Editor editor = sharedPreferences.edit(); 
		     	 //��putString�ķ����������� 
		     	 editor.putString("version",curVersion);
		     	 editor.commit(); 
			 }
		 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }
	}
	
	
	 /**
	    * ���ر���ͼƬ
	    * @param url
	    * @return
	    */
    public static Bitmap getLoacalBitmap(String url) {
         try {
              FileInputStream fis = new FileInputStream(url);
              return BitmapFactory.decodeStream(fis);  ///����ת��ΪBitmapͼƬ        

           } catch (FileNotFoundException e) {
              e.printStackTrace();
              return null;
         }
    }

	//OpenCV�����ز���ʼ���ɹ���Ļص��������ڴ����ǲ������κβ���  
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {  
       @Override  
       public void onManagerConnected(int status) {  
           switch (status) {  
               case LoaderCallbackInterface.SUCCESS:{  
                   System.loadLibrary("myOpenCV");
               } break;  
               default:{  
                   super.onManagerConnected(status);  
               } break;  
           }  
       }  
   };  
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	 
	    if (resultCode == RESULT_OK && null != data) {
	    	if(requestCode == 1)
	    	{
		    	Uri uri = data.getData();
		    	ContentResolver cr = this.getContentResolver();  
	            try {  
	            	bmp = BitmapFactory.decodeStream(cr.openInputStream(uri));  
	            	mZoomView.setImageBitmap(bmp);
	                imgpath = getRealFilePath(getApplicationContext(), uri);
	            } catch (FileNotFoundException e) {  
	                	//Log.e("Exception", e.getMessage(),e);  
	            }  
	    	}
	    	else if(requestCode == 2)
	    	{
	    	
	        }  
	    }
   	}

   
   /**
    * Try to return the absolute file path from the given Uri
    *
    * @param context
    * @param uri
    * @return the file path or null
    */
   public static String getRealFilePath( final Context context, final Uri uri ) {
       if ( null == uri ) return null;
       final String scheme = uri.getScheme();
       String data = null;
       if ( scheme == null )
           data = uri.getPath();
       else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
           data = uri.getPath();
       } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
           Cursor cursor = context.getContentResolver().query( uri, new String[] { ImageColumns.DATA }, null, null, null );
           if ( null != cursor ) {
               if ( cursor.moveToFirst() ) {
                   int index = cursor.getColumnIndex( ImageColumns.DATA );
                   if ( index > -1 ) {
                       data = cursor.getString( index );
                   }
               }
               cursor.close();
           }
       }
       return data;
   }
   
   private class MyTask extends AsyncTask<String, Integer, String> {  
       //onPreExecute����������ִ�к�̨����ǰ��һЩUI����  
       @Override  
       protected void onPreExecute() {  
       }  
         
       //doInBackground�����ڲ�ִ�к�̨����,�����ڴ˷������޸�UI  
       @Override  
       protected String doInBackground(String... params) {
    	   try {  
    		    String resultImgDirPath = path +"/ai/tmp/";
    		   	String logpath = path+"/ai/ai_log.log";
				String svmpath = path+"/ai/svm.xml";
				String annpath = path+"/ai/ann.xml";
				String imagepath =  new String(imgpath.getBytes(),"gbk");
			    System.out.println("entering the jni");
			    SendMsgText("����ʶ��.....",1);
			    Thread.sleep(100);
			    String result = null;
			    
			    byte[] resultByte =CarPlateDetection.ImageProc(path, logpath, imagepath, svmpath, annpath);
			    System.out.println(result);
			    if(resultByte != null)
			    {
			    	bmp = BitmapFactory.decodeFile(resultImgDirPath+"result.jpg");
					SendMsgRefresh(3);
					result = new String(resultByte,"UTF-8");
					SendMsgText(result,1);
					SendMsgText(result,2);
			    }
			    else
			    {
			    	SendMsgText("ʶ��ʧ��!",1);
			    }
          } 
    	  catch (Exception e) {  
    		  SendMsgText("entering the detect error",2);
          }
    	   return null;
       }  
   }
   public Handler mHandler=new Handler()  
   {  
       public void handleMessage(Message msg)  
       {  
           switch(msg.what)  
           {  
	            case 1: 
	            	m_text.setText((String)msg.obj);
	                break; 
	            case 2: 
	            	Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
	                break;
	            case 3:
	            	mZoomView.setImageBitmap(bmp);
	            	break;
	            default: 
	                break;            
           }    
           super.handleMessage(msg);  
       }  
   }; 
  
   public void SendMsgText(String str, int id)
   {
   		Message message=new Message(); 
   		message.what=id; 
   		message.obj = str;
   		mHandler.sendMessage(message);
   }
   
   public void SendMsgRefresh(int id)
   {
   		Message message=new Message(); 
   		message.what=id; 
   		message.obj = null;
   		mHandler.sendMessage(message);
   }
   
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!OpenCVLoader.initDebug()) {
			Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
		} else {
			Log.d(TAG, "OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}
	}
	
  	@Override
  	public boolean onOptionsItemSelected(MenuItem item) {
  		File root;
	    Uri uri;
	    Intent intent;
	  	switch (item.getItemId()) {
	
	  		case R.id.action_settings:
	  			//intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
                //startActivityForResult(intent, 2); 
	  			break;
	  		case R.id.action_view_image:
	  			root = new File(Environment.getExternalStorageDirectory().getPath()
	  					+ "/ai/tmp/result.jpg");
			    uri = Uri.fromFile(root);
			    intent = new Intent();
			    intent.setAction(Intent.ACTION_VIEW);
			    intent.setDataAndType(uri , "image/*"); 
			    startActivity(intent);
	  			break;
	  		case R.id.action_about:
	  			Toast.makeText(getApplicationContext(),"daiyinger", Toast.LENGTH_SHORT).show();
	  			break;
	  		default:
	  			break;
	  	}
	  	return super.onOptionsItemSelected(item);
  	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_opener, menu);
		return true;
	}
	
}
