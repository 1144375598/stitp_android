package njupt.stitp.android.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import njupt.stitp.android.R;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.model.User;
import njupt.stitp.android.util.JsonUtil;
import njupt.stitp.android.util.ServerHelper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;

public class FunctionActivity extends ActionBarActivity {
	private Spinner selectChild;
	private Chronometer useTime;
	private Button track;
	private Button appInfo;
	private Button useControl;
	private Button chat;
	private String path;
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_function);
		init();
		// 获取孩子和自己信息
		new Thread(new Runnable() {
			@Override
			public void run() {
				path = "user/getChild";
				Map<String, String> params = new HashMap<String, String>();
				params.put("user.username", username);
				String result = new ServerHelper().getResult(path, params);
				List<User> childs = JsonUtil.getChild(result);
				if (childs != null) {
					new UserDB(getApplicationContext()).addUsers(childs);
				}
				path = "user/getUser";
				result = new ServerHelper().getResult(path, params);
				User user = JsonUtil.getUser(result);
				if (user != null) {
					new UserDB(getApplicationContext()).addUser(user);
				}
			}
		}).start();
		track.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FunctionActivity.this,
						TrackActivity.class);
				intent.putExtra("username", username);
				startActivity(intent);
			}
		});
		appInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FunctionActivity.this,
						AppActivity.class);
				startActivity(intent);
			}
		});
		useControl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FunctionActivity.this,
						UseControlActivity.class);
				startActivity(intent);
			}
		});
		chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FunctionActivity.this,
						ChatActivity.class);
				startActivity(intent);
			}
		});
	}

	private void init() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		username = getIntent().getExtras().getString("username");
		selectChild = (Spinner) findViewById(R.id.selectChild);
		useTime = (Chronometer) findViewById(R.id.usetimeChronometer);
		track = (Button) findViewById(R.id.function_location);
		appInfo = (Button) findViewById(R.id.function_findapp);
		useControl = (Button) findViewById(R.id.function_controltime);
		chat = (Button) findViewById(R.id.function_chat);
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, " ebI4sTvwMxkiayc62NCO3NwR");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.function_setting, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_item_setting:
			intent = new Intent(FunctionActivity.this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_item_addFriend:
			intent = new Intent(FunctionActivity.this, AddFriendActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
