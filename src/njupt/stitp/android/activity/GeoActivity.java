package njupt.stitp.android.activity;

import njupt.stitp.android.R;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.GeoDB;
import njupt.stitp.android.model.GeoFencing;
import njupt.stitp.android.service.TrackService;
import njupt.stitp.android.util.MyActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class GeoActivity extends ActionBarActivity implements
		OnGetGeoCoderResultListener {
	private EditText city;
	private EditText address;
	private EditText geoRange;
	private Button confirm;
	private GeoDB geoDB;
	private String username;
	private String loginName;
	private ProgressDialog p;
	private boolean isChanged;

	private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private BaiduMap mBaiduMap = null;
	private MapView mMapView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_geofencing);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(
				new StringBuffer(getString(R.string.geofencing)));
		getSupportActionBar().setBackgroundDrawable(
				ContextCompat.getDrawable(this,R.drawable.bg_theme));
		geoDB = new GeoDB(this);
		isChanged = false;
		username = getIntent().getExtras().getString("username");
		loginName = ((MyApplication) getApplication()).getUsername();
		city = (EditText) findViewById(R.id.geo_center_city);
		address = (EditText) findViewById(R.id.geo_center_address);
		geoRange = (EditText) findViewById(R.id.geo_range);
		confirm = (Button) findViewById(R.id.save);

		p = new ProgressDialog(GeoActivity.this);
		p.setTitle(getString(R.string.wait));

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.geo_bmapView);
		mBaiduMap = mMapView.getMap();

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		initGeo();
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.equals(loginName, username)) {
					Toast.makeText(GeoActivity.this, "无法修改自己的围栏信息",
							Toast.LENGTH_SHORT).show();
					return;
				}
				isChanged = true;
				mSearch.geocode(new GeoCodeOption().city(
						city.getText().toString()).address(
						address.getText().toString()));
				p.show();
				Log.i("GeoActivity", "confirm");
			}
		});
	}

	private void initGeo() {
		GeoFencing geoFencing = geoDB.getGeo(username);
		if (geoFencing != null) {
			String[] tempGeoCenter = geoFencing.getAddress().split(",");
			city.setText(tempGeoCenter[0]);
			address.setText(tempGeoCenter[1]);
			geoRange.setText((int) geoFencing.getDistance() + "");
			mBaiduMap.clear();
			LatLng location = new LatLng(geoFencing.getLongitude(),
					geoFencing.getLatitude());
			mBaiduMap.addOverlay(new MarkerOptions().position(location).icon(
					BitmapDescriptorFactory.fromResource(R.drawable.ic_track)));
			CircleOptions circleOptions = new CircleOptions();
			circleOptions.center(location);
			circleOptions.radius((int) geoFencing.getDistance());
			circleOptions.fillColor(Color.argb(100, 255, 175, 175));
			mBaiduMap.addOverlay(circleOptions);
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		Log.i("geoactivity pause", "pause");
		if (isChanged == true) {
			Intent intent = new Intent(this, TrackService.class);
			intent.setAction(TrackService.UPLOAD_GEO_ACTION);
			intent.putExtra("username", username);
			startService(intent);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		mSearch.destroy();
		geoDB.close();
		super.onDestroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		p.dismiss();
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
			isChanged = false;
			return;
		}

		double longitude = result.getLocation().longitude;
		double latitude = result.getLocation().latitude;
		double distance = 0;
		try {
			distance = Double.valueOf(geoRange.getText().toString());
		} catch (NumberFormatException e) {
			Toast.makeText(GeoActivity.this, "距离设置错误", Toast.LENGTH_SHORT)
					.show();
			isChanged = false;
			return;
		}
		mBaiduMap.clear();
		mBaiduMap
				.addOverlay(new MarkerOptions().position(result.getLocation())
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_track)));
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(result.getLocation());
		circleOptions.radius((int) distance);
		circleOptions.fillColor(Color.argb(100, 255, 175, 175));
		mBaiduMap.addOverlay(circleOptions);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));

		if (isChanged == true) {
			String geoCenter = city.getText().toString() + ","
					+ address.getText().toString();
			GeoFencing geoFencing = new GeoFencing();
			geoFencing.setUsername(username);
			geoFencing.setDistance(distance);
			geoFencing.setAddress(geoCenter);
			geoFencing.setLongitude(longitude);
			geoFencing.setLatitude(latitude);
			geoDB.saveGeo(username, geoFencing);
			Toast.makeText(GeoActivity.this, "已保存", Toast.LENGTH_SHORT).show();
			;
		}
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap
				.addOverlay(new MarkerOptions().position(result.getLocation())
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_track)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		Toast.makeText(this, result.getAddress(), Toast.LENGTH_LONG).show();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (NavUtils.getParentActivityName(GeoActivity.this) != null) {
				NavUtils.navigateUpFromSameTask(GeoActivity.this);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
