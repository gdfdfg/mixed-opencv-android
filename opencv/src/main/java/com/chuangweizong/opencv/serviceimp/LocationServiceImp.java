package com.chuangweizong.opencv.serviceimp;//package com.chengqin.app.serviceimp;
//
//import android.hardware.GeomagneticField;
//import android.util.Log;
//
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.LocationClient;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.utils.CoordinateConverter;
//import com.yunzhong.travelsafe.Constant;
//import com.yunzhong.travelsafe.MyApplication;
//import com.yunzhong.travelsafe.http.RequestParams;
//import com.yunzhong.travelsafe.http.RestClient;
//import com.yunzhong.travelsafe.service.BackService;
//import com.yunzhong.travelsafe.service.ILocationService;
//
//public class LocationServiceImp extends AbstractService implements ILocationService,BDLocationListener{
//
//	private LocationClient mLocClient;
//	
//	private BDLocation lastLocation = new BDLocation();
//	
//	private boolean isStartLocationService;
//	
//	private BDLocationListener locationListener;
//	
//	private String userId;
//	
//	public LocationServiceImp(BackService backService) {
//		super(backService);
//		mLocClient = new LocationClient(backService);
//		mLocClient.registerLocationListener(this);
//	}
//
//	@Override
//	public BDLocation getCurrentLocation() {
//		return lastLocation;
//	}
//	
//	public GeomagneticField getGeomagneticField() {
//		GeomagneticField gmf = new GeomagneticField(
//				(float) lastLocation.getLatitude(),
//				(float) lastLocation.getLongitude(),
//				(float) lastLocation.getAltitude(), System.currentTimeMillis());
//		return gmf;
//	}
//
//	
//
//	/**
//	 * 百度接口
//	 */
//	@Override
//	public void onReceiveLocation(BDLocation location) {
//
//		if (location == null) {
//			return;
//		}
//		Log.d("map", "On location change received:" + location);
//
//		if (lastLocation != null) {
//			if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
//				Log.d("map", "same location, skip refresh");
//				// mMapView.refresh(); //need this refresh?
//				return;
//			}
//		}
//		lastLocation = location;
//		
//		if(locationListener!=null){
//			locationListener.onReceiveLocation(location);
//		}
//		
//		LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//		CoordinateConverter converter= new CoordinateConverter();
//		converter.coord(llA);
//		converter.from(CoordinateConverter.CoordType.COMMON);
//		LatLng convertLatLng = converter.convert();
//
//		//上传自己的位置
//		if(userId!=null&&!userId.equals("")){
//			RequestParams params = new RequestParams();
//			params.put("userId", MyApplication.getInstance().getUserName());
//			params.put("longitude",String.valueOf(lastLocation.getLongitude()));
//			params.put("latitude", String.valueOf(lastLocation.getLatitude()));
//			params.put("altitude", String.valueOf(lastLocation.getAltitude()));
//			RestClient.post(Constant.URL.URL_USER_SERVICE+Constant.URL.URL_USER_METHOD_UPDATELOCATION, params, null);
//		}
//	}
//
//	@Override
//	public void onReceivePoi(BDLocation poiLocation) {
//		if (poiLocation == null) {
//			return;
//		}
//		
//		if(locationListener!=null){
//			locationListener.onReceivePoi(poiLocation);
//		}
//		
//	}
//	
//	@Override
//	public void start(String userId) {
//		if (mLocClient != null && !mLocClient.isStarted())
//			{
//				this.userId = userId;
//				mLocClient.start();
//				isStartLocationService = true;
//			}
//	}
//
//	@Override
//	public void stop() {
//		if (mLocClient != null && mLocClient.isStarted())
//			mLocClient.stop();
//		isStartLocationService = false;
//	}
//
//	@Override
//	public boolean getIsStartLocationService() {
//		// TODO Auto-generated method stub
//		return isStartLocationService;
//	}
//	
//	public void setLocationListener(BDLocationListener locationListener) {
//		this.locationListener = locationListener;
//	}
//	
//}
