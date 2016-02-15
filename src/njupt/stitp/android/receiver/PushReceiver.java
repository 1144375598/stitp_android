package njupt.stitp.android.receiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.service.MyService;
import njupt.stitp.android.util.ServerHelper;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;

//本类接收百度云推送的消息
public class PushReceiver extends PushMessageReceiver {
	private String cid;
	private String username;

	/**
	 * 调用 PushManager.startWorkPushManager.startWork PushManager.startWork后，sdk
	 * 将对push server发起绑定请求，这个过程是异步的。绑定请求的结果通过通过 onBind返回。
	 */
	@Override
	public void onBind(Context context, int errorCode, String appId,
			String userId, String channelId, String requestId) { 
		username=((MyApplication)(context.getApplicationContext())).getUsername();
		cid = channelId;
		new Thread(new Runnable() {

			@Override
			public void run() {
				String path = "user/uidAndCid";
				Map<String, String> params = new HashMap<String, String>();
				params.put("user.username", username);
				params.put("user.cid", cid);
				String result = new ServerHelper().getResult(path, params);
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
		username=((MyApplication)(context.getApplicationContext())).getUsername();
		String serviceCode = null;
		Intent i = null;
		JSONObject customJson = new JSONObject().fromString(message);
		String name = customJson.getString("username");
		if (!name.equals(username)) {
			return;
		}
		serviceCode = customJson.getString("serviceCode");
		int codeNum = Integer.valueOf(serviceCode);
		Log.i("透传消息code",serviceCode);
		switch (codeNum) {
		case 1:
			String lockPwd = null;
			lockPwd = customJson.getString("lockPwd");
			if (!lockPwd.equals("null")) {
				new UserDB(context).updateLockPwd(username, lockPwd);
			}
			i=new Intent(context,MyService.class);
			i.setAction(MyService.LOCK_ACTION);
			context.startService(i);
			break;
		case 2:
			i=new Intent(context,MyService.class);
			i.setAction(MyService.UNLOCK_SUCCESS_ACTION);
			context.startService(i);
			break;
		default:
			break;
		}
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
