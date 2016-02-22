package njupt.stitp.android.activity;

import java.util.Calendar;

import njupt.stitp.android.R;
import njupt.stitp.android.db.UseControlDB;
import njupt.stitp.android.model.UseTimeControl;
import njupt.stitp.android.util.MyActivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddUseControlActivity extends ActionBarActivity {
	private TimePicker start;
	private TimePicker end;
	private Button confirm;

	private UseControlDB useControlDB;
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_add_usecontrol);

		getSupportActionBar().setTitle(
				new StringBuffer(getString(R.string.add_usecontrol)));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		start = (TimePicker) findViewById(R.id.add_startPicker);
		end = (TimePicker) findViewById(R.id.add_endPicker);
		confirm = (Button) findViewById(R.id.add_controltimeSet);
		useControlDB = new UseControlDB(this);
		username=getIntent().getExtras().getString("username");

		start.setIs24HourView(true);
		end.setIs24HourView(true);
		Calendar nowTime = Calendar.getInstance();
		start.setCurrentHour(nowTime.get(Calendar.HOUR_OF_DAY));
		start.setCurrentMinute(nowTime.get(Calendar.MINUTE));
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int startHour = start.getCurrentHour();
				int startMin = start.getCurrentMinute();
				int endHour = end.getCurrentHour();
				int endMin = end.getCurrentMinute();
				if (startHour > endHour
						|| (startHour == endHour && startMin > endMin)) {
					Toast.makeText(AddUseControlActivity.this,
							getString(R.string.start_after_end),
							Toast.LENGTH_SHORT).show();
				} else {
					UseTimeControl useTimeControl = new UseTimeControl();
					useTimeControl.setStart(startHour + ":" + startMin);
					useTimeControl.setEnd(endHour + ":" + endMin);
					useTimeControl.setUsername(username);
					useControlDB.addUseTimeControl(username, useTimeControl);
					setResult(RESULT_OK);
					finish();
				}
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}
	@Override
	protected void onDestroy() {
		useControlDB.close();
		super.onDestroy();
	}
}
