package njupt.stitp.android.receiver;

import java.util.Date;

import njupt.stitp.android.R;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.OptionDB;
import njupt.stitp.android.service.ProtectEyeService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i("收到广播",action);
		if (Intent.ACTION_USER_PRESENT.equals(action)
				|| Intent.ACTION_SCREEN_OFF.equals(action)
				|| action.equals("screen_on") || action.equals("screen_off")) {
			MyApplication myApplication = (MyApplication) context
					.getApplicationContext();
			myApplication.setTime(new Date().getTime());
		}
		
		if(action.equals("screen_off")){
			Intent i=new Intent(context,ProtectEyeService.class);
			i.setAction(ProtectEyeService.CLOSE_CONTINUE_USE_ACTION);
			context.startService(i);
		}
		
		if(Intent.ACTION_USER_PRESENT.equals(action)||action.equals("screen_on")){
			Intent i=new Intent(context,ProtectEyeService.class);
			i.setAction(ProtectEyeService.OPEN_CONTINUE_USE_ACTION);
			context.startService(i);
		}
		
		if (action.equals("android.media.VOLUME_CHANGED_ACTION")||action.equals("music_volume_changed")) {			
			String username = ((MyApplication) (context.getApplicationContext()))
					.getUsername();
			OptionDB optionDB=new OptionDB(context);
			if (optionDB.getVoiceControl(username) == 1) {
				AudioManager audioManager = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
				int currVolume = audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);// 当前的媒体音量
				Log.i("current volume",currVolume+"");
				int max = audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
				if (currVolume > max*0.6) {
					Toast.makeText(context,
							context.getString(R.string.voice_high),
							Toast.LENGTH_SHORT).show();
					
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(max*0.6),
							AudioManager.FLAG_SHOW_UI);
				}
				optionDB.close();
			}

		}
	}
}
