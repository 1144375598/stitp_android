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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class TrackService extends Service {
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private List<Track> tracks;
	private List<Track> unCommitTracks;
	private String username;
	private ServerHelper serverHelper;
	private String path;
	private TrackDB trackDB;
	private Handler handler;
	private String lastAddress;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//Log.i("info", "track Service OnCreate");
		tracks = new ArrayList<Track>();
		username = new SPHelper().getInfo(getApplicationContext(), "userInfo",
				"username");
		serverHelper = new ServerHelper();
		path = "uploadInfo/trackInfo";
		trackDB = new TrackDB(getApplicationContext());
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					trackDB.updateTrack(unCommitTracks);
					break;
				case 1:
					trackDB.addTracks(tracks, 0);
					tracks.clear();
					break;
				case 2:
					trackDB.addTracks(tracks, 1);
					tracks.clear();
					break;
				}
			}
		};
		lastAddress = null;
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initLocation();
		mLocationClient.start();
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		option.setScanSpan(1000*60);// 1min定位一次
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Log.i("address", location.getAddrStr());
			// 离线定位或网络定位成功
			if (location.getLocType() == BDLocation.TypeOffLineLocation
					|| location.getLocType() == BDLocation.TypeNetWorkLocation) {
				if (lastAddress==null||!location.getAddrStr().equals(lastAddress)) {
					lastAddress=location.getAddrStr();
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
				}else{
					Track temp=tracks.get(tracks.size()-1);
					temp.setStayTime(temp.getStayTime()+1);
				}
			}
			if (tracks.size() >= 60||tracks.get(tracks.size()-1).getStayTime()>=60) {
				//Log.i("2", "asadad");
				lastAddress=null;
				unCommitTracks = trackDB.getUncommitTrack(username);
				new Thread(new Runnable() {

					@Override
					public void run() {
						Boolean flag;
						if (unCommitTracks.size() > 0) {
							flag = serverHelper.uploadTrackAndAPP(path,
									unCommitTracks, null);
							if (flag) {
								//Log.i("3", "asadad");
								Message msg = new Message();
								msg.what = 0;
								handler.sendMessage(msg);
							}
						}
						flag = serverHelper.uploadTrackAndAPP(path, tracks,
								null);
						if (flag) {
							//Log.i("4", "asadad");
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);
						} else {
							//Log.i("5", "asadad");
							Message msg = new Message();
							msg.what = 2;
							handler.sendMessage(msg);
						}
					}
				}).start();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		if(tracks.size()>0){
			trackDB.addTracks(tracks, 1);
		}
		if(trackDB!=null){
			trackDB.close();
		}
		super.onDestroy();
	}
}
