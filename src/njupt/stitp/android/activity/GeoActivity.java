package njupt.stitp.android.activity;

import njupt.stitp.android.R;
import njupt.stitp.android.db.GeoDB;
import njupt.stitp.android.model.GeoFencing;
import njupt.stitp.android.service.TrackService;
import njupt.stitp.android.util.MyActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
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
import com.baidu.mapapi.map.Stroke;
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
		geoDB = new GeoDB(this);
		isChanged = false;
		username = getIntent().getExtras().getString("username");
		city = (EditText) findViewById(R.id.geo_center_city);
		address = (EditText) findViewById(R.id.geo_center_address);
		geoRange = (EditText) findViewById(R.id.geo_range);
		confirm = (Button) findViewById(R.id.save);

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
				isChanged = true;
				mSearch.geocode(new GeoCodeOption().city(
						city.getText().toString()).address(
						address.getText().toString()));
			}
		});
	}

	private void initGeo() {
		GeoFencing geoFencing = geoDB.getGeo(username);
		if (geoFencing != null) {
			String[] tempGeoCenter = geoFencing.getAddress().split(",");
			city.setText(tempGeoCenter[0]);
			address.setText(tempGeoCenter[1]);
			geoRange.setText(geoFencing.getDistance() + "");
			mSearch.geocode(new GeoCodeOption().city(tempGeoCenter[0]).address(
					tempGeoCenter[1]));
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
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
		if (isChanged == true) {
			Intent intent = new Intent(this, TrackService.class);
			intent.setAction(TrackService.UPLOAD_GEO_ACTION);
			startService(intent);
		}
		super.onDestroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
			isChanged = false;
			initGeo();
			return;
		}
		mBaiduMap.clear();
		double longitude = result.getLocation().longitude;
		double latitude = result.getLocation().latitude;
		int distance = Integer.valueOf(geoRange.getText().toString());
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(result.getLocation());
		circleOptions.radius(distance);
		circleOptions.stroke(new Stroke(5, 0xAA00FF00));// 设置边框
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
