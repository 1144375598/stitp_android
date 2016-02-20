package njupt.stitp.android.view;

import njupt.stitp.android.R;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.service.LockService;
import njupt.stitp.android.util.SPHelper;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UnlockView extends LinearLayout {
	private Context mContext;
	private String username;
	private View rootView;
	private StringBuffer lockPwd;
	private String pwd;
	private StringBuffer textViewString;

	private TextView lockPwdView;
	private Button confirm;
	private Button backspace;
	private Button back;
	private Button num0;
	private Button num1;
	private Button num2;
	private Button num3;
	private Button num4;
	private Button num5;
	private Button num6;
	private Button num7;
	private Button num8;
	private Button num9;

	public UnlockView(Context context) {
		super(context);

		mContext = context;
		username = new SPHelper().getInfo(mContext, "userInfo", "username");
		lockPwd = new StringBuffer();
		textViewString = new StringBuffer();
		pwd = new UserDB(mContext).getUser(username).getLockPwd();

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rootView = inflater.inflate(R.layout.view_unlock, this);
		confirm = (Button) rootView.findViewById(R.id.btn_lock_confirm);
		backspace = (Button) rootView.findViewById(R.id.btn_lock_cancel);
		lockPwdView = (TextView) rootView.findViewById(R.id.edtlockPwd);
		back = (Button) rootView.findViewById(R.id.btn_lock_back);
		num0 = (Button) rootView.findViewById(R.id.btn_lock_0);
		num1 = (Button) rootView.findViewById(R.id.btn_lock_1);
		num2 = (Button) rootView.findViewById(R.id.btn_lock_2);
		num3 = (Button) rootView.findViewById(R.id.btn_lock_3);
		num4 = (Button) rootView.findViewById(R.id.btn_lock_4);
		num5 = (Button) rootView.findViewById(R.id.btn_lock_5);
		num6 = (Button) rootView.findViewById(R.id.btn_lock_6);
		num7 = (Button) rootView.findViewById(R.id.btn_lock_7);
		num8 = (Button) rootView.findViewById(R.id.btn_lock_8);
		num9 = (Button) rootView.findViewById(R.id.btn_lock_9);

		num0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lockPwd.append("0");
				textViewString.append("*");
				lockPwdView.setText(textViewString);
			}
		});
		num1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lockPwd.append("1");
				textViewString.append("*");
				lockPwdView.setText(textViewString);
			}
		});
		num2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lockPwd.append("2");
				textViewString.append("*");
				lockPwdView.setText(textViewString);
			}
		});
		num3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lockPwd.append("3");
				textViewString.append("*");
				lockPwdView.setText(textViewString);
			}
		});
		num4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lockPwd.append("4");
				textViewString.append("*");
				lockPwdView.setText(textViewString);
			}
		});
		num5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lockPwd.append("5");
				textViewString.append("*");
				lockPwdView.setText(textViewString);
			}
		});
		num6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lockPwd.append("6");
				textViewString.append("*");
				lockPwdView.setText(textViewString);
			}
		});
		num7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lockPwd.append("7");
				textViewString.append("*");
				lockPwdView.setText(textViewString);
			}
		});
		num8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lockPwd.append("8");
				textViewString.append("*");
				lockPwdView.setText(textViewString);
			}
		});
		num9.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lockPwd.append("9");
				textViewString.append("*");
				lockPwdView.setText(textViewString);
			}
		});

		backspace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (lockPwd.length() > 0) {
					lockPwd.deleteCharAt(lockPwd.length() - 1);
					textViewString.deleteCharAt(textViewString.length() - 1);
					lockPwdView.setText(textViewString);
				}
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, LockService.class);
				i.setAction(LockService.BACK_LOCK_ACTION);
				mContext.startService(i);

			}
		});

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (lockPwd.toString().equals(pwd)) {
					Intent i = new Intent(mContext, LockService.class);
					i.setAction(LockService.UNLOCK_SUCCESS_ACTION);
					mContext.startService(i);
				} else {
					lockPwd.delete(0, lockPwd.length()-1);
					textViewString.delete(0, textViewString.length()-1);
					lockPwdView.requestFocus();
					lockPwdView.setText("");
					lockPwdView.setHint(mContext
							.getString(R.string.password_wrong));
				}
			}
		});
	}

}
