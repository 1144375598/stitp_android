package njupt.stitp.android.receiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.OptionDB;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.service.AddFriendService;
import njupt.stitp.android.service.LockService;
import njupt.stitp.android.service.ProtectEyeService;
import njupt.stitp.android.service.TrackService;
import njupt.stitp.android.util.JudgeState;
import njupt.stitp.android.util.ServerHelper;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;

//本类接收百度云推送的消息
public class PushReceiver extends PushMessageReceiver {
	private String cid;
	private String username;
	public static final int MODIFY_LOCK_PWD = 0;
	public static final int LOCK_SCREEN = 1;
	public static final int UNLOCK_SCREEN = 2;
	public static final int OPEN_VOICE_CONTROL = 3;
	public static final int CLOSE_VOICE_CONTROL = 4;
	public static final int OPEN_BUMP_REMIND = 5;
	public static final int CLOSE_BUMP_REMIND = 6;
	public static final int OPEN_CONTINUE_USE = 7;
	public static final int CLOSE_CONTINUE_USE = 8;
	public static final int MODIFY_CONTINUE_USE = 9;
	public static final int MODIFY_LOCK_PLAN = 10;
	public static final int REQUEST_ADD_FRIEND = 11;
	public static final int ADD_FRIEND_RESULT = 12;
	public static final int PARENT_LIST_CHANGE = 13;
	public static final int OUT_OF_RANGE = 14;
	public static final int GEO_CHANGE = 15;

