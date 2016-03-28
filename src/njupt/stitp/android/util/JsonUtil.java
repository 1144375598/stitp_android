package njupt.stitp.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import njupt.stitp.android.model.APP;
import njupt.stitp.android.model.GeoFencing;
import njupt.stitp.android.model.Track;
import njupt.stitp.android.model.UseTimeControl;
import njupt.stitp.android.model.User;
import android.util.Base64;
import android.util.Log;

public class JsonUtil {
	// json字符串为空返回-1，否则返回状态代码
	public static int getResultCode(String json) {
		Log.i("json",json);
		if (json == null || json.isEmpty())
			return -1;
		JSONObject jsonObject = new JSONObject().fromString(json);
		return jsonObject.getInt("result_code");
	}

	public static List<User> getChild(String json) {
		if (json == null || json.isEmpty())
			return null;
		List<User> list = new ArrayList<User>();
		JSONObject jsonObject = new JSONObject().fromString(json);
		int result_code = jsonObject.getInt("result_code");
		if (result_code == 1)
			return null;
		JSONArray result = jsonObject.getJSONArray("result");
		for (int i = 0; i < result.length(); i++) {
			JSONObject child = result.getJSONObject(i);
			User user = new User();
			user.setChannelId(child.getString("cid"));
			user.setUsername(child.getString("username"));
			user.setTimeOfContinuousListen(child.getInt("timeOfContinuousUse"));
			user.setTimeOfContinuousUse(child.getInt("timeOfContinuousUse"));
			user.setQQ(child.getString("QQ"));
			list.add(user);
		}
		return list;
	}

	public static User getUser(String json) {
		if (json == null || json.isEmpty())
			return null;
		JSONObject jsonObject = new JSONObject().fromString(json);
		int result_code = jsonObject.getInt("result_code");
		if (result_code == 1)
			return null;
		JSONObject result = jsonObject.getJSONObject("result");
		User user = new User();
		user.setChannelId(result.getString("cid"));
		user.setUsername(result.getString("username"));
		user.setTimeOfContinuousListen(result.getInt("timeOfContinuousUse"));
		user.setTimeOfContinuousUse(result.getInt("timeOfContinuousUse"));
		user.setQQ(result.getString("QQ"));
		return user;
	}

	public static Map<String, String> getValidation(String json) {
		if (json == null || json.isEmpty())
			return null;
		JSONObject jsonObject = new JSONObject().fromString(json);
		int result_code = jsonObject.getInt("result_code");
		if (result_code == 1)
			return null;
		String question = jsonObject.getString("question");
		String answer = jsonObject.getString("answer");
		Map<String, String> validation = new HashMap<String, String>();
		validation.put("question", question);
		validation.put("answer", answer);
		return validation;
	}

	public static List<Track> getTracks(String json) {
		if (json == null || json.isEmpty())
			return null;
		List<Track> list = new ArrayList<Track>();
		JSONObject jsonObject = new JSONObject().fromString(json);
		int result_code = jsonObject.getInt("result_code");
		if (result_code == 1 || result_code == 2)
			return null;
		JSONArray result = jsonObject.getJSONArray("result");
		for (int i = 0; i < result.length(); i++) {
			JSONObject jsonTrack = result.getJSONObject(i);
			Track track = new Track();
			track.setAddress(jsonTrack.getString("address"));
			track.setAddTime(jsonTrack.getString("addTime"));
			track.setLatitude(jsonTrack.getDouble("latitude"));
			track.setLongitude(jsonTrack.getDouble("longitude"));
			track.setUsername(jsonTrack.getString("username"));
			track.setStayTime(jsonTrack.getInt("stayTime"));
			list.add(track);
		}
		return list;
	}

	public static List<APP> getApps(String json) {
		if (json == null || json.isEmpty())
			return null;
		List<APP> list = new ArrayList<APP>();
		JSONObject jsonObject = new JSONObject().fromString(json);
		int result_code = jsonObject.getInt("result_code");
		if (result_code == 1 || result_code == 2)
			return null;
		JSONArray result = jsonObject.getJSONArray("result");
		for (int i = 0; i < result.length(); i++) {
			JSONObject jsonApp = result.getJSONObject(i);
			APP app = new APP();
			app.setAddDate(jsonApp.getString("addDate"));
			app.setAppName(jsonApp.getString("appName"));
			app.setAppUseTime(jsonApp.getInt("appUseTime"));
			app.setUsername(jsonApp.getString("username"));
			app.setIcon(Base64.decode(jsonApp.getString("icon"),
					Base64.URL_SAFE | Base64.NO_WRAP));
			list.add(app);
		}
		return list;
	}

	public static List<UseTimeControl> getUseControl(String json) {
		if (json == null || json.isEmpty())
			return null;
		List<UseTimeControl> list = new ArrayList<UseTimeControl>();
		JSONObject jsonObject = new JSONObject().fromString(json);
		int result_code = jsonObject.getInt("result_code");
		if (result_code == 1)
			return null;
		JSONArray result = jsonObject.getJSONArray("result");
		for (int i = 0; i < result.length(); i++) {
			JSONObject jsonTime = result.getJSONObject(i);
			UseTimeControl useTimeControl = new UseTimeControl();
			useTimeControl.setUsername(jsonTime.getString("username"));
			useTimeControl.setStart(jsonTime.getString("start"));
			useTimeControl.setEnd(jsonTime.getString("end"));
			list.add(useTimeControl);
		}
		return list;
	}

	public static GeoFencing getGeo(String json) {
		if (json == null || json.isEmpty())
			return null;
		JSONObject jsonObject = new JSONObject().fromString(json);
		int result_code = jsonObject.getInt("result_code");
		if (result_code == 1)
			return null;
		JSONArray result = jsonObject.getJSONArray("result");
		JSONObject jsonGeo = result.getJSONObject(0);
		GeoFencing geoFencing = new GeoFencing();
		geoFencing.setAddress(jsonGeo.getString("address"));
		geoFencing.setDistance(jsonGeo.getDouble("distance"));
		geoFencing.setLatitude(jsonGeo.getDouble("latitude"));
		geoFencing.setLongitude(jsonGeo.getDouble("longitude"));
		geoFencing.setUsername(jsonGeo.getString("username"));
		return geoFencing;
	}
}
