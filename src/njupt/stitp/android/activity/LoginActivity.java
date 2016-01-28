package njupt.stitp.android.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.model.User;
import njupt.stitp.android.service.GetAPPMsgService;
import njupt.stitp.android.service.TrackService;
import njupt.stitp.android.util.JsonUtil;
import njupt.stitp.android.util.SPHelper;
import njupt.stitp.android.util.ServerHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Button login;
	private Button register;
	private Button forgetPassword;
	private EditText etusername;
	private EditText etpassword;
	private String username;
	private String password;
	private ProgressDialog p;
	private SPHelper sPHelper;
	private String path;
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		sPHelper = new SPHelper();
		if (!sPHelper.getInfo(getApplicationContext(), "userInfo", "username")
				.equals("")) {
			Intent intent1 = new Intent(LoginActivity.this,
					GetAPPMsgService.class);
			startService(intent1);
			intent1=new Intent(LoginActivity.this,TrackService.class);
			startService(intent1);
			Intent intent = new Intent(LoginActivity.this,
					FunctionActivity.class);
			username=sPHelper.getInfo(getApplicationContext(), "userInfo", "username");
			intent.putExtra("username", username);
			startActivity(intent);
		}
		init();
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
		forgetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				username = etusername.getText().toString().trim();
				if (username == null || username.isEmpty()) {
					etusername.requestFocus();
					etusername.setError(new StringBuffer(
							getString(R.string.username_is_null)));
					return;
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						path = "user/getValidation";
						Map<String, String> params = new HashMap<String, String>();
						params.put("user.username", username);
						String json = new ServerHelper()
								.getResult(path, params);
						Map<String, String> validation = JsonUtil
								.getValidation(json);
						if (validation == null) {
							Message msg = new Message();
							msg.what = -2;
							handler.sendMessage(msg);
							return;
						}
						Intent intent = new Intent(LoginActivity.this,
								ResetPwdActivity.class);
						intent.putExtra("username", username);
						intent.putExtra("question", validation.get("question"));
						intent.putExtra("answer", validation.get("answer"));
						startActivity(intent);
					}
				}).start();
			}
		});
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				username = etusername.getText().toString().trim();
				password = etpassword.getText().toString().trim();
				if (username == null || username.isEmpty()) {
					etusername.requestFocus();
					etusername.setError(new StringBuffer(
							getString(R.string.username_is_null)));
					return;
				}
				if (password == null || password.isEmpty()) {
					etpassword.requestFocus();
					etpassword.setError(new StringBuffer(
							getString(R.string.password_is_null)));
					return;
				}
				p.show();
				new Thread(new Runnable() {

					@Override
					public void run() {
						path = "user/login";
						Map<String, String> params = new HashMap<String, String>();
						params.put("user.username", username);
						params.put("user.password", password);
						String result = new ServerHelper().getResult(path,
								params);
						int result_code = JsonUtil.LoginAndRegister(result);
						Message message = new Message();
						message.what = result_code;
						handler.sendMessage(message);
					}
				}).start();
			}
		});
	}

	private void init() {
		etusername = (EditText) findViewById(R.id.etusername);
		etpassword = (EditText) findViewById(R.id.etpassword);
		forgetPassword = (Button) findViewById(R.id.forgetPassword);
		login = (Button) findViewById(R.id.login);
		register = (Button) findViewById(R.id.register);
		p = new ProgressDialog(LoginActivity.this);
		p.setTitle(getString(R.string.loginProgress_title));
		p.setMessage(new StringBuffer(getString(R.string.loginProgress_message)));
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					Toast.makeText(LoginActivity.this,
							getString(R.string.login_success),
							Toast.LENGTH_LONG).show();
					
					Map<String, String> params = new HashMap<String, String>();
					params.put("username", username);
					sPHelper.saveInfo(getApplicationContext(), "userInfo",
							params);
					Intent intent = new Intent(LoginActivity.this,
							GetAPPMsgService.class);
					startService(intent);
					intent = new Intent(LoginActivity.this,
							FunctionActivity.class);
					intent.putExtra("username", username);
					startActivity(intent);
					break;
				case 1:
					Toast.makeText(LoginActivity.this,
							getString(R.string.username_error),
							Toast.LENGTH_LONG).show();
					break;
				case 2:
					Toast.makeText(LoginActivity.this,
							getString(R.string.user_not_exist),
							Toast.LENGTH_LONG).show();
					break;
				case -1:
					Toast.makeText(LoginActivity.this,
							getString(R.string.connect_server_fail),
							Toast.LENGTH_LONG).show();
					break;
				case -2:
					etusername.requestFocus();
					etusername.setError(new StringBuffer(
							getString(R.string.username_not_exist)));
					break;
				}
				p.dismiss();
			}
		};
	}

}
