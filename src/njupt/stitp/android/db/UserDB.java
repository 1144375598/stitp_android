package njupt.stitp.android.db;

import java.util.ArrayList;
import java.util.List;

import njupt.stitp.android.model.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

	public void updateUser(List<User> list, String username) {
		if (list.size() > 0) {
			delete();
			addUsers(list, username);
		}
	}

	// username不为空表示插入的user为username的孩子
	public void addUser(User user, String username) {
		ContentValues values = new ContentValues();
		values.put("username", user.getUsername());
		values.put("timeOfContinuousUse", user.getTimeOfContinuousUse());
		values.put("timeOfContinuousListen", user.getTimeOfContinuousListen());
		values.put("channelId", user.getChannelId());
		values.put("lockPwd", user.getLockPwd());
		wdb.insert("user", null, values);
		if (username != null) {
			ContentValues values2 = new ContentValues();
			values2.put("parentname", username);
			values2.put("childname", user.getUsername());
			wdb.insert("relationship", null, values2);
		}
	}

	// username不为空表示插入的user为username的孩子
	public void addUsers(List<User> list, String username) {
		for (User user : list) {
			addUser(user, username);
		}
	}

	public List<String> getChildNames(String username) {
		List<String> names = new ArrayList<String>();
		Cursor cursor = rdb.rawQuery(
				"select childname from relationship where parentname=?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			do {
				names.add(cursor.getString(cursor.getColumnIndex("childname")));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return names;
	}

	public User getUser(String username) {
		User user = new User();
		Cursor cursor = rdb.rawQuery("select * from user where username=?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
			user.setTimeOfContinuousUse(cursor.getInt(cursor
					.getColumnIndex("timeOfContinuousUse")));
			user.setTimeOfContinuousListen(cursor.getInt(cursor
					.getColumnIndex("timeOfContinuousListen")));
			user.setChannelId(cursor.getString(cursor
					.getColumnIndex("channelId")));
			user.setLockPwd(cursor.getString(cursor.getColumnIndex("lockPwd")));
		} else {
			cursor.close();
			return null;
		}
		cursor.close();
		return user;
	}

	public void updateLockPwd(String username, String lockPwd) {
		ContentValues values = new ContentValues();
		values.put("lockPwd", lockPwd);
		wdb.update("user", values, "username = ?", new String[] { username });
	}

	public void updateContinueUse(String username, int useTime) {
		ContentValues values = new ContentValues();
		values.put("timeOfContinuousUse", useTime);
		wdb.update("user", values, "username = ?", new String[] { username });
	}

	public boolean isFriend(String username, String queryName) {
		Cursor cursor = rdb.rawQuery("select parentname from relationship "
				+ "where (childname =? and parentname=?) "
				+ "or (childname=? and parentname=?)", new String[] { username,
				queryName, username, queryName });
		if (cursor.getCount() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void close() {
		if (rdb != null) {
			rdb.close();
		}
		if (wdb != null) {
			wdb.close();
		}
	}
}
