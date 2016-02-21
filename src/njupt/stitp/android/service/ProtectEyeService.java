package njupt.stitp.android.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import njupt.stitp.android.R;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.OptionDB;
import njupt.stitp.android.db.UseControlDB;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.model.UseTimeControl;
import njupt.stitp.android.util.JsonUtil;
import njupt.stitp.android.util.ServerHelper;
import android.R.integer;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

//后台检测手机连续使用时间和颠簸
public class ProtectEyeService extends Service {
	private Timer timer;
	private Timer timer2;

	private UserDB userDB;
	private UseControlDB useControlDB;
	private OptionDB optionDB;
	private String username;
	private Handler handler;

	private SensorManager sensorManager;
	private SensorEventListener mSensorEventListener;
	private Vibrator vibrator;

	private static final int USE_TIME_OUT = 1;
	private static final int SENSOR_SHAKE = 2;
	private static final int RESET_TIMER = 3;

	public static final String OPEN_CONTINUE_USE_ACTION = "continue use open";
	public static final String CLOSE_CONTINUE_USE_ACTION = "continue use close";
	public static final String OPEN_BUMP_REMIND_ACTION = "open bump remind";
	public static final String CLOSE_BUMP_REMIND_ACTION = "close bump remind";
	public static final String USE_CONTROL = "use control";
	public static final String USE_CONTROL_CHANGE = "use control change";

	@Override
	public void onCreate() {
		super.onCreate();

		optionDB = new OptionDB(this);
		userDB = new UserDB(this);
		useControlDB = new UseControlDB(this);

		username = ((MyApplication) getApplication()).getUsername();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); // 首先得到传感器管理器对象
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		setControlTimer();
		mSensorEventListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				// 传感器信息改变时执行该方法
				float[] values = event.values;
				float x = values[0];
				float y = values[1];
				float z = values[2];
				int medumValue = 19;
				if (x > medumValue || x < -medumValue || y > medumValue
						|| y < -medumValue || z > medumValue || z < -medumValue) {
					vibrator.vibrate(200);
					Message msg = new Message();
					msg.what = SENSOR_SHAKE;
					handler.sendMessage(msg);
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}

		};

		handler = new Handler() {
			Intent intent = null;

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case USE_TIME_OUT:
					Toast.makeText(ProtectEyeService.this,
							getString(R.string.time_out), Toast.LENGTH_SHORT)
							.show();
					intent = new Intent(ProtectEyeService.this,
							LockService.class);
					intent.setAction(LockService.LOCK_ACTION);
					startService(intent);
					break;
				case RESET_TIMER:
					intent = new Intent(ProtectEyeService.this,
							LockService.class);
					intent.setAction(LockService.UNLOCK_SUCCESS_ACTION);
					Log.i("unlock", "protect start unlock");
					startService(intent);
					setTimer();
					break;
				case SENSOR_SHAKE:
					Log.i("传感器监听", "监测到晃动，执行操作");
					showDialog();
					Toast.makeText(ProtectEyeService.this,
							getString(R.string.too_bump_to_use_phone),
							Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();

		if (TextUtils.equals(action, OPEN_CONTINUE_USE_ACTION)) {
			setTimer();
		} else if (TextUtils.equals(action, CLOSE_CONTINUE_USE_ACTION)) {
			if (timer != null) {
				timer.cancel();
			}
		} else if (TextUtils.equals(action, OPEN_BUMP_REMIND_ACTION)) {
			sensorManager.registerListener(mSensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					sensorManager.SENSOR_DELAY_NORMAL);
		} else if (TextUtils.equals(action, CLOSE_BUMP_REMIND_ACTION)) {
			if (sensorManager != null) {
				sensorManager.unregisterListener(mSensorEventListener);// 解绑定Listener
				sensorManager = null;
			}
		} else if (TextUtils.equals(action, USE_CONTROL)) {
			setControlTimer();
		} else if (TextUtils.equals(action, USE_CONTROL_CHANGE)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					String path = "downloadInfo/useTimeControlInfo";
					Map<String, String> params = new HashMap<String, String>();
					params.put("user.username", username);
					String result = new ServerHelper().getResult(path, params);
					List<UseTimeControl> list = JsonUtil.getUseControl(result);
					useControlDB.deleteAndAdd(username, list);
					setControlTimer();
				}
			}).start();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void setControlTimer() {
		List<UseTimeControl> list = useControlDB.getUseTimeControl(username);
		if (list.size() == 0) {
			return;
		}
		if (timer2 != null) {
			timer2.cancel();
			timer2 = null;
		}
		timer2 = new Timer();
		for (UseTimeControl useTimeControl : list) {
			Calendar startDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();
			Date nowDate = new Date();
			String[] start = useTimeControl.getStart().split(":");
			String[] end = useTimeControl.getEnd().split(":");
			startDate.set(Calendar.HOUR_OF_DAY, Integer.valueOf(start[0]));
			startDate.set(Calendar.MINUTE, Integer.valueOf(start[1]));
			endDate.set(Calendar.HOUR_OF_DAY, Integer.valueOf(end[0]));
			endDate.set(Calendar.MINUTE, Integer.valueOf(end[1]));
			if (nowDate.getTime() > endDate.getTimeInMillis()) {
				startDate.add(Calendar.DAY_OF_MONTH, 1);
				endDate.add(Calendar.DAY_OF_MONTH, 1);
			}
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					Message msg = new Message();
					msg.what = USE_TIME_OUT;
					handler.sendMessage(msg);
				}
			};
			TimerTask task2 = new TimerTask() {

				@Override
				public void run() {
					Message msg = new Message();
					msg.what = RESET_TIMER;
					handler.sendMessage(msg);
				}
			};
			timer2.scheduleAtFixedRate(task,
					new Date(startDate.getTimeInMillis()), 24 * 60 * 60 * 1000);
			timer2.scheduleAtFixedRate(task2,
					new Date(endDate.getTimeInMillis()), 24 * 60 * 60 * 1000);
		}
	}

	public void setTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timer = new Timer();
		if (optionDB.getContinueUse(username) == 0) {
			return;
		}
		int useTime = userDB.getUser(username).getTimeOfContinuousUse();
		if (useTime == 0) {
			return;
		}
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Message msg;
				msg = new Message();
				msg.what = USE_TIME_OUT;
				handler.sendMessage(msg);
				try {
					wait(15 * 60 * 1000); // 锁屏15分钟
				} catch (InterruptedException e) {
					e.printStackTrace();
					msg = new Message();
					msg.what = RESET_TIMER;
					handler.sendMessage(msg);
				}
				msg = new Message();
				msg.what = RESET_TIMER;
				handler.sendMessage(msg);
			}
		};
		timer.schedule(task, useTime * 60 * 1000);
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		if (sensorManager != null) {
			sensorManager.unregisterListener(mSensorEventListener);// 解绑定Listener
			sensorManager = null;
		}
		if (userDB != null) {
			userDB.close();
		}
		if (optionDB != null) {
			optionDB.close();
		}
		if (useControlDB != null) {
			useControlDB.close();
		}
		super.onDestroy();
	}

	public void showDialog() {

		Vibrator vibratorm = (Vibrator) getApplication().getSystemService(
				Service.VIBRATOR_SERVICE);

		vibratorm.vibrate(new long[] { 100, 50, 100, 50, 100, 50, 100, 100, 50,
				100, 50, 100, 50, 100, 50, 100, 50, 100, 50, 100, 100, 50, 100,
				50, 100, 50, 100, 100, 50, 100, 50, 100, 50, 100 }, -1);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
