package njupt.stitp.android.activity;

import java.util.Date;
import java.util.List;

import njupt.stitp.android.R;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.OptionDB;
import njupt.stitp.android.db.RelationshipDB;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.service.GetAPPMsgService;
import njupt.stitp.android.service.LockService;
import njupt.stitp.android.service.ProtectEyeService;
import njupt.stitp.android.service.TrackService;
import njupt.stitp.android.util.MyActivityManager;
import njupt.stitp.android.util.PushUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class FunctionActivity extends ActionBarActivity {
	private Spinner selectChild;
	private Chronometer useTime;
	private Button track;
	private Button appInfo;
	private Button useControl;
	private Button chat;
	private String username;
	private String name;
	private List<String> names;
	private UserDB userDB;
	private OptionDB optionDB;
	private RelationshipDB relationshipDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_function);
		init();
		startServices();
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
				intent.putExtra("username", username);
				startActivity(intent);
			}
		});
		useControl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FunctionActivity.this,
						UseControlActivity.class);
				intent.putExtra("username", username);
				startActivity(intent);
			}
		});
		chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.equals(name, username)) {
					Toast.makeText(FunctionActivity.this,
							getString(R.string.cannot_chat_to_self),
							Toast.LENGTH_SHORT).show();
					;
					return;
				}
				String QQ = userDB.getUser(name).getQQ();
				String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + QQ;
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
		});
		selectChild.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				name = names.get(position);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void init() {
		username = ((MyApplication) getApplication()).getUsername();
		selectChild = (Spinner) findViewById(R.id.selectChild);
		useTime = (Chronometer) findViewById(R.id.usetimeChronometer);
		track = (Button) findViewById(R.id.function_location);
		appInfo = (Button) findViewById(R.id.function_findapp);
		useControl = (Button) findViewById(R.id.function_controltime);
		chat = (Button) findViewById(R.id.function_chat);
		userDB = new UserDB(this);
		optionDB = new OptionDB(this);
		relationshipDB = new RelationshipDB(this);

		// 百度云推送服务
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, PushUtil.getApiKey());
	}

	public void setUseTime() {
		useTime.setFormat("连续使用时间:\n %s");
		long time = ((MyApplication) getApplication()).getTime();
		long time2 = (new Date().getTime() - time);// 连续使用了多少毫秒
		useTime.setBase(SystemClock.elapsedRealtime() - time2);
	}

	@Override
	protected void onStart() {
		setUseTime();
		useTime.start();
		super.onStart();
	}

	@Override
	protected void onResume() {
		initSpinner();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		useTime.stop();
		userDB.close();
		optionDB.close();
		relationshipDB.close();
		Log.i("Function activity", "destory");
		super.onDestroy();
	}

	private void initSpinner() {
		Log.i("update spinner", "update spinner");
		names = relationshipDB.getFriendName(username);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				FunctionActivity.this, android.R.layout.simple_spinner_item,
				names);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectChild.setAdapter(adapter);
		selectChild.setVisibility(View.VISIBLE);
	}

	private void startServices() {
		Intent intent1 = new Intent(FunctionActivity.this,
				GetAPPMsgService.class);
		startService(intent1);
		intent1 = new Intent(FunctionActivity.this, TrackService.class);
		startService(intent1);
		if (optionDB.getBumpRemind(username) == 1) {
			intent1 = new Intent(this, ProtectEyeService.class);
			intent1.setAction(ProtectEyeService.OPEN_BUMP_REMIND_ACTION);
			startService(intent1);
		}
		if (optionDB.getContinueUse(username) == 1) {
			intent1 = new Intent(this, ProtectEyeService.class);
			intent1.setAction(ProtectEyeService.OPEN_CONTINUE_USE_ACTION);
			startService(intent1);
		}
		if (optionDB.getLockScreen(username) == 1) {
			intent1 = new Intent(this, LockService.class);
			intent1.setAction(LockService.LOCK_ACTION);
			startService(intent1);
		}
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
		case R.id.menu_item_function:
			intent = new Intent(FunctionActivity.this,
					OtherFunctionActivity.class);
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

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				FunctionActivity.this);
		builder.setTitle(R.string.prompt)
				.setIconAttribute(android.R.attr.alertDialogIcon)
				.setMessage(R.string.sure_exit)
				.setPositiveButton(R.string.confirm_button,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								MyActivityManager.getInstance()
										.finshAllActivities();
							}
						}).setNegativeButton(R.string.cancel, null).create()
				.show();
	}
}
