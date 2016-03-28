package njupt.stitp.android.service;

import java.util.HashMap;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.activity.AddFriendActivity;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.RelationshipDB;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.model.User;
import njupt.stitp.android.util.JsonUtil;
import njupt.stitp.android.util.ServerHelper;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class AddFriendService extends Service {
	private UserDB userDB;
	private RelationshipDB relationshipDB;

	private String loginName;

	NotificationManager nm;

	public static final String REQUEST_ADD_FRIEND_ACTION = "request add friend";
	public static final String ADD_FRIEND_RESULT_ACTION = "ADD FRIEND RESULT";
	public static final String PARENT_LIST_CHANGE_ACTION = "parent list change";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		userDB = new UserDB(this);
		relationshipDB = new RelationshipDB(this);
		loginName = ((MyApplication) getApplication()).getUsername();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = null;
		String message = null;
		if (intent != null) {
			action = intent.getAction();
		}

		if (TextUtils.equals(action, REQUEST_ADD_FRIEND_ACTION)) {
			String requsetName = intent.getExtras().getString("requestName");
			String relationship = intent.getExtras().getString("relationship");
			if (relationship.equals("parent")) {
				message = requsetName + "请求添加您为家长";
			} else if (relationship.equals("child")) {
				message = requsetName + "请求添加您为孩子";
			}

			Intent intent2 = new Intent(this, AddFriendActivity.class);
			intent2.setAction(AddFriendActivity.REQUEST_ADD_FRIEND_ACTION);
			intent2.putExtra("requestName", requsetName);
			intent2.putExtra("relationship", relationship);
			intent2.putExtra("message", message);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					intent2, PendingIntent.FLAG_UPDATE_CURRENT);

			Builder builder = new Builder(this);
			builder.setContentIntent(pendingIntent);
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setContentTitle("好友申请");
			builder.setContentText(message);
			builder.setTicker("好友申请");
			builder.setAutoCancel(true);
			builder.setDefaults(Notification.DEFAULT_SOUND
					| Notification.DEFAULT_VIBRATE);
			Notification notification = builder.getNotification();
			nm.notify(0, notification);

		} else if (TextUtils.equals(action, ADD_FRIEND_RESULT_ACTION)) {
			String resultCode = intent.getExtras().getString("resultCode");
			String relationship = intent.getExtras().getString("relationship");
			final String friendName = intent.getExtras()
					.getString("friendName");
			if (TextUtils.equals(resultCode, "0")) {
				Builder builder = new Builder(this);
				builder.setSmallIcon(R.drawable.ic_launcher);
				builder.setContentTitle(getString(R.string.add_friend_success));
				builder.setContentText(friendName + "同意了您的申请");
				builder.setTicker(getString(R.string.add_friend_success));
				Notification notification = builder.getNotification();
				builder.setAutoCancel(true);
				builder.setDefaults(Notification.DEFAULT_SOUND
						| Notification.DEFAULT_VIBRATE);

				nm.notify(0, notification);
				Log.i("addFriendService relatinship", relationship);
				if (TextUtils.equals(relationship, "child")) {
					relationshipDB.addRelationship(loginName, friendName);
				} else if (TextUtils.equals(relationship, "parent")) {
					relationshipDB.addRelationship(friendName, loginName);
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						String path = "user/getUser";
						Map<String, String> params = new HashMap<String, String>();
						params.put("user.username", friendName);
						String result = new ServerHelper().getResult(path,
								params);
						User user = JsonUtil.getUser(result);
						if (user != null) {
							userDB.delete();
							userDB.addUser(user, null);
						}

					}
				}).start();
			} else if (TextUtils.equals(resultCode, "1")) {
				Builder builder = new Builder(this);
				builder.setSmallIcon(R.drawable.ic_launcher);
				builder.setContentTitle(getString(R.string.add_friend_success));
				builder.setContentText(friendName + "拒绝了您的申请");
				builder.setTicker(getString(R.string.add_friend_success));
				Notification notification = builder.getNotification();
				builder.setAutoCancel(true);
				builder.setDefaults(Notification.DEFAULT_SOUND
						| Notification.DEFAULT_VIBRATE);

				nm.notify(0, notification);
			}
		} else if (TextUtils.equals(action, PARENT_LIST_CHANGE_ACTION)) {
			String parentName = intent.getExtras().getString("parentName");
			Builder builder = new Builder(this);
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setContentTitle(getString(R.string.add_friend_success));
			builder.setContentText(parentName + "将您从孩子列表中删除");
			builder.setTicker(getString(R.string.add_friend_success));
			Notification notification = builder.getNotification();
			builder.setAutoCancel(true);
			builder.setDefaults(Notification.DEFAULT_SOUND
					| Notification.DEFAULT_VIBRATE);

			nm.notify(0, notification);
			relationshipDB.deleteFriend(parentName, loginName);
			userDB.deleteUser(parentName);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		userDB.close();
		relationshipDB.close();
		super.onDestroy();
	}
}
