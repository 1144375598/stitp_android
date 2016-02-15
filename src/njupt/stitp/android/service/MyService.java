package njupt.stitp.android.service;

import njupt.stitp.android.view.LockScreenView;
import njupt.stitp.android.view.PhoneView;
import njupt.stitp.android.view.UnlockView;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MyService extends Service {
	public static final String LOCK_ACTION = "lock";
	public static final String UNLOCK_ACTION = "to_unlock";
	public static final String DIAL_ACTION = "dial";
	public static final String UNLOCK_SUCCESS_ACTION = "unlock_success";
	public static final String BACK_LOCK_ACTION = "back_to_lock";

	private Context mContext;
	private WindowManager mWinMng;
	private LockScreenView screenView;
	private UnlockView unlockView;
	private PhoneView callView;
	TelephonyManager tpManager;
	PhoneStateListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		mWinMng = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);

		tpManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		listener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (state != 0) {
					removeLock();
					removeCall();
					removeUnlock();
				} else {
					addLock();
				}
			}
		};
		tpManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	@Override
	public void onDestroy() {		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		Intent i=null;
		if (TextUtils.equals(action, LOCK_ACTION)) {
			Log.i("myService","lock screen");
			addLock();
			i=new Intent("screen_off");
			sendBroadcast(i);
		}

		else if (TextUtils.equals(action, UNLOCK_ACTION)) {
			removeLock();
			addUnlock();
		} else if (TextUtils.equals(action, DIAL_ACTION)) {
			removeLock();
			addCall();
		} else if (TextUtils.equals(action, UNLOCK_SUCCESS_ACTION)) {
			Log.i("myService","unlock screen");
			removeUnlock();
			i=new Intent("screen_on");
			sendBroadcast(i);
			stopSelf();
		} else if (TextUtils.equals(action, BACK_LOCK_ACTION)) {
			removeUnlock();
			removeCall();
			addLock();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void addLock() {
		if (screenView == null) {
			screenView = new LockScreenView(mContext);

			LayoutParams param = new LayoutParams();
			param.type = LayoutParams.TYPE_SYSTEM_ALERT;
			param.format = PixelFormat.RGBA_8888;
			param.width = LayoutParams.MATCH_PARENT;
			param.height = LayoutParams.MATCH_PARENT;
			mWinMng.addView(screenView, param);
		}
	}

	public void removeLock() {
		if (screenView != null) {
			mWinMng.removeView(screenView);
			screenView = null;
		}
	}

	public void addUnlock() {
		if (unlockView == null) {
			unlockView = new UnlockView(mContext);

			LayoutParams param = new LayoutParams();
			param.type = LayoutParams.TYPE_SYSTEM_ALERT;
			param.format = PixelFormat.RGBA_8888;
			param.width = LayoutParams.MATCH_PARENT;
			param.height = LayoutParams.MATCH_PARENT;

			mWinMng.addView(unlockView, param);
		}
	}

	public void removeUnlock() {
		if (unlockView != null) {
			mWinMng.removeView(unlockView);
			unlockView = null;
		}
	}

	public void addCall() {
		if (callView == null) {
			callView = new PhoneView(mContext);

			LayoutParams param = new LayoutParams();
			param.type = LayoutParams.TYPE_SYSTEM_ALERT;
			param.format = PixelFormat.RGBA_8888;
			param.width = LayoutParams.MATCH_PARENT;
			param.height = LayoutParams.MATCH_PARENT;
			mWinMng.addView(callView, param);
		}
	}

	public void removeCall() {
		if (callView != null) {
			mWinMng.removeView(callView);
			callView = null;
		}
	}
}
