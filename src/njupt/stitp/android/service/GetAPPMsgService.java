package njupt.stitp.android.service;

import java.util.ArrayList;
import java.util.List;

import njupt.stitp.android.db.AppDB;
import njupt.stitp.android.util.SPHelper;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.util.Log;

public class GetAPPMsgService extends Service {

	private ActivityManager am = null;
	private PackageManager pm = null;
	private PackageInfo info = null;
	private AppDB appDB = null;
	private String username;
	// 进程列表
	private List<RunningAppProcessInfo> processInfos = null;
	private List<String> applicationName = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		pm = getApplicationContext().getPackageManager();
		processInfos = new ArrayList<RunningAppProcessInfo>();
		applicationName = new ArrayList<String>();
		username = new SPHelper().getInfo(getApplicationContext(), "userInfo",
				"username");

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					applicationName.clear();
					// 获取运行进程
					processInfos = am.getRunningAppProcesses();
				/*	Log.d("process number",
							new Integer(processInfos.size()).toString());*/
					for (int i = 0; i < processInfos.size(); i++) {
						try {
							// 取出一个进程
							info = pm.getPackageInfo(
									processInfos.get(i).processName, 0);
							// 判断是否为系统进程，若为非系统进程则获取应用名
							if ((info.applicationInfo.flags & info.applicationInfo.FLAG_SYSTEM) <= 0) {
								String name = (String) pm
										.getApplicationLabel(info.applicationInfo);
								if (!applicationName.contains(name)) {
									applicationName.add(name);
									Log.d("appName", name);
								}
							}
						} catch (NameNotFoundException e) {
							e.printStackTrace();
						}
					}
					// 将当前运行的应用存入数据库
					appDB.saveMessage(username, applicationName);
					// 获取一次当前进程后沉睡5分钟
					try {
						Thread.sleep(60 * 5 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
