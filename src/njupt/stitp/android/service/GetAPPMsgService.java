package njupt.stitp.android.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import njupt.stitp.android.db.AppDB;
import njupt.stitp.android.model.APP;
import njupt.stitp.android.util.IconUtil;
import njupt.stitp.android.util.SPHelper;
import njupt.stitp.android.util.ServerHelper;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

public class GetAPPMsgService extends Service {

	private ActivityManager am = null;
	private PackageManager pm = null;
	private PackageInfo info = null;
	private AppDB appDB = null;
	private String username;
	private int count;
	private String path;
	private ServerHelper sHelper;
	private Drawable icon = null;
	// 进程列表
	private List<RunningAppProcessInfo> processInfos = null;
	private List<APP> runningApps = null;
	private List<String> applicationName;
	private List<AndroidAppProcess> processInfos2;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		count = 1;
		path = "uploadInfo/appInfo";
		sHelper = new ServerHelper();
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		pm = getApplicationContext().getPackageManager();
		runningApps = new ArrayList<APP>();
		applicationName = new ArrayList<String>();
		username = new SPHelper().getInfo(getApplicationContext(), "userInfo",
				"username");
		appDB = new AppDB(getApplicationContext());
		// 如果当前安卓系统小于5.0
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						runningApps.clear();
						applicationName.clear();
						// 获取运行进程
						processInfos = am.getRunningAppProcesses();
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
										icon = pm
												.getApplicationIcon(info.applicationInfo);
										APP app = new APP();
										app.setAppName(name);
										app.setIcon(IconUtil
												.drawableToByte(icon));
										runningApps.add(app);
									}
								}
							} catch (NameNotFoundException e) {
								e.printStackTrace();
							}

						}
						// 将当前运行的应用存入数据库
						appDB.saveMessage(username, runningApps);
						count++;
						if (count >= 10) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									List<APP> apps = appDB.getMessage(username,
											new Date());
									if (apps != null && apps.size() > 0) {
										sHelper.uploadTrackAndAPP(path, null,
												apps);
									}
								}
							}).start();
						}
						// 获取一次当前进程后沉睡5分钟
						try {
							Thread.sleep(5*60*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						runningApps.clear();
						applicationName.clear();
						// 获取运行进程
						processInfos2 = ProcessManager.getRunningAppProcesses();
						for (AndroidAppProcess processInfo : processInfos2) {
							try {
								String packname = processInfo.name;
								ApplicationInfo applicationInfo = pm
										.getApplicationInfo(packname, 0);
								// 判断是否为系统进程，若为非系统进程则获取应用名
								if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
									String name = applicationInfo.loadLabel(pm)
											.toString();
									if (!applicationName.contains(name)) {
										applicationName.add(name);
										icon = applicationInfo.loadIcon(pm);
										APP app = new APP();
										app.setAppName(name);
										app.setIcon(IconUtil
												.drawableToByte(icon));
										runningApps.add(app);
									}
								}
							} catch (NameNotFoundException e) {
							}

						}
						// 将当前运行的应用存入数据库
						appDB.saveMessage(username, runningApps);
						count++;
						if (count >= 10) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									List<APP> apps = appDB.getMessage(username,
											new Date());
									if (apps != null && apps.size() > 0) {
										sHelper.uploadTrackAndAPP(path, null,
												apps);
									}
								}
							}).start();
						}
						// 获取一次当前进程后沉睡5分钟
						try {
							Thread.sleep(5*60*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();

		}
	}

	@Override
	public void onDestroy() {
		if (runningApps.size() > 0) {
			appDB.saveMessage(username, runningApps);
		}
		appDB.close();
		super.onDestroy();
	}
}
