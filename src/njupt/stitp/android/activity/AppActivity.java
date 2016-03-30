package njupt.stitp.android.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.adapter.AppAdapter;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.AppDB;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.model.APP;
import njupt.stitp.android.util.JsonUtil;
import njupt.stitp.android.util.JudgeState;
import njupt.stitp.android.util.MyActivityManager;
import njupt.stitp.android.util.ServerHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AppActivity extends ActionBarActivity {
	private Spinner selectChild;
	private Button nextDay;
	private Button lastDay;
	private TextView selectedDay;
	private ListView appMsgList;
	private List<String> names;
	private String username;
	private Calendar calendar;
	private Calendar nowTime;
	private String tempName;
	private Date tempDate;
	private String path;
	private Handler handler;
	private AppDB appDB;
	private UserDB userDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_appmsg);
		initLayout();
		initDate();
		initList();
		selectChild.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String name = names.get(arg2);
				getApps(name, nowTime.getTime());
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
					getApps(name, calendar.getTime());
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
					String dateString = calendar.get(Calendar.YEAR) + "-"
							+ (calendar.get(Calendar.MONTH) + 1) + "-"
							+ calendar.get(Calendar.DAY_OF_MONTH);
					selectedDay.setText(dateString);
					String name = selectChild.getSelectedItem().toString();
					getApps(name, calendar.getTime());
				} else {
					calendar.add(Calendar.DATE, -1);
				}
			}
		});
	}

	private void initLayout() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(
				new StringBuffer(getString(R.string.app_use_msg)));
		getSupportActionBar().setBackgroundDrawable(
				ContextCompat.getDrawable(this,R.drawable.bg_theme));
		username = getIntent().getExtras().getString("username");
		selectChild = (Spinner) findViewById(R.id.selectChild);
		selectedDay = (TextView) findViewById(R.id.selected_day);
		nextDay = (Button) findViewById(R.id.next_day);
		lastDay = (Button) findViewById(R.id.last_day);
		appMsgList = (ListView) findViewById(R.id.app_msg_list);

		appDB = new AppDB(this);
		userDB = new UserDB(this);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					appMsgList.removeAllViewsInLayout();
					@SuppressWarnings("unchecked")
					List<APP> appList = (List<APP>) msg.obj;
					AppAdapter adapter = new AppAdapter(AppActivity.this,
							R.layout.item_appmsg, appList);
					appMsgList.setAdapter(adapter);
					break;
				case 1:
					appMsgList.setAdapter(null);
					Toast.makeText(AppActivity.this,
							getString(R.string.get_appmsg_fail),
							Toast.LENGTH_SHORT).show();
					break;
				case 2:
					appMsgList.setAdapter(null);
					Toast.makeText(AppActivity.this,
							getString(R.string.none_appmsg), Toast.LENGTH_SHORT)
							.show();
					break;
				case MyApplication.NETWORK_DISCONNECT:
					Toast.makeText(AppActivity.this,
							getString(R.string.network_disconnect),
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		};
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
				AppActivity.this, android.R.layout.simple_spinner_item, names);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectChild.setAdapter(adapter);
		selectChild.setVisibility(View.VISIBLE);
	}

	private void initList() {
		getApps(username, nowTime.getTime());
	}

	private void getApps(String name, Date date) {
		tempDate = date;
		tempName = name;

		List<APP> apps = appDB.getMessage(name, date);
		if (apps.size() == 0) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					if (!JudgeState.isNetworkConnected(getApplicationContext())) {
						Message msg = new Message();
						msg.what = MyApplication.NETWORK_DISCONNECT;
						handler.sendMessage(msg);
						return;
					}
					path = "downloadInfo/appInfo";
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
						List<APP> apps2 = JsonUtil.getApps(result);
						appDB.updateMessage(tempName, apps2);
						Message msg = new Message();
						msg.what = 0;
						msg.obj = apps2;
						handler.sendMessage(msg);
					}
				}
			}).start();
		} else {
			Message msg = new Message();
			msg.what = 0;
			msg.obj = apps;
			handler.sendMessage(msg);
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (NavUtils.getParentActivityName(AppActivity.this) != null) {
				NavUtils.navigateUpFromSameTask(AppActivity.this);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		initSpinner();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		appDB.close();
		userDB.close();
		super.onDestroy();
	}
}
