package njupt.stitp.android.activity;

import java.util.HashMap;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.service.GetAPPMsgService;
import njupt.stitp.android.util.JsonUtil;
import njupt.stitp.android.util.SPHelper;
import njupt.stitp.android.util.ServerHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private EditText etusername;
	private EditText etpassword;
	private EditText confirmPassword;
	private ProgressDialog dialog;
	private String username;
	private String password;
	private String password2;
	private String path;
	private Handler handler;
	private Button registerButton;
	private Button returnLoginButton;
	private SPHelper sPHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		init();
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				username = etusername.getText().toString().trim();
				password = etpassword.getText().toString().trim();
				password2=confirmPassword.getText().toString().trim();
				if (username == null || username.isEmpty()) {
					etusername.requestFocus();
					etusername.setError(new StringBuffer(
							getString(R.string.username_is_null)));
					return;
				}else
				if (password == null || password.isEmpty()) {
					etpassword.requestFocus();
					etpassword.setError(new StringBuffer(
							getString(R.string.password_is_null)));
					return;
				}else
				if(!password.equals(password2)){
					confirmPassword.setText("");
					etpassword.requestFocus();
					etpassword.setError(new StringBuffer(
							getString(R.string.password_is_different)));
					return;
				}
				dialog.show();
				new Thread(new Runnable() {
					public void run() {
						path = "user/register";
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
		returnLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});
	}

	private void init() {
		etusername = (EditText) findViewById(R.id.name);
		etpassword = (EditText) findViewById(R.id.password);
		confirmPassword=(EditText) findViewById(R.id.comfirm_password);
		registerButton = (Button) findViewById(R.id.register);
		dialog = new ProgressDialog(RegisterActivity.this);
		dialog.setTitle(getString(R.string.register_upload_message));
		dialog.setMessage(new StringBuffer(getString(R.string.register_wait)));
		sPHelper =new SPHelper();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					Toast.makeText(RegisterActivity.this,
							getString(R.string.register_success),
							Toast.LENGTH_LONG).show();
					Map<String,String> params = new HashMap<String, String>();
					params.put("username", username);
					sPHelper.saveInfo(getApplicationContext(), "userInfo", params);
					Intent intent = new Intent(RegisterActivity.this,
							GetAPPMsgService.class);
					startService(intent);
					intent = new Intent(RegisterActivity.this,
							FunctionActivity.class);
					startActivity(intent);
					break;
				case 1:
					Toast.makeText(RegisterActivity.this,
							getString(R.string.register_fail),
							Toast.LENGTH_LONG).show();
					break;
				case -1:
					Toast.makeText(RegisterActivity.this,
							getString(R.string.connect_server_fail),
							Toast.LENGTH_LONG).show();
					break;
				}
				dialog.dismiss();
			}
		};
	}
}
