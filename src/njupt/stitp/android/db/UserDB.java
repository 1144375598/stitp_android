package njupt.stitp.android.db;

import java.util.List;

import njupt.stitp.android.model.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class UserDB {
	private DBOpenHelper helper;
	private SQLiteDatabase rdb;
	private SQLiteDatabase wdb;

	public UserDB(Context context) {
		helper = new DBOpenHelper(context);
		rdb = helper.getReadableDatabase();
		wdb = helper.getWritableDatabase();
	}

	public void delete() {
		wdb.execSQL("delete from user");
	}

	public void updateUser(List<User> list) {
		if (list.size() > 0) {
			delete();
			addUsers(list);
		}
	}

	public void addUser(User user) {
		ContentValues values = new ContentValues();
		values.put("username", user.getUsername());
		values.put("password", user.getPassword());
		values.put("timeOfContinuousUse", user.getTimeOfContinuousUse());
		values.put("timeOfContinuousListen", user.getTimeOfContinuousListen());
		values.put("channelId", user.getChannel_id());
		wdb.insert("user", null, values);
	}

	public void addUsers(List<User> list) {
		for (User user : list) {
			addUser(user);
		}
	}
}
