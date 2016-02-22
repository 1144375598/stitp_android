package njupt.stitp.android.activity;

import java.util.List;

import njupt.stitp.android.R;
import njupt.stitp.android.adapter.UseControlAdapter;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.UseControlDB;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.model.UseTimeControl;
import njupt.stitp.android.util.MyActivityManager;
import njupt.stitp.android.util.ServerHelper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//给孩子设置使用时间控制信息
public class UseControlActivity extends ActionBarActivity {
	private Spinner selectChild;
	private String username;
	private String loginName;
	private List<String> names;
	private ListView controlTimelist;
	private UseControlDB useControlDB;
	private UserDB userDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_setcontroltime);

		getSupportActionBar().setTitle(
				new StringBuffer(getString(R.string.function_lock)));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		useControlDB = new UseControlDB(this);
		userDB = new UserDB(this);
		
		selectChild = (Spinner) findViewById(R.id.selectChild);
		controlTimelist = (ListView) findViewById(R.id.controltimeList);
		username = ((MyApplication) getApplication()).getUsername();
		loginName = username;

		initSpinner();
		setListView(username);
		registerForContextMenu(controlTimelist);
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, 1, 0, "删除");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			if (username.equals(loginName)) {
				Toast.makeText(this, getString(R.string.cannot_modify_self),
						Toast.LENGTH_SHORT).show();
				break;
			}
			AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();
			View view = controlTimelist.getChildAt(menuInfo.position);
			TextView controlmsg = (TextView) view
					.findViewById(R.id.tv_control_time);
			String[] msg = controlmsg.getText().toString().split("-");
			UseTimeControl useTimeControl = new UseTimeControl();
			useTimeControl.setUsername(username);
			useTimeControl.setStart(msg[0]);
			useTimeControl.setEnd(msg[1]);
			useControlDB.delete(useTimeControl);
			setListView(username);
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void initSpinner() {
		names = userDB.getChildNames(username);
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
			if (!username.equals(loginName)) {
				intent = new Intent(this, AddUseControlActivity.class);
				intent.putExtra("username", username);
				startActivityForResult(intent, 1);
			} else {
				Toast.makeText(this, getString(R.string.cannot_modify_self),
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			setListView(username);
			break;
		case RESULT_CANCELED:
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String path = "uploadInfo/useTimeControlInfo";
				ServerHelper serverHelper = new ServerHelper();
				List<String> childName = userDB.getChildNames(loginName);
				for (String name : childName) {
					List<UseTimeControl> list = useControlDB
							.getUseTimeControl(name);
					serverHelper.uploadContolTime(path, list);
				}
				userDB.close();
				useControlDB.close();
			}
		}).start();
		super.onDestroy();
	}
}
