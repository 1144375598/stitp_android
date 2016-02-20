package njupt.stitp.android.activity;

import java.util.List;

import njupt.stitp.android.R;
import njupt.stitp.android.adapter.UseControlAdapter;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.UseControlDB;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.model.UseTimeControl;
import njupt.stitp.android.util.MyActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

//给孩子设置使用时间控制信息
public class UseControlActivity extends ActionBarActivity {
	private Spinner selectChild;
	private String username;
	private List<String> names;
	private ListView controlTimelist;
	private UseControlDB useControlDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_setcontroltime);

		getSupportActionBar().setTitle(
				new StringBuffer(getString(R.string.function_lock)));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		useControlDB = new UseControlDB(getApplicationContext());
		selectChild = (Spinner) findViewById(R.id.selectChild);
		controlTimelist = (ListView) findViewById(R.id.controltimeList);
		username = ((MyApplication) getApplication()).getUsername();

		initSpinner();
		setListView(username);

		selectChild.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				username = names.get(position);
				setListView(username);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void initSpinner() {
		names = new UserDB(getApplicationContext()).getChildNames(username);
		names.add(username);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				UseControlActivity.this, android.R.layout.simple_spinner_item,
				names);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectChild.setAdapter(adapter);
		selectChild.setVisibility(View.VISIBLE);
	}

	private void setListView(String name) {
		List<UseTimeControl> list = useControlDB.getUseTimeControl(name);
		controlTimelist.removeAllViewsInLayout();
		UseControlAdapter adapter = new UseControlAdapter(this,
				R.layout.item_controltime, list);
		controlTimelist.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.controltime_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_item_add_controlTime:
			intent = new Intent(this, AddUseControlActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		useControlDB.close();
		super.onDestroy();
	}
}