	/**
	 * 调用 PushManager.startWorkPushManager.startWork PushManager.startWork后，sdk
	 * 将对push server发起绑定请求，这个过程是异步的。绑定请求的结果通过通过 onBind返回。
	 */
	@Override
	public void onBind(Context context, int errorCode, String appId,
			String userId, String channelId, String requestId) {
		username = ((MyApplication) (context.getApplicationContext()))
				.getUsername();
		cid = channelId;
		Log.i("onbind", "errorCode:" + errorCode);
		if (!JudgeState.isNetworkConnected(context)) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {

				String path = "user/uidAndCid";
				Map<String, String> params = new HashMap<String, String>();
				params.put("user.username", username);
				params.put("user.cid", cid);
				new ServerHelper().getResult(path, params);
			}
		}).start();

	}

	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> successTags, List<String> failTags, String requsetId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	/**
	 * 接收透传消息的函数。
	 *
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内容,为空或者json字符串
	 */
	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		Log.i("透传消息", message);
		OptionDB optionDB = new OptionDB(context);
		UserDB userDB = new UserDB(context);
		username = ((MyApplication) (context.getApplicationContext()))
				.getUsername();
		String serviceCode = null;
		Intent i = null;
		JSONObject customJson = new JSONObject().fromString(message);
		String name = customJson.getString("username");
		if (!name.equals(username)) {
			return;
		}
		serviceCode = customJson.getString("serviceCode");
		int codeNum = Integer.valueOf(serviceCode);
		switch (codeNum) {
		case MODIFY_LOCK_PWD:
			String lockPwd0 = null;
			lockPwd0 = customJson.getString("lockPwd");
			if (!lockPwd0.equals("null")) {
				userDB.updateLockPwd(username, lockPwd0);
			}
			break;
		case LOCK_SCREEN:
			String lockPwd = null;
			lockPwd = customJson.getString("lockPwd");
			if (!lockPwd.equals("null")) {
				userDB.updateLockPwd(username, lockPwd);
			}
			i = new Intent(context, LockService.class);
			i.setAction(LockService.ONE_KEY_LOCK_ACTION);
			context.startService(i);
			optionDB.setLockScreen(username, 1);
			break;
		case UNLOCK_SCREEN:
			i = new Intent(context, LockService.class);
			i.setAction(LockService.UNLOCK_SUCCESS_ACTION);
			context.startService(i);
			optionDB.setLockScreen(username, 0);
			break;
		case OPEN_VOICE_CONTROL:
			optionDB.setVoiceControl(username, 1);
			i = new Intent("music_volume_changed");
			context.sendBroadcast(i);
			break;
		case CLOSE_VOICE_CONTROL:
			optionDB.setVoiceControl(username, 0);
			break;
		case OPEN_BUMP_REMIND:
			i = new Intent(context, ProtectEyeService.class);
			i.setAction(ProtectEyeService.OPEN_BUMP_REMIND_ACTION);
			context.startService(i);
			optionDB.setBumpRemind(username, 1);
			break;
		case CLOSE_BUMP_REMIND:
			i = new Intent(context, ProtectEyeService.class);
			i.setAction(ProtectEyeService.CLOSE_BUMP_REMIND_ACTION);
			context.startService(i);
			optionDB.setBumpRemind(username, 0);
			break;
		case OPEN_CONTINUE_USE:
			i = new Intent(context, ProtectEyeService.class);
			i.setAction(ProtectEyeService.OPEN_CONTINUE_USE_ACTION);
			context.startService(i);
			optionDB.setContinueUse(username, 1);
			break;
		case CLOSE_CONTINUE_USE:
			i = new Intent(context, ProtectEyeService.class);
			i.setAction(ProtectEyeService.CLOSE_CONTINUE_USE_ACTION);
			context.startService(i);
			optionDB.setContinueUse(username, 0);
			break;
		case MODIFY_CONTINUE_USE:
			int useTime = customJson.getInt("continueUseTime");
			userDB.updateContinueUse(username, useTime);
			i = new Intent(context, ProtectEyeService.class);
			i.setAction(ProtectEyeService.OPEN_CONTINUE_USE_ACTION);
			context.startService(i);
			break;
		case MODIFY_LOCK_PLAN:
			i = new Intent(context, ProtectEyeService.class);
			i.setAction(ProtectEyeService.USE_CONTROL_CHANGE);
			context.startService(i);
			break;
		case REQUEST_ADD_FRIEND:
			String requestName = customJson.getString("requestName");
			String relationship = customJson.getString("relationship");
			i = new Intent(context, AddFriendService.class);
			i.setAction(AddFriendService.REQUEST_ADD_FRIEND_ACTION);
			i.putExtra("requestName", requestName);
			i.putExtra("relationship", relationship);
			context.startService(i);
			break;
		case ADD_FRIEND_RESULT:
			String friendName = customJson.getString("friendName");
			String resultCode = customJson.getString("resultCode");
			String relationship2 = customJson.getString("relationship");
			i = new Intent(context, AddFriendService.class);
			i.setAction(AddFriendService.ADD_FRIEND_RESULT_ACTION);
			i.putExtra("friendName", friendName);
			i.putExtra("relationship", relationship2);
			i.putExtra("resultCode", resultCode);
			context.startService(i);
			break;
		case PARENT_LIST_CHANGE:
			String parentName = customJson.getString("parentName");
			i = new Intent(context, AddFriendService.class);
			i.setAction(AddFriendService.PARENT_LIST_CHANGE_ACTION);
			i.putExtra("parentName", parentName);
			context.startService(i);
			break;
		case OUT_OF_RANGE:
			String childName = customJson.getString("childName");
			i = new Intent(context, TrackService.class);
			i.setAction(TrackService.OUT_OF_RANGE_ACTION);
			i.putExtra("username", childName);
			context.startService(i);
			break;
		case GEO_CHANGE:
			String changeName = customJson.getString("changeName");
			i = new Intent(context, TrackService.class);
			i.setAction(TrackService.DOWNLOAD_GEO_ACTION);
			i.putExtra("username", changeName);
			context.startService(i);
			break;
		default:
			break;
		}
		optionDB.close();
		userDB.close();
	}

	@Override
	public void onNotificationArrived(Context arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotificationClicked(Context arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

}
