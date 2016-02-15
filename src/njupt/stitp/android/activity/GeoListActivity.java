package njupt.stitp.android.activity;

import java.util.List;

import njupt.stitp.android.R;
import njupt.stitp.android.adapter.GeoAdapter;
import njupt.stitp.android.db.GeoDB;
import njupt.stitp.android.model.GeoFencing;
import njupt.stitp.android.util.MyActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class GeoListActivity extends ActionBarActivity {
	private ListView geoListView;
	private Button addGeo;
	private List<GeoFencing> geos;
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		username=getIntent().getExtras().getString("username");
		geoListView = (ListView) findViewById(R.id.geolist);
		addGeo = (Button) findViewById(R.id.add_geo);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(
				new StringBuffer(getString(R.string.geo_list)));
		
		geos=new GeoDB(this).getGeos(username);
		GeoAdapter adapter=new GeoAdapter(this, R.layout.item_geo, geos);
		geoListView.setAdapter(adapter);
		addGeo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GeoListActivity.this,
						AddGeoActivity.class);
				startActivityForResult(intent, 1);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			
			break;

		default:
			break;
		}
	}
}
