package njupt.stitp.android.util;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPHelper {
	private SharedPreferences preferences;
	private Editor editor;

	public void saveInfo(Context context, String preferencesName,
			Map<String, String> params) {
		preferences = context.getSharedPreferences(preferencesName,
				context.MODE_PRIVATE);
		editor = preferences.edit();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			editor.putString(entry.getKey(), entry.getValue());
		}
		editor.commit();
	}

	public String getInfo(Context context, String preferencesName, String key) {
		preferences = context.getSharedPreferences(preferencesName,
				context.MODE_PRIVATE);
		return preferences.getString(key, "");

	}
}
