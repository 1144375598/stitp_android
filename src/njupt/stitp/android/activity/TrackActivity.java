package njupt.stitp.android.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.TrackDB;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.model.Track;
import njupt.stitp.android.util.JsonUtil;
import njupt.stitp.android.util.JudgeState;
import njupt.stitp.android.util.MyActivityManager;
import njupt.stitp.android.util.ReceiverView;
import njupt.stitp.android.util.ServerHelper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

public class TrackActivity extends ActionBarActivity {
	private Button lastDay;
	private Button nextDay;
	private TextView selectedDay;
	private Spinner selectChild;
	private List<String> names;
	private String username;
	private Calendar calendar;
	private Calendar nowTime;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private String path;
	private Handler handler;
	private String tempName;
	private Date tempDate;
	private String name;

	private UserDB userDB;
	private TrackDB trackDB;

	// 定位相关
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_track);

		initLayout();// 初始化布局与handler
		initDate();
		setLoaction();// 设置当前位置
		selectChild.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				name = names.get(arg2);
				Date date = null;
				try {
					date = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).parse(selectedDay
							.getText().toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (!name.equals(username)) {
					mBaiduMap.setMyLocationEnabled(false);
				} else {
					mBaiduMap.setMyLocationEnabled(true);
				}
				getTracks(name, date);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		lastDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				calendar.add(Calendar.DATE, -1);
				if (Math.abs(calendar.getTimeInMillis()
						- nowTime.getTimeInMillis()) <= 7 * 24 * 60 * 60 * 1000) {
					String dateString = calendar.get(Calendar.YEAR) + "-"
							+ (calendar.get(Calendar.MONTH) + 1) + "-"
							+ calendar.get(Calendar.DAY_OF_MONTH);
					selectedDay.setText(dateString);
					String name = selectChild.getSelectedItem().toString();
					mBaiduMap.setMyLocationEnabled(false);
					getTracks(name, calendar.getTime());
				} else {
					calendar.add(Calendar.DATE, 1);
				}
			}
		});
		nextDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				calendar.add(Calendar.DATE, 1);
				if (calendar.getTimeInMillis() <= nowTime.getTimeInMillis()) {
					String date = calendar.get(Calendar.YEAR) + "-"
							+ (calendar.get(Calendar.MONTH) + 1) + "-"
							+ calendar.get(Calendar.DAY_OF_MONTH);
					selectedDay.setText(date);
					String name = selectChild.getSelectedItem().toString();
					if (calendar.get(Calendar.YEAR) == nowTime
							.get(Calendar.YEAR)
							&& calendar.get(Calendar.MONTH) == nowTime
									.get(Calendar.MONTH)
							&& calendar.get(Calendar.DAY_OF_MONTH) == nowTime
									.get(Calendar.DAY_OF_MONTH)) {
						mBaiduMap.setMyLocationEnabled(true);
					} else {
						mBaiduMap.setMyLocationEnabled(false);
					}
					getTracks(name, calendar.getTime());
				} else {
					calendar.add(Calendar.DATE, -1);
				}
			}
		});

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				Track track = (Track) marker.getExtraInfo().get("track");
				int stayHour = track.getStayTime() / 60;
				int stayMin = track.getStayTime() % 60;
				// 生成一个TextView用户在地图中显示InfoWindow
				TextView location = new TextView(getApplicationContext());
				location.setPadding(30, 20, 30, 50);
				location.setText("停留时间：" + stayHour + "h" + stayMin + "min"
						+ "\n" + track.getAddress());
				location.setBackgroundResource(R.color.RoyalBlue);
				// 定义用于显示该InfoWindow的坐标点
				LatLng pt = new LatLng(track.getLatitude(), track
						.getLongitude());
				// 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
				InfoWindow mInfoWindow = new InfoWindow(location, pt, -47);
				// 显示InfoWindow
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				mBaiduMap.hideInfoWindow();
			}
		});
	}

	private void initDate() {
		calendar = Calendar.getInstance();
		nowTime = Calendar.getInstance();
		String date = nowTime.get(Calendar.YEAR) + "-"
				+ (nowTime.get(Calendar.MONTH) + 1) + "-"
				+ nowTime.get(Calendar.DAY_OF_MONTH);
		selectedDay.setText(date);
	}

	private void initSpinner() {
		names = userDB.getAllUserName(username);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				TrackActivity.this, android.R.layout.simple_spinner_item, names);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectChild.setAdapter(adapter);
		selectChild.setVisibility(View.VISIBLE);
	}

	private void setLoaction() {
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	private void setTrack(List<Track> tracks) {
		ReceiverView receiView = new ReceiverView(TrackActivity.this);
		Track temp = tracks.get(0);
		Marker marker = null;
		LatLng point = null;
		OverlayOptions option = null;
		BitmapDescriptor bitmap = null;
		int id = 1;
		mBaiduMap.clear();
		// 对tracks按时间排序
		Collections.sort(tracks, new Comparator<Track>() {
			@Override
			public int compare(Track arg0, Track arg1) {
				return arg0.getAddTime().compareTo(arg1.getAddTime());
			}
		});
		for (int i = 1; i < tracks.size(); i++) {
			Track track = tracks.get(i);
			if (!track.getAddress().equals(temp.getAddress())) {
				// 定义Maker坐标点
				point = new LatLng(temp.getLatitude(), temp.getLongitude());
				// 构建Marker图标
				bitmap = BitmapDescriptorFactory.fromBitmap(receiView
						.getBitmapFromView(Color.WHITE, 15,
								((Integer) id).toString()));
				// 构建MarkerOption，用于在地图上添加Marker
				option = new MarkerOptions().position(point).icon(bitmap);
				// 在地图上添加Marker，并显示
				marker = (Marker) (mBaiduMap.addOverlay(option));
				Bundle bundle = new Bundle();
				bundle.putSerializable("track", temp);
				marker.setExtraInfo(bundle);
				id++;
				temp = track;
			} else {
				temp.setStayTime(temp.getStayTime() + track.getStayTime());
			}
		}

		point = new LatLng(temp.getLatitude(), temp.getLongitude());
		bitmap = BitmapDescriptorFactory.fromBitmap(receiView
				.getBitmapFromView(Color.WHITE, 15, ((Integer) id).toString()));
		option = new MarkerOptions().position(point).icon(bitmap);
		marker = (Marker) (mBaiduMap.addOverlay(option));
		Bundle bundle = new Bundle();
		bundle.putSerializable("track", temp);
		marker.setExtraInfo(bundle);

		// 将地图移到到最后一个经纬度位置
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
		mBaiduMap.setMapStatus(u);
	}

	private void initLayout() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(
				new StringBuffer(getString(R.string.track)));
		getSupportActionBar().setBackgroundDrawable(
				ContextCompat.getDrawable(this,R.drawable.bg_theme));
		username = getIntent().getExtras().getString("username");
		lastDay = (Button) findViewById(R.id.last_day);
		nextDay = (Button) findViewById(R.id.next_day);
		selectedDay = (TextView) findViewById(R.id.selected_day);
		selectChild = (Spinner) findViewById(R.id.selectChild);

		userDB = new UserDB(this);
		trackDB = new TrackDB(this);

		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					@SuppressWarnings("unchecked")
					List<Track> tracks = (List<Track>) msg.obj;
					trackDB.dropThenAddTracks(tracks, 2);
					setTrack(tracks);
					break;
				case 1:
					Toast.makeText(TrackActivity.this,
							getString(R.string.get_track_fail),
							Toast.LENGTH_LONG).show();
					break;
				case 2:
					Toast.makeText(TrackActivity.this,
							getString(R.string.none_track), Toast.LENGTH_LONG)
							.show();
					break;
				case MyApplication.NETWORK_DISCONNECT:
					Toast.makeText(TrackActivity.this,
							getString(R.string.network_disconnect),
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.geofencing_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (NavUtils.getParentActivityName(TrackActivity.this) != null) {
				NavUtils.navigateUpFromSameTask(TrackActivity.this);
			}
			return true;
		case R.id.item_geo:
			Intent intent = new Intent(this, GeoActivity.class);
			intent.putExtra("username", name);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void getTracks(String name, Date date) {
		tempDate = date;
		tempName = name;
		List<Track> tracks = trackDB.getTracks(name, date);
		Boolean flag = true;
		if (tracks.size() > 0) {
			Collections.sort(tracks, new Comparator<Track>() {
				@Override
				public int compare(Track arg0, Track arg1) {
					return arg0.getAddTime().compareTo(arg1.getAddTime());
				}
			});
			String dateString = tracks.get(tracks.size() - 1).getAddTime();
			Date date2 = null;
			try {
				date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault())
						.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (Math.abs(date.getTime() - date2.getTime()) > 3 * 60 * 60 * 1000) {
				flag = false;
			}
		}

		if (tracks.size() == 0 || flag == false) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (!JudgeState.isNetworkConnected(getApplicationContext())) {
						Message msg = new Message();
						msg.what = MyApplication.NETWORK_DISCONNECT;
						handler.sendMessage(msg);
						return;
					}
					path = "downloadInfo/trackInfo";
					Map<String, String> params = new HashMap<String, String>();
					params.put("user.username", tempName);
					params.put("dateString",
							new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(tempDate));
					String result = new ServerHelper().getResult(path, params);
					int resultCode = JsonUtil.getResultCode(result);
					if (resultCode == 1 || resultCode == 2) {
						Message msg = new Message();
						msg.what = Integer.valueOf(resultCode);
						handler.sendMessage(msg);
					} else {
						List<Track> tracks2 = JsonUtil.getTracks(result);
						Message msg = new Message();
						msg.what = 0;
						msg.obj = tracks2;
						handler.sendMessage(msg);
					}
				}
			}).start();
		} else {
			Message msg = new Message();
			msg.what = 0;
			msg.obj = tracks;
			handler.sendMessage(msg);
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}
	}

	@Override
	protected void onDestroy() {
		userDB.close();
		trackDB.close();
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		initSpinner();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
		super.onPause();
	}

}
