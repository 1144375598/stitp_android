package njupt.stitp.android.view;

import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import njupt.stitp.android.R;
import njupt.stitp.android.service.LockService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LockScreenView extends LinearLayout {
	private static final String SYSTEM = "/system/fonts/";
	private static final String SYSTEM_FONT_TIME_BACKGROUND = SYSTEM
			+ "AndroidClock.ttf";
	private final static String M12 = "h:mm";
	private final static String M24 = "kk:mm";

	private static Context mContext;
	private View rootView;

	private Button btnUnlock;
	private Button btnDial;
	private TextView mDateView;
	private TextView mTimeView;

	private String mDateFormat;
	private String mFormat;
	public ContentObserver mFormatChangeObserver;
	private AmPm mAmPm;
	private Calendar mCalendar;
	public BroadcastReceiver mIntentReceiver;
	private final Handler mHandler = new Handler();
	private static final Typeface sBackgroundFont;
	static {
		
		sBackgroundFont = Typeface.createFromFile(SYSTEM_FONT_TIME_BACKGROUND);
	}

	public LockScreenView(Context context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rootView = inflater.inflate(R.layout.view_lockscreen, this);
		btnUnlock = (Button) rootView.findViewById(R.id.btn_unlock_screen);
		btnDial=(Button) rootView.findViewById(R.id.dial);
		mDateFormat = "yyyy-MM-dd EEE";
		mDateView = (TextView) rootView.findViewById(R.id.date);
		mTimeView = (TextView) rootView.findViewById(R.id.time);
		mTimeView.setTypeface(sBackgroundFont);

		mAmPm = new AmPm(null);
		
		mCalendar = Calendar.getInstance();

		setDateFormat();
		
		registerComponent();
		refreshDate();
		if (btnUnlock != null)
			System.out.println("found the button");
		btnUnlock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, LockService.class);
				i.setAction(LockService.UNLOCK_ACTION);
				mContext.startService(i);
			}
		});
		btnDial.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, LockService.class);
				i.setAction(LockService.DIAL_ACTION);
				mContext.startService(i);
				
			}
		});
	}

	private void refreshDate() {
		if (mDateView != null) {
			
			mDateView.setText(DateFormat.format(mDateFormat, new Date()));
		}
	}

	class AmPm {
		private TextView mAmPmTextView;
		private String mAmString, mPmString;

		AmPm(Typeface tf) {
			mAmPmTextView = (TextView) findViewById(R.id.am_pm);
			if (mAmPmTextView != null && tf != null) {
				
				mAmPmTextView.setTypeface(tf);
			}

			
			String[] ampm = new DateFormatSymbols().getAmPmStrings();
			mAmString = ampm[0];
			mPmString = ampm[1];
		}

		void setShowAmPm(boolean show) {
			if (mAmPmTextView != null) {
				mAmPmTextView.setVisibility(show ? View.VISIBLE : View.GONE);
			}
		}

		void setIsMorning(boolean isMorning) {
			if (mAmPmTextView != null) {
				mAmPmTextView.setText(isMorning ? mAmString : mPmString);
			}
		}
	}

	private static class TimeChangedReceiver extends BroadcastReceiver {
		private WeakReference<LockScreenView> mStatusViewManager;

		public TimeChangedReceiver(LockScreenView status) {
			mStatusViewManager = new WeakReference<LockScreenView>(status);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			final boolean timezoneChanged = intent.getAction().equals(
					Intent.ACTION_TIMEZONE_CHANGED);
			final LockScreenView status = mStatusViewManager.get();
			if (status != null) {
				status.mHandler.post(new Runnable() {
					public void run() {
						if (timezoneChanged) {
							status.mCalendar = Calendar.getInstance();
						}
						status.updateTime();
					}
				});
			} else {
				try {
					mContext.unregisterReceiver(this);
				} catch (RuntimeException e) {
					// Shouldn't happen
				}
			}
		}
	}

	
	private static class FormatChangeObserver extends ContentObserver {
		private WeakReference<LockScreenView> mStatusViewManager;

	
		public FormatChangeObserver(LockScreenView status) {
			super(new Handler());
		
			mStatusViewManager = new WeakReference<LockScreenView>(status);
		}

		@Override
		public void onChange(boolean selfChange) {
			LockScreenView mStatusManager = mStatusViewManager.get();
			if (mStatusManager != null) {
				mStatusManager.setDateFormat();
				mStatusManager.updateTime();
			} else {
				try {
					mContext.getContentResolver().unregisterContentObserver(
							this);
				} catch (RuntimeException e) {
					// Shouldn't happen
				}
			}
		}
	}


	private void updateTime() {
		mCalendar.setTimeInMillis(System.currentTimeMillis());

		CharSequence newTime = DateFormat.format(mFormat, mCalendar);
		mTimeView.setText(newTime);
		mAmPm.setIsMorning(mCalendar.get(Calendar.AM_PM) == 0);
	}


	private void setDateFormat() {
		mFormat = android.text.format.DateFormat.is24HourFormat(mContext) ? M24
				: M12;
		mAmPm.setShowAmPm(mFormat.equals(M12));
	}

	public void registerComponent() {
		
		Log.d("MainActivity", "registerComponent()");

		if (mIntentReceiver == null) {
			mIntentReceiver = new TimeChangedReceiver(this);
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			mContext.registerReceiver(mIntentReceiver, filter);
		}
		 if (mFormatChangeObserver == null) {
	            mFormatChangeObserver = new FormatChangeObserver(this);
	            mContext.getContentResolver().registerContentObserver(
	                    Settings.System.CONTENT_URI, true, mFormatChangeObserver);
	        }
		updateTime();
	}

	public void unregisterComponent() {
		// TODO Auto-generated method stub
		Log.d("MainActivity", "unregisterComponent()");
		if (mIntentReceiver != null) {
			mContext.unregisterReceiver(mIntentReceiver);
		}
		if (mFormatChangeObserver != null) {
        	mContext.getContentResolver().unregisterContentObserver(
                    mFormatChangeObserver);
        }
        mFormatChangeObserver = null;
		mIntentReceiver = null;
	}
}
