package njupt.stitp.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
//后台检测手机使用情况，若不在不许玩手机的时间玩手机则做出应对
public class UseControlService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
