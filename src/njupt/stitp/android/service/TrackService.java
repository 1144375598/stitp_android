package njupt.stitp.android.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.db.GeoDB;
import njupt.stitp.android.db.TrackDB;
import njupt.stitp.android.model.GeoFencing;
import njupt.stitp.android.model.Track;
import njupt.stitp.android.util.JsonUtil;
import njupt.stitp.android.util.JudgeState;
import njupt.stitp.android.util.SPHelper;
import njupt.stitp.android.util.ServerHelper;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class TrackService extends Service {
	public static final String UPLOAD_GEO_ACTION = "upload geo";
	public static final String DOWNLOAD_GEO_ACTION = "download geo";
	public static final String OUT_OF_RANGE_ACTION = "out of range";

	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private List<Track> tracks;
	private List<Track> unCommitTracks;
	private String username;
	private ServerHelper serverHelper;
	private String path;
	private TrackDB trackDB;
	private GeoDB geoDB;
	private String lastAddress;
	private boolean outOfRange;
	private LatLng geoCenter;
	private double distance;
	private NotificationManager nm;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		tracks = new ArrayList<Track>();
		outOfRange = false;
		username = new SPHelper().getInfo(getApplicationContext(), "userInfo",
				"username");
		serverHelper = new ServerHelper();
		path = "uploadInfo/trackInfo";
		trackDB = new TrackDB(this);
		geoDB = new GeoDB(this);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		lastAddress = null;

		GeoFencing geoFencing = geoDB.getGeo(username);
		if (geoFencing != null) {
			geoCenter = new LatLng(geoFencing.getLatitude(),
					geoFencing.getLongitude());
			distance = geoFencing.getDistance();
		}
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initLocation();
		mLocationClient.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = null;
		if (intent != null) {
			action = intent.getAction();
		}
		if (TextUtils.equals(action, UPLOAD_GEO_ACTION)) {
			String username = intent.getExtras().getString("username");
			uploadGeo(username);
		} else if (TextUtils.equals(action, DOWNLOAD_GEO_ACTION)) {
			String username = intent.getExtras().getString("username");
			downloadGeo(username);
		} else if (TextUtils.equals(action, OUT_OF_RANGE_ACTION)) {
			String username = intent.getExtras().getString("username");
			Builder builder = new Builder(this);
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setContentTitle("提示");
			builder.setContentText(username + "已超出地理围栏");
			builder.setTicker("超出地理围栏");
			builder.setAutoCancel(true);
			builder.setDefaults(Notification.DEFAULT_SOUND
					| Notification.DEFAULT_VIBRATE);
			Notification notification = builder.getNotification();
			nm.notify(0, notification);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		option.setScanSpan(1000 * 60);// 1min定位一次
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// 离线定位或网络定位成功
			if (location.getLocType() == BDLocation.TypeOffLineLocation
					|| location.getLocType() == BDLocation.TypeNetWorkLocation) {
				if (geoCenter != null) {
					double distance2 = DistanceUtil.getDistance(
							geoCenter,
							new LatLng(location.getLatitude(), location
									.getLongitude()));
					if (distance2 > distance) {
						if (outOfRange == false) {
							outOfRange();
							outOfRange = true;
						}
					} else {
						outOfRange = false;
					}
				}
				if (lastAddress == null
						|| !location.getAddrStr().equals(lastAddress)) {
					Log.i("add track", new Date().toString());
					lastAddress = location.getAddrStr();
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					Track track = new Track();
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					track.setAddTime(format.format(new Date()));
					track.setLatitude(latitude);
					track.setLongitude(longitude);
					track.setAddress(location.getAddrStr());
					track.setUsername(username);
					track.setStayTime(1);
					tracks.add(track);
				} else {
					Log.i("update track", new Date().toString());
					if (tracks.size() > 0) {
						Track temp = tracks.get(tracks.size() - 1);
						temp.setStayTime(temp.getStayTime() + 1);
					}

				}
			}
			if (tracks.size() >= 60
					|| (tracks.size() > 0 && tracks.get(tracks.size() - 1)
							.getStayTime() >= 60)) {
				Log.i("track size", tracks.size() + "");
				lastAddress = null;
				unCommitTracks = trackDB.getUncommitTrack(username);
				Log.i("uncommittrack", unCommitTracks.size() + "");
				new Thread(new Runnable() {

					@Override
					public void run() {
						if (!JudgeState
								.isNetworkConnected(getApplicationContext())) {
							return;
						}
						Boolean flag;
						if (unCommitTracks.size() > 0) {
							flag = serverHelper.uploadTrackAndAPP(path,
									unCommitTracks, null);
							if (flag) {
								trackDB.updateTrack(unCommitTracks);
							}
						}
						flag = serverHelper.uploadTrackAndAPP(path, tracks,
								null);
						if (flag) {
							trackDB.addTracks(tracks, 0);
							tracks.clear();
						} else {
							trackDB.addTracks(tracks, 1);
							tracks.clear();
						}
					}
				}).start();
			}
		}
	}

	private void downloadGeo(String name) {
		final String name2 = name;
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (!JudgeState.isNetworkConnected(getApplicationContext())) {
					return;
				}
				Log.i("download geo", "download geo");
				String path = "downloadInfo/geoFencingInfo";
				Map<String, String> params = new HashMap<String, String>();
				params.put("user.username", name2);
				String result = new ServerHelper().getResult(path, params);
				GeoFencing geoFencing = JsonUtil.getGeo(result);
				if (geoFencing != null) {
					geoDB.saveGeo(name2, geoFencing);
					geoCenter = new LatLng(geoFencing.getLatitude(),
							geoFencing.getLongitude());
					distance = geoFencing.getDistance();
				}
			}
		}).start();
	}

	private void uploadGeo(String name) {
		final String name2 = name;
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (!JudgeState.isNetworkConnected(getApplicationContext())) {
					return;
				}
				String path = "uploadInfo/geoFencingInfo";
				GeoFencing geoFencing = geoDB.getGeo(name2);
				if (geoFencing == null) {
					return;
				}
				Map<String, String> params = new HashMap<String, String>();
				params.put("geoFencing.user.username", name2);
				params.put("geoFencing.longitude", geoFencing.getLongitude()
						+ "");
				params.put("geoFencing.latitude", geoFencing.getLatitude() + "");
				params.put("geoFencing.distance", geoFencing.getDistance() + "");
				params.put("geoFencing.address", geoFencing.getAddress());
				new ServerHelper().getResult(path, params);
				Log.i("upload geo", "upload");
			}
		}).start();
	}

	private void outOfRange() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (!JudgeState.isNetworkConnected(getApplicationContext())) {
					return;
				}
				String path = "user/outOfRange";
				Map<String, String> params = new HashMap<String, String>();
				params.put("user.username", username);
				new ServerHelper().getResult(path, params);
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		if (mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		if (tracks.size() > 0) {
			trackDB.addTracks(tracks, 1);
		}
		if (trackDB != null) {
			trackDB.close();
		}
		if (geoDB != null) {
			geoDB.close();
		}
		super.onDestroy();
	}
}
