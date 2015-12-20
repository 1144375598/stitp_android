package njupt.stitp.android.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import njupt.stitp.android.model.User;

public class JsonUtil {
	// json为空，返回-1；否则返回result_code
	public static int LoginAndRegister(String json) {
		if (json == null || json.isEmpty())
			return -1;
		JSONObject jsonObject = new JSONObject().fromString(json);
		return jsonObject.getInt("result_code");
	}

	// json为空，返回null；无孩子，返回空的list；有孩子，返回孩子的list
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

}
