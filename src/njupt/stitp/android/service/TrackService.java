package njupt.stitp.android.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import njupt.stitp.android.db.TrackDB;
import njupt.stitp.android.model.Track;
import njupt.stitp.android.util.SPHelper;
import njupt.stitp.android.util.ServerHelper;
import android.app.Service;
import android.content.Intent;
import android.graphics.Point;
import android.os.IBinder;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class TrackService extends Service {
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private List<Track> tracks;
	private List<Track> unCommitTracks;
	private String username;
	private ServerHelper serverHelper;
	private String path;
	private TrackDB trackDB;
	private LatLng point;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		tracks = new ArrayList<Track>();
		username = new SPHelper().getInfo(getApplicationContext(), "userInfo",
				"username");
		serverHelper = new ServerHelper();
		path = "uploadInfo/trackInfo";
		trackDB = new TrackDB(getApplicationContext());
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initLocation();
		point = new LatLng(0, 0);
		mLocationClient.start();
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 5 * 60 * 1000; // 5分钟
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(false);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// 离线定位或网络定位成功
			if (location.getLocType() == BDLocation.TypeOffLineLocation
					|| location.getLocType() == BDLocation.TypeNetWorkLocation) {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				LatLng newPoint = new LatLng(latitude, longitude);
				if (DistanceUtil.getDistance(point, newPoint) > 3000) {
					point=newPoint;
					Track track = new Track();
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					track.setAddTime(format.format(new Date()));
					track.setLatitude(latitude);
					track.setLongitude(longitude);
					track.setAddress(location.getAddrStr());
					track.setUsername(username);
					tracks.add(track);
				}				
			}
			if (tracks.size() >= 72) {
				unCommitTracks = trackDB.getUncommitTrack(username);
				if (unCommitTracks.size() > 0) {
					if (serverHelper.uploadTrackAndAPP(path, unCommitTracks,
							null)) {
						trackDB.updateTrack(unCommitTracks);
					}
				}
				if (serverHelper.uploadTrackAndAPP(path, tracks, null) == true) {
					trackDB.addTracks(tracks, 0);
					tracks.clear();
				} else {
					trackDB.addTracks(tracks, 1);
					tracks.clear();
				}
			}
		}

	}

}
