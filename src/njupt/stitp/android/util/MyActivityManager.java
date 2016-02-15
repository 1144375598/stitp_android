package njupt.stitp.android.util;

import java.util.Arrays;
import java.util.LinkedList;

import android.app.Activity;

public class MyActivityManager {
	private LinkedList<Activity> activityLinkedList = new LinkedList<Activity>();

	private MyActivityManager() {
	}

	private static MyActivityManager instance;

	public static MyActivityManager getInstance() {
		if (null == instance) {
			instance = new MyActivityManager();
		}
		return instance;
	}

	// 向list中添加Activity
	public MyActivityManager addActivity(Activity activity) {
		activityLinkedList.add(activity);
		return instance;
	}

	// 结束特定的Activity(s)
	public MyActivityManager finshActivities(
			Class<? extends Activity>... activityClasses) {
		for (Activity activity : activityLinkedList) {
			if (Arrays.asList(activityClasses).contains(activity.getClass())) {
				activity.finish();
			}
		}
		return instance;
	}

	// 结束所有的Activities
	public MyActivityManager finshAllActivities() {
		for (Activity activity : activityLinkedList) {
			activity.finish();
		}
		return instance;
	}
}
