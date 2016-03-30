package njupt.stitp.android.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.OptionDB;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.util.JudgeState;
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
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	private TextView continueUseView;

	private UserDB userDB;
	private OptionDB optionDB;

	private String username;
	private String loginName;
	private List<String> names;

	private Handler handler;
	private static final int OPEN_LOCK_SUCCESS = 0;
	private static final int OPEN_LOCK_FAIL = 1;
	private static final int CLOSE_LOCK_SUCCESS = 2;
	private static final int CLOSE_LOCK_FAIL = 3;
	private static final int CANNOT_SET_SELF = 4;
	private static final int SET_USETIME_SUCCESS = 5;
	private static final int SET_USETIME_FAIL = 6;
	private static final int OPEN_USETIME_SUCCESS = 7;
	private static final int OPEN_USETIME_FAIL = 8;
	private static final int CLOSE_USETIME_SUCCESS = 9;
	private static final int CLOSE_USETIME_FAIL = 10;
	private static final int OPEN_BUMPREMIND_SUCCESS = 11;
	private static final int OPEN_BUMPREMIND_FAIL = 12;
	private static final int CLOSE_BUMPREMIND_SUCCESS = 13;
	private static final int CLOSE_BUMPREMIND_FAIL = 14;
	private static final int OPEN_VOICECONTROL_SUCCESS = 15;
	private static final int OPEN_VOICECONTROL_FAIL = 16;
	private static final int CLOSE_VOICECONTROL_SUCCESS = 17;
	private static final int CLOSE_VOICECONTROL_FAIL = 18;
	private static final int SET_LOCKPWD_SUCCESS = 19;
	private static final int SET_LOCKPWD_FAIL = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_otherfunction);
		getSupportActionBar().setBackgroundDrawable(
				ContextCompat.getDrawable(this,R.drawable.bg_theme));
		voiceControl = (Switch) findViewById(R.id.switch_voice_control);
		bumpRemind = (Switch) findViewById(R.id.switch_bump_remind);
		lockScreen = (Switch) findViewById(R.id.switch_lock_screen);
		continueUse = (Switch) findViewById(R.id.switch_continue_use);
		exitApp = (Button) findViewById(R.id.exit_app);
		setLockPwd = (TextView) findViewById(R.id.tv_set_lock_pwd);
		lockPwdView = (TextView) findViewById(R.id.tv_lock_pwd);
		selectChild = (Spinner) findViewById(R.id.selectChild);
		continueUseView = (TextView) findViewById(R.id.tv_continue_use_time);
		userDB = new UserDB(this);
		optionDB = new OptionDB(this);
		loginName = ((MyApplication) getApplication()).getUsername();
		username = loginName;
		setOption();

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case OPEN_LOCK_SUCCESS:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.lock_success),
							Toast.LENGTH_SHORT).show();
					break;
				case OPEN_LOCK_FAIL:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.lock_fail), Toast.LENGTH_SHORT)
							.show();
					lockScreen.setChecked(false);
					break;
				case CLOSE_LOCK_SUCCESS:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.unlock_success),
							Toast.LENGTH_SHORT).show();
					break;
				case CLOSE_LOCK_FAIL:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.unlock_fail), Toast.LENGTH_SHORT)
							.show();
					lockScreen.setChecked(true);
					break;
				case CANNOT_SET_SELF:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.cannot_set_self),
							Toast.LENGTH_SHORT).show();
					break;
				case SET_USETIME_SUCCESS:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.set_usetime_success),
							Toast.LENGTH_SHORT).show();
					continueUseView.setText(userDB.getUser(username)
							.getTimeOfContinuousUse() + "分钟");
					break;
				case SET_USETIME_FAIL:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.set_usetime_fail),
							Toast.LENGTH_SHORT).show();
					break;
				case OPEN_USETIME_SUCCESS:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.open_usetime_success),
							Toast.LENGTH_SHORT).show();
					break;
				case OPEN_USETIME_FAIL:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.open_usetime_fail),
							Toast.LENGTH_SHORT).show();
					continueUse.setChecked(false);
					break;
				case CLOSE_USETIME_SUCCESS:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.close_usetime_success),
							Toast.LENGTH_SHORT).show();
					break;
				case CLOSE_USETIME_FAIL:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.close_usetime_fail),
							Toast.LENGTH_SHORT).show();
					continueUse.setChecked(true);
					break;
				case OPEN_BUMPREMIND_SUCCESS:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.open_bumpremind_success),
							Toast.LENGTH_SHORT).show();
					break;
				case OPEN_BUMPREMIND_FAIL:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.open_bumpremind_fail),
							Toast.LENGTH_SHORT).show();
					bumpRemind.setChecked(false);
					break;
				case CLOSE_BUMPREMIND_SUCCESS:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.close_bumpremind_success),
							Toast.LENGTH_SHORT).show();
					break;
				case CLOSE_BUMPREMIND_FAIL:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.close_bumpremind_fail),
							Toast.LENGTH_SHORT).show();
					bumpRemind.setChecked(true);
					break;
				case OPEN_VOICECONTROL_SUCCESS:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.open_voicecontrol_success),
							Toast.LENGTH_SHORT).show();
					break;
				case OPEN_VOICECONTROL_FAIL:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.open_voicecontrol_fail),
							Toast.LENGTH_SHORT).show();
					voiceControl.setChecked(false);
					break;
				case CLOSE_VOICECONTROL_SUCCESS:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.close_voicecontrol_success),
							Toast.LENGTH_SHORT).show();
					break;
				case CLOSE_VOICECONTROL_FAIL:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.close_voicecontrol_fail),
							Toast.LENGTH_SHORT).show();
					voiceControl.setChecked(true);
					break;
				case SET_LOCKPWD_SUCCESS:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.set_lockpwd_success),
							Toast.LENGTH_SHORT).show();
					lockPwdView.setText(userDB.getUser(username).getLockPwd());
					break;
				case SET_LOCKPWD_FAIL:
					Toast.makeText(OtherFunctionActivity.this,
							getString(R.string.set_lockpwd_fail),
							Toast.LENGTH_SHORT).show();
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
				setOption();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		voiceControl
				.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (username.equals(loginName)) {
							Message message = new Message();
							message.what = 4;
							handler.sendMessage(message);
							setOption();
							return;
						}

						if (voiceControl.isChecked()) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									if (!JudgeState
											.isNetworkConnected(getApplicationContext())) {
										Message message = new Message();
										message.what = OPEN_VOICECONTROL_FAIL;
										handler.sendMessage(message);
										optionDB.setVoiceControl(username, 0);
										return;
									}
									String path = "user/bumpRemind";
									Map<String, String> params = new HashMap<String, String>();
									params.put("user.username", username);
									params.put("serviceCode", "3");
									String result = new ServerHelper()
											.getResult(path, params);
									if (TextUtils.equals(result, "200")) {
										Message message = new Message();
										message.what = OPEN_VOICECONTROL_SUCCESS;
										handler.sendMessage(message);
										optionDB.setVoiceControl(username, 1);
									} else {
										Message message = new Message();
										message.what = OPEN_VOICECONTROL_FAIL;
										handler.sendMessage(message);
										optionDB.setVoiceControl(username, 0);
									}
								}
							}).start();
						} else {
							new Thread(new Runnable() {

								@Override
								public void run() {
									if (!JudgeState
											.isNetworkConnected(getApplicationContext())) {
										Message message = new Message();
										message.what = CLOSE_VOICECONTROL_FAIL;
										handler.sendMessage(message);
										optionDB.setVoiceControl(username, 0);
										return;
									}
									String path = "user/bumpRemind";
									Map<String, String> params = new HashMap<String, String>();
									params.put("user.username", username);
									params.put("serviceCode", "4");
									String result = new ServerHelper()
											.getResult(path, params);
									if (TextUtils.equals(result, "200")) {
										Message message = new Message();
										message.what = CLOSE_VOICECONTROL_SUCCESS;
										handler.sendMessage(message);
										optionDB.setVoiceControl(username, 1);
									} else {
										Message message = new Message();
										message.what = CLOSE_VOICECONTROL_FAIL;
										handler.sendMessage(message);
										optionDB.setVoiceControl(username, 0);
									}
								}
							}).start();
						}
					}
				});
		bumpRemind.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (username.equals(loginName)) {
					Message message = new Message();
					message.what = 4;
					handler.sendMessage(message);
					setOption();
					return;
				}

				if (bumpRemind.isChecked()) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							if (!JudgeState
									.isNetworkConnected(getApplicationContext())) {
								Message message = new Message();
								message.what = OPEN_BUMPREMIND_FAIL;
								handler.sendMessage(message);
								optionDB.setBumpRemind(username, 0);
								return;
							}
							String path = "user/bumpRemind";
							Map<String, String> params = new HashMap<String, String>();
							params.put("user.username", username);
							params.put("serviceCode", "5");
							String result = new ServerHelper().getResult(path,
									params);
							if (TextUtils.equals(result, "200")) {
								Message message = new Message();
								message.what = OPEN_BUMPREMIND_SUCCESS;
								handler.sendMessage(message);
								optionDB.setBumpRemind(username, 1);
							} else {
								Message message = new Message();
								message.what = OPEN_BUMPREMIND_FAIL;
								handler.sendMessage(message);
								optionDB.setBumpRemind(username, 0);
							}
						}
					}).start();
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							if (!JudgeState
									.isNetworkConnected(getApplicationContext())) {
								Message message = new Message();
								message.what = CLOSE_BUMPREMIND_FAIL;
								handler.sendMessage(message);
								optionDB.setBumpRemind(username, 1);
								return;
							}
							String path = "user/bumpRemind";
							Map<String, String> params = new HashMap<String, String>();
							params.put("user.username", username);
							params.put("serviceCode", "6");
							String result = new ServerHelper().getResult(path,
									params);
							if (TextUtils.equals(result, "200")) {
								Message message = new Message();
								message.what = CLOSE_BUMPREMIND_SUCCESS;
								handler.sendMessage(message);
								optionDB.setBumpRemind(username, 0);
							} else {
								Message message = new Message();
								message.what = CLOSE_BUMPREMIND_FAIL;
								handler.sendMessage(message);
								optionDB.setBumpRemind(username, 1);
							}
						}
					}).start();
				}
			}
		});
		lockScreen.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (username.equals(loginName)) {
					Message message = new Message();
					message.what = 4;
					handler.sendMessage(message);
					setOption();
					return;
				}

				if (lockScreen.isChecked()) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							if (!JudgeState
									.isNetworkConnected(getApplicationContext())) {
								Message message = new Message();
								message.what = OPEN_LOCK_FAIL;
								handler.sendMessage(message);
								optionDB.setLockScreen(username, 0);
								return;
							}
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
							if (TextUtils.equals(result, "200")) {
								Message message = new Message();
								message.what = OPEN_LOCK_SUCCESS;
								handler.sendMessage(message);
								optionDB.setLockScreen(username, 1);
							} else {
								Message message = new Message();
								message.what = OPEN_LOCK_FAIL;
								handler.sendMessage(message);
								optionDB.setLockScreen(username, 0);
							}
						}
					}).start();
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							if (!JudgeState
									.isNetworkConnected(getApplicationContext())) {
								Message message = new Message();
								message.what = CLOSE_LOCK_FAIL;
								handler.sendMessage(message);
								optionDB.setLockScreen(username, 1);
								return;
							}
							String path = "user/unlockScreen";
							Map<String, String> params = new HashMap<String, String>();
							params.put("user.username", username);
							String result = new ServerHelper().getResult(path,
									params);
							if (TextUtils.equals(result, "200")) {
								Message message = new Message();
								message.what = CLOSE_LOCK_SUCCESS;
								handler.sendMessage(message);
								optionDB.setLockScreen(username, 0);
							} else {
								Message message = new Message();
								message.what = CLOSE_LOCK_FAIL;
								handler.sendMessage(message);
								optionDB.setLockScreen(username, 1);
							}
						}
					}).start();
				}

			}
		});
		continueUse.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (username.equals(loginName)) {
					Message message = new Message();
					message.what = 4;
					handler.sendMessage(message);
					setOption();
					return;
				}

				if (continueUse.isChecked()) {
					if (userDB.getUser(username).getTimeOfContinuousUse() == 0) {
						Toast.makeText(OtherFunctionActivity.this,
								getString(R.string.not_set_use_time),
								Toast.LENGTH_SHORT).show();
						continueUse.setChecked(false);
						return;
					}
					new Thread(new Runnable() {

						@Override
						public void run() {
							if (!JudgeState
									.isNetworkConnected(getApplicationContext())) {
								Message message = new Message();
								message.what = OPEN_USETIME_FAIL;
								handler.sendMessage(message);
								optionDB.setContinueUse(username, 0);
								return;
							}
							String path = "user/continueUseTime";
							Map<String, String> params = new HashMap<String, String>();
							params.put("user.username", username);
							params.put("serviceCode", "7");
							String result = new ServerHelper().getResult(path,
									params);
							if (TextUtils.equals(result, "200")) {
								Message message = new Message();
								message.what = OPEN_USETIME_SUCCESS;
								handler.sendMessage(message);
								optionDB.setContinueUse(username, 1);
							} else {
								Message message = new Message();
								message.what = OPEN_USETIME_FAIL;
								handler.sendMessage(message);
								optionDB.setContinueUse(username, 0);
							}
						}
					}).start();
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							if (!JudgeState
									.isNetworkConnected(getApplicationContext())) {
								Message message = new Message();
								message.what = CLOSE_USETIME_FAIL;
								handler.sendMessage(message);
								optionDB.setContinueUse(username, 1);
								return;
							}
							String path = "user/continueUseTime";
							Map<String, String> params = new HashMap<String, String>();
							params.put("user.username", username);
							params.put("serviceCode", "8");
							String result = new ServerHelper().getResult(path,
									params);
							if (TextUtils.equals(result, "200")) {
								Message message = new Message();
								message.what = CLOSE_USETIME_SUCCESS;
								handler.sendMessage(message);
								optionDB.setContinueUse(username, 0);
							} else {
								Message message = new Message();
								message.what = CLOSE_USETIME_FAIL;
								handler.sendMessage(message);
								optionDB.setContinueUse(username, 1);
							}
						}
					}).start();
				}
			}
		});

		continueUseView
				.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (username.equals(loginName)) {
							Message message = new Message();
							message.what = 4;
							handler.sendMessage(message);
							return;
						}

						final EditText editText = new EditText(
								OtherFunctionActivity.this);
						editText.setInputType(InputType.TYPE_CLASS_NUMBER);
						editText.setMinWidth(100);
						new AlertDialog.Builder(OtherFunctionActivity.this)
								.setTitle(R.string.dialog_set_time)
								.setIcon(android.R.drawable.ic_dialog_info)
								.setView(editText)
								.setPositiveButton(R.string.confirm_button,
										new OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												Integer useTime0 = Integer
														.valueOf(editText
																.getText()
																.toString());
												if (useTime0 == 0) {
													Toast.makeText(
															OtherFunctionActivity.this,
															getString(R.string.use_time_cannot_be_zero),
															Toast.LENGTH_SHORT)
															.show();
													return;
												}
												new Thread(new Runnable() {

													@Override
													public void run() {
														if (!JudgeState
																.isNetworkConnected(getApplicationContext())) {
															Message message = new Message();
															message.what = SET_USETIME_FAIL;
															handler.sendMessage(message);
															return;
														}
														Integer useTime = Integer
																.valueOf(editText
																		.getText()
																		.toString());

														String path = "user/continueUseTime";
														Map<String, String> params = new HashMap<String, String>();
														params.put(
																"user.username",
																username);
														params.put(
																"user.timeOfContinuousUse",
																useTime.toString());
														params.put(
																"serviceCode",
																"9");
														String result = new ServerHelper()
																.getResult(
																		path,
																		params);
														Log.i("result", result);
														if (TextUtils.equals(
																result, "200")) {
															Message message = new Message();
															message.what = SET_USETIME_SUCCESS;
															handler.sendMessage(message);
															userDB.updateContinueUse(
																	username,
																	useTime);
														} else {
															Message message = new Message();
															message.what = SET_USETIME_FAIL;
															handler.sendMessage(message);
														}
													}
												}).start();
											}
										})
								.setNegativeButton(R.string.cancel,
										new OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}).show();

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

				if (username.equals(loginName)) {
					Message message = new Message();
					message.what = 4;
					handler.sendMessage(message);
					return;
				}

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
										new Thread(new Runnable() {

											@Override
											public void run() {
												if (!JudgeState
														.isNetworkConnected(getApplicationContext())) {
													Message message = new Message();
													message.what = SET_LOCKPWD_FAIL;
													handler.sendMessage(message);
													return;
												}
												String lockPwd = editText
														.getText().toString();

												String path = "user/continueUseTime";
												Map<String, String> params = new HashMap<String, String>();
												params.put("user.username",
														username);
												params.put("user.lockPwd",
														lockPwd);
												params.put("serviceCode", "0");
												String result = new ServerHelper()
														.getResult(path, params);
												Log.i("result", result);
												if (TextUtils.equals(result,
														"200")) {
													Message message = new Message();
													message.what = SET_LOCKPWD_SUCCESS;
													handler.sendMessage(message);
													userDB.updateLockPwd(
															username, lockPwd);
												} else {
													Message message = new Message();
													message.what = SET_LOCKPWD_FAIL;
													handler.sendMessage(message);
												}
											}
										}).start();
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
		names = userDB.getAllUserName(username);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				OtherFunctionActivity.this,
				android.R.layout.simple_spinner_item, names);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectChild.setAdapter(adapter);
		selectChild.setVisibility(View.VISIBLE);
	}

	private void setOption() {
		if (optionDB.getLockScreen(username) == 0) {
			lockScreen.setChecked(false);
		} else {
			lockScreen.setChecked(true);
		}
		if (optionDB.getBumpRemind(username) == 0) {
			bumpRemind.setChecked(false);
		} else {
			bumpRemind.setChecked(true);
		}
		if (optionDB.getContinueUse(username) == 0) {
			continueUse.setChecked(false);
		} else {
			continueUse.setChecked(true);
		}
		if (optionDB.getVoiceControl(username) == 0) {
			voiceControl.setChecked(false);
		} else {
			voiceControl.setChecked(true);
		}
		if (!username.equals(loginName)) {
			String tempPwd = userDB.getUser(username).getLockPwd();
			if (tempPwd == null || tempPwd.length() == 0) {
				lockPwdView.setText(R.string.not_set);
			} else {
				lockPwdView.setText(tempPwd);
			}
		} else {
			lockPwdView.setText("****");
		}
		int time = 0;
		if (userDB.getUser(username) != null) {
			time = userDB.getUser(username).getTimeOfContinuousUse();
		}
		if (time == 0) {
			continueUseView.setText(R.string.not_set);
		} else {
			continueUseView.setText(time + "分钟");
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (NavUtils.getParentActivityName(OtherFunctionActivity.this) != null) {
				NavUtils.navigateUpFromSameTask(OtherFunctionActivity.this);
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
		if (userDB != null) {
			userDB.close();
		}
		if (optionDB != null) {
			optionDB.close();
		}
		super.onDestroy();
	}
}
