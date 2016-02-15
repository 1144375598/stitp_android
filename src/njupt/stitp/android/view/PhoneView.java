package njupt.stitp.android.view;

import njupt.stitp.android.R;
import njupt.stitp.android.service.MyService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhoneView extends LinearLayout {
	private Context mContext;
	private View rootView;
	private StringBuffer phoneNumString;

	private TextView phoneNum;
	private Button call;
	private Button back;
	private Button backspace;// 退格
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
	private Button num10;// #号键
	private Button num11;// *号键

	public PhoneView(Context context) {
		super(context);
		mContext = context;
		phoneNumString = new StringBuffer();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		rootView = inflater.inflate(R.layout.view_phone, this);
		call = (Button) rootView.findViewById(R.id.btnOut);
		back = (Button) rootView.findViewById(R.id.btn_call_back);
		phoneNum = (TextView) rootView.findViewById(R.id.edtNumber);
		backspace = (Button) rootView.findViewById(R.id.btnCancel);
		num0 = (Button) rootView.findViewById(R.id.btn_call_0);
		num1 = (Button) rootView.findViewById(R.id.btn_call_1);
		num2 = (Button) rootView.findViewById(R.id.btn_call_2);
		num3 = (Button) rootView.findViewById(R.id.btn_call_3);
		num4 = (Button) rootView.findViewById(R.id.btn_call_4);
		num5 = (Button) rootView.findViewById(R.id.btn_call_5);
		num6 = (Button) rootView.findViewById(R.id.btn_call_6);
		num7 = (Button) rootView.findViewById(R.id.btn_call_7);
		num8 = (Button) rootView.findViewById(R.id.btn_call_8);
		num9 = (Button) rootView.findViewById(R.id.btn_call_9);
		num10 = (Button) rootView.findViewById(R.id.btn_call_10);
		num11 = (Button) rootView.findViewById(R.id.btn_call_11);

		num0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append(0);
				phoneNum.setText(phoneNumString);
			}
		});
		num1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append(1);
				phoneNum.setText(phoneNumString);
			}
		});

		num2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append(2);
				phoneNum.setText(phoneNumString);
			}
		});

		num3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append(3);
				phoneNum.setText(phoneNumString);
			}
		});

		num4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append(4);
				phoneNum.setText(phoneNumString);
			}
		});
		num5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append(5);
				phoneNum.setText(phoneNumString);
			}
		});
		num6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append(6);
				phoneNum.setText(phoneNumString);
			}
		});
		num7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append(7);
				phoneNum.setText(phoneNumString);
			}
		});
		num8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append(8);
				phoneNum.setText(phoneNumString);
			}
		});
		num9.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append(9);
				phoneNum.setText(phoneNumString);
			}
		});
		num10.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append("#");
				phoneNum.setText(phoneNumString);
			}
		});
		num11.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNumString.append("*");
				phoneNum.setText(phoneNumString);
			}
		});

		backspace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (phoneNumString.length() > 0) {
					phoneNumString.deleteCharAt(phoneNumString.length() - 1);
					phoneNum.setText(phoneNumString);
				}
			}
		});

		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.CALL");
				intent.setData(Uri.parse("tel:" + phoneNumString));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				mContext.startActivity(intent);
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, MyService.class);
				i.setAction(MyService.BACK_LOCK_ACTION);
				mContext.startService(i);
			}
		});
	}
}
