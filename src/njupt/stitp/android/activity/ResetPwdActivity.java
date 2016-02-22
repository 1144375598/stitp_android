package njupt.stitp.android.activity;


import java.util.HashMap;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.util.MyActivityManager;
import njupt.stitp.android.util.ServerHelper;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPwdActivity extends ActionBarActivity{
	private TextView resetPwdQuestion;
	private EditText resetPwdAnswer;
	private EditText resetPassword;
	private Button confirm;
	private String answer;
	private String password;
	private String inputAnswer;
	private String path;
	private String username;
	private ProgressDialog dialog;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_resetpwd);
		init();
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				inputAnswer=resetPwdAnswer.getText().toString().trim();
				password = resetPassword.getText().toString().trim();
				if(inputAnswer==null||inputAnswer.isEmpty()){
					resetPwdAnswer.requestFocus();
					resetPwdAnswer.setError(new StringBuffer(
							getString(R.string.answer_is_null)));
					return;
				}else if(password==null||password.isEmpty()){
					resetPassword.requestFocus();
					resetPassword.setError(new StringBuffer(
							getString(R.string.password_is_null)));
					return;
				} else if(answer!=inputAnswer){
					resetPwdAnswer.requestFocus();
					resetPwdAnswer.setError(new StringBuffer(
							getString(R.string.answer_is_wrong)));
					return;
				}
				dialog.show();
				new Thread(new Runnable() {
					public void run() {
						path = "user/resetPassword";
						Map<String, String> params = new HashMap<String, String>();
						params.put("user.username", username);
						params.put("user.password", password);
						String result = new ServerHelper().getResult(path,
								params);
						if(result.equals("200")){
							Message message = new Message();
							message.what = 200;
							handler.sendMessage(message);
						}
					}
				}).start();
			}
		});
	}
	private void init(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(new StringBuffer(getString(R.string.reset_pwd)));
		resetPwdAnswer=(EditText) findViewById(R.id.resetpwd_answer);
		resetPwdQuestion=(TextView) findViewById(R.id.resetpwd_question);
		confirm=(Button) findViewById(R.id.confirm);
		resetPassword=(EditText) findViewById(R.id.new_pwd);
		dialog = new ProgressDialog(ResetPwdActivity.this);
		dialog.setTitle(getString(R.string.register_upload_message));
		dialog.setMessage(new StringBuffer(getString(R.string.register_wait)));
		username= getIntent().getExtras().getString("username");
		resetPwdQuestion.setText(getIntent().getExtras().getString("question"));
		answer=getIntent().getExtras().getString("answer");
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 200:
					Toast.makeText(ResetPwdActivity.this,
							getString(R.string.password_reset_success),
							Toast.LENGTH_SHORT).show();
					Intent intent=new Intent(ResetPwdActivity.this,LoginActivity.class);
					startActivity(intent);
					break;

				default:
					Toast.makeText(ResetPwdActivity.this,
							getString(R.string.password_reset_fail),
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
	        if(NavUtils.getParentActivityName(ResetPwdActivity.this)!=null){
	        	NavUtils.navigateUpFromSameTask(ResetPwdActivity.this);
	        }
	        return true;
	        default:
	        	return super.onOptionsItemSelected(item);
	    } 	    
	}  
}
