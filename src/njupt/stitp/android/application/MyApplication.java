package njupt.stitp.android.application;

import java.util.Date;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

public class MyApplication extends Application {
	private long time;
	private String username;
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());
		time=new Date().getTime();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
