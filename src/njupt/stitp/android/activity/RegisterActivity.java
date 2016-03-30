package njupt.stitp.android.activity;

import java.util.HashMap;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.service.GetAPPMsgService;
import njupt.stitp.android.util.JsonUtil;
import njupt.stitp.android.util.JudgeState;
import njupt.stitp.android.util.MyActivityManager;
import njupt.stitp.android.util.SPHelper;
import njupt.stitp.android.util.ServerHelper;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends ActionBarActivity {
	private EditText etusername;
	private EditText etpassword;
	private EditText confirmPassword;
	private EditText validationQuestion;
	private EditText validationAnswer;
	private EditText qqNumber;
	private ProgressDialog dialog;
	private String question;
	private String answer;
	private String username;
	private String password;
	private String password2;
	private String qq;
	private String path;
	private Handler handler;
	private Button registerButton;
	private SPHelper sPHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_register);
		init();
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				username = etusername.getText().toString().trim();
				password = etpassword.getText().toString().trim();
				password2 = confirmPassword.getText().toString().trim();
				question = validationQuestion.getText().toString().trim();
				answer = validationAnswer.getText().toString().trim();
				qq = qqNumber.getText().toString().trim();
				if (username == null || username.isEmpty()) {
					etusername.requestFocus();
					etusername.setError(getString(R.string.username_is_null));
					return;
				} else if (password == null || password.isEmpty()) {
					etpassword.requestFocus();
					etpassword.setError(getString(R.string.password_is_null));
					return;
				} else if (!password.equals(password2)) {
					confirmPassword.setText("");
					etpassword.requestFocus();
					etpassword
							.setError(getString(R.string.password_is_different));
					return;
				} else if (question == null || question.isEmpty()) {
					validationQuestion.requestFocus();
					validationQuestion
							.setError(getString(R.string.question_is_null));
					return;
				} else if (answer == null || answer.isEmpty()) {
					validationAnswer.requestFocus();
					validationAnswer
							.setError(getString(R.string.answer_is_null));
					return;
				} else if (qq == null || qq.isEmpty()) {
					qqNumber.requestFocus();
					qqNumber.setError(getString(R.string.QQ_cannot_null));
					return;
				}
				dialog.show();
				new Thread(new Runnable() {
					public void run() {
						if (!JudgeState
								.isNetworkConnected(getApplicationContext())) {
							Message msg = new Message();
							msg.what = MyApplication.NETWORK_DISCONNECT;
							handler.sendMessage(msg);
							return;
						}
						path = "user/register";
						Map<String, String> params = new HashMap<String, String>();
						params.put("user.username", username);
						params.put("user.password", password);
						params.put("user.question", question);
						params.put("user.answer", answer);
						params.put("user.QQ", qq);
						String result = new ServerHelper().getResult(path,
								params);
						int result_code = JsonUtil.getResultCode(result);
						Message message = new Message();
						message.what = result_code;
						handler.sendMessage(message);

					}
				}).start();
			}
		});
	}

	
	private void init() {
		getSupportActionBar().setTitle(
				new StringBuffer(getString(R.string.register_button)));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				ContextCompat.getDrawable(this,R.drawable.bg_register));
		etusername = (EditText) findViewById(R.id.name);
		etpassword = (EditText) findViewById(R.id.password);
		confirmPassword = (EditText) findViewById(R.id.comfirm_password);
		validationQuestion = (EditText) findViewById(R.id.validation_question);
		validationAnswer = (EditText) findViewById(R.id.validation_answer);
		registerButton = (Button) findViewById(R.id.register);
		qqNumber = (EditText) findViewById(R.id.QQ);
		dialog = new ProgressDialog(RegisterActivity.this);
		dialog.setTitle(getString(R.string.register_upload_message));
		dialog.setMessage(new StringBuffer(getString(R.string.register_wait)));
		sPHelper = new SPHelper();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					Toast.makeText(RegisterActivity.this,
							getString(R.string.register_success),
							Toast.LENGTH_LONG).show();
					((MyApplication) getApplication()).setUsername(username);
					Map<String, String> params = new HashMap<String, String>();
					params.put("username", username);
					sPHelper.saveInfo(getApplicationContext(), "userInfo",
							params);
					Intent intent = new Intent(RegisterActivity.this,
							GetAPPMsgService.class);
					startService(intent);
					intent = new Intent(RegisterActivity.this,
							FunctionActivity.class);
					intent.putExtra("username", username);
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
				case MyApplication.NETWORK_DISCONNECT:
					Toast.makeText(RegisterActivity.this,
							getString(R.string.network_disconnect),
							Toast.LENGTH_SHORT).show();
					break;
				}
				dialog.dismiss();
			}
		};

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (NavUtils.getParentActivityName(RegisterActivity.this) != null) {
				NavUtils.navigateUpFromSameTask(RegisterActivity.this);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
