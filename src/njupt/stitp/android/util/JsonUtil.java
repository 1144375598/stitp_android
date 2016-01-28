package njupt.stitp.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import njupt.stitp.android.model.Track;
import njupt.stitp.android.model.User;

public class JsonUtil {
	//json字符串为空返回-1，否则返回状态代码
	public static int LoginAndRegister(String json) {
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
			user.setChannel_id(child.getString("cid"));
			user.setUsername(child.getString("username"));
			user.setTimeOfContinuousListen(child.getInt("timeOfContinuousUse"));
			user.setTimeOfContinuousUse(child.getInt("timeOfContinuousUse"));
			list.add(user);
		}
		return list;
	}

	public static User getUser(String json) {
		if (json == null || json.isEmpty())
			return null;
		JSONObject jsonObject =new JSONObject().fromString(json);
		int result_code = jsonObject.getInt("result_code");
		if (result_code== 1)
			return null;
		JSONObject result = jsonObject.getJSONObject("result");
		User user = new User();
		user.setChannel_id(result.getString("cid"));
		user.setUsername(result.getString("username"));
		user.setTimeOfContinuousListen(result.getInt("timeOfContinuousUse"));
		user.setTimeOfContinuousUse(result.getInt("timeOfContinuousUse"));
		return user;
	}
	public static Map<String, String> getValidation(String json){
		if (json == null || json.isEmpty())
			return null;
		JSONObject jsonObject =new JSONObject().fromString(json);
		int result_code = jsonObject.getInt("result_code");
		if (result_code== 1)
			return null;
		String question=jsonObject.getString("question");
		String answer=jsonObject.getString("answer");
		Map<String, String> validation=new HashMap<String, String>();
		validation.put("question", question);
		validation.put("answer", answer);
		return validation;
	}
	public static List<Track> getTracks(String json){
		if (json == null || json.isEmpty())
			return null;
		List<Track> list = new ArrayList<Track>();
		JSONObject jsonObject = new JSONObject().fromString(json);
		int result_code = jsonObject.getInt("result_code");
		if (result_code == 1||result_code==2)
			return null;
		JSONArray result = jsonObject.getJSONArray("result");
		for (int i = 0; i < result.length(); i++) {
			JSONObject jsonTrack = result.getJSONObject(i);
			Track track=new Track();
			track.setAddress(jsonTrack.getString("address"));
			track.setAddTime(jsonTrack.getString("addTime"));
			track.setLatitude(jsonTrack.getDouble("latitude"));
			track.setLongitude(jsonTrack.getDouble("longitude"));
			track.setUsername(jsonTrack.getString("username"));
			list.add(track);
		}
		return list;
	}
}
