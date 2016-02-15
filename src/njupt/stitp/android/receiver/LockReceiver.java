package njupt.stitp.android.receiver;

import java.util.Date;

import njupt.stitp.android.application.MyApplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LockReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_USER_PRESENT.equals(action)
				|| Intent.ACTION_SCREEN_OFF.equals(action)
				|| action.equals("screen_on") || action.equals("screen_off")) {
			MyApplication myApplication = (MyApplication) context
					.getApplicationContext();
			myApplication.setTime(new Date().getTime());
		}
	}
}
