package njupt.stitp.android.db;

import java.util.ArrayList;
import java.util.List;

import com.baidu.a.a.a.c;

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
		values.put("question", user.getQuestion());
		values.put("answer", user.getAnswer());
		wdb.insert("user", null, values);
	}
	public void addUsers(List<User> list) {
		for (User user : list) {
			addUser(user);
		}
	}
	public List<String> getChildNames(String username){
		List<String> names=new ArrayList<String>(); 
		Cursor cursor = rdb.rawQuery("select username from user where username=?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			do{
				names.add(cursor.getString(cursor
						.getColumnIndex("username")));
			}while(cursor.moveToNext());
		}
		cursor.close();
		return names;
	}
}
