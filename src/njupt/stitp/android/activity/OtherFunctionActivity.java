package njupt.stitp.android.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.model.User;
import njupt.stitp.android.util.MyActivityManager;
import njupt.stitp.android.util.SPHelper;
import njupt.stitp.android.util.ServerHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class OtherFunctionActivity extends ActionBarActivity {
	private Spinner selectChild;
	private Switch voiceControl;
	private Switch bumpRemind;
	private Switch lockScreen;
	private Switch continueUse;
	private Button exitApp;
	private TextView setLockPwd;
	private TextView lockPwdView;

	private UserDB userDB;

	private String username;
	private List<String> names;

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_otherfunction);
		voiceControl = (Switch) findViewById(R.id.switch_voice_control);
		bumpRemind = (Switch) findViewById(R.id.switch_bump_remind);
		lockScreen = (Switch) findViewById(R.id.switch_lock_screen);
		continueUse = (Switch) findViewById(R.id.switch_continue_use);
		exitApp = (Button) findViewById(R.id.exit_app);
		setLockPwd = (TextView) findViewById(R.id.tv_set_lock_pwd);
		lockPwdView = (TextView) findViewById(R.id.tv_lock_pwd);
		selectChild = (Spinner) findViewById(R.id.selectChild);
		userDB = new UserDB(getApplicationContext());
		initSpinner();

		String tempPwd = userDB.getUser(username).getLockPwd();
		if (tempPwd == null || tempPwd.length() == 0) {
			lockPwdView.setText(R.string.not_set_pwd);
		} else {
			lockPwdView.setText(tempPwd);
		}

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.lock_success),
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.lock_fail), Toast.LENGTH_SHORT)
							.show();
					break;
				case 2:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.unlock_success),
							Toast.LENGTH_SHORT).show();
					break;
				case 3:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.unlock_fail), Toast.LENGTH_SHORT)
							.show();
					break;
				default:
					break;
				}
			}
		};

		selectChild.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				username = names.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		voiceControl.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.dialog_setvoice,
							(ViewGroup) findViewById(R.id.dialog_voice));
					final EditText voice = (EditText) layout
							.findViewById(R.id.et_set_voice);
					new AlertDialog.Builder(OtherFunctionActivity.this)
							.setTitle(R.string.dialog_set_voice)
							.setView(layout)
							.setPositiveButton(R.string.confirm_button,
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (voice.getText().toString()
													.equals("")) {
												voice.requestFocus();
												voice.setError(getString(R.string.set_voice_wrong));
											}
											Integer voiceLevel = Integer
													.valueOf(voice.getText()
															.toString());
											if (voiceLevel > 100
													|| voiceLevel < 0) {
												voice.requestFocus();
												voice.setError(getString(R.string.set_voice_wrong));
											} else {
												dialog.dismiss();
												// 通知服务器打开音量控制
											}
										}
									})
							.setNegativeButton(R.string.cancel,
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											voiceControl.setChecked(false);
											dialog.dismiss();
										}
									}).show();
				} else {
					// 通知服务器关闭音量控制
				}
			}
		});
		bumpRemind.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					// 通知服务器打开颠簸提示
				} else {
					// 通知服务器关闭颠簸提示
				}

			}
		});
		lockScreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							String lockPwd = userDB.getUser(username)
									.getLockPwd();
							String path = "user/lockScreen";
							Map<String, String> params = new HashMap<String, String>();
							params.put("user.username", username);
							if (lockPwd == null || lockPwd.length() == 0) {
								params.put("user.lockPwd", "null");
							} else {
								params.put("user.lockPwd", lockPwd);
							}

							String result = new ServerHelper().getResult(path,
									params);
							if (result.equals("200")) {
								Message message = new Message();
								message.what = 0;
								handler.sendMessage(message);
							} else {
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							}
						}
					}).start();
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							String path = "user/unlockScreen";
							Map<String, String> params = new HashMap<String, String>();
							params.put("user.username", username);
							String result = new ServerHelper().getResult(path,
									params);
							if (result.equals("200")) {
								Message message = new Message();
								message.what = 2;
								handler.sendMessage(message);
							} else {
								Message message = new Message();
								message.what = 3;
								handler.sendMessage(message);
							}
						}
					}).start();
				}

			}
		});
		continueUse.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

			}
		});
		exitApp.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(OtherFunctionActivity.this)
						.setMessage(R.string.sure_exit)
						.setTitle(R.string.prompt)
						.setPositiveButton(R.string.confirm_button,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										new SPHelper().clear(
												OtherFunctionActivity.this,
												"userInfo");
										Intent intent = new Intent(
												OtherFunctionActivity.this,
												LoginActivity.class);
										startActivity(intent);
									}
								})
						.setNegativeButton(R.string.cancel,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).show();

			}
		});
		setLockPwd.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final EditText editText = new EditText(
						OtherFunctionActivity.this);
				editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				editText.setMinWidth(100);
				new AlertDialog.Builder(OtherFunctionActivity.this)
						.setTitle(R.string.dialog_set_lockpwd)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(editText)
						.setPositiveButton(R.string.confirm_button,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String lockPwd = editText.getText()
												.toString();
										userDB.updateLockPwd(username, lockPwd);
										lockPwdView.setText(lockPwd);
									}
								})
						.setNegativeButton(R.string.cancel,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).show();

			}
		});
	}

	private void initSpinner() {
		username = new SPHelper().getInfo(getApplicationContext(), "userInfo",
				"username");
		names = userDB.getChildNames(username);
		names.add(username);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				OtherFunctionActivity.this,
				android.R.layout.simple_spinner_item, names);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectChild.setAdapter(adapter);
		selectChild.setVisibility(View.VISIBLE);
	}
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
}
