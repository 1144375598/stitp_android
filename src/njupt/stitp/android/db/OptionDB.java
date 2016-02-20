package njupt.stitp.android.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OptionDB {
	private DBOpenHelper helper;
	private SQLiteDatabase rdb;
	private SQLiteDatabase wdb;
	private String insert = "insert or ignore into option"
			+ "(username,lockScreen,voiceControl,bumpRemind,continueUse) "
			+ "values(?,?,?,?,?)";

	public OptionDB(Context context) {
		helper = new DBOpenHelper(context);
		wdb = helper.getWritableDatabase();
		rdb = helper.getReadableDatabase();
	}

	// option中0为未选择，1表示选择
	public void setLockScreen(String username, int option) {

		wdb.execSQL(insert, new Object[] { username, 0, 0, 0, 0 });
		wdb.execSQL("update option set lockScreen = ? where username =?",
				new Object[] { option, username });
	}

	public int getLockScreen(String username) {

		int i = 0;
		Cursor cursor = rdb.rawQuery(
				"select lockScreen from option where username=?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			i = cursor.getInt(cursor.getColumnIndex("lockScreen"));
		}
		cursor.close();
		return i;
	}

	public void setVoiceControl(String username, int option) {
		wdb.execSQL(insert, new Object[] { username, 0, 0, 0, 0 });
		wdb.execSQL("update option set voiceControl = ? where username =?",
				new Object[] { option, username });
	}

	public int getVoiceControl(String username) {
		int i = 0;
		Cursor cursor = rdb.rawQuery(
				"select voiceControl from option where username=?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			i = cursor.getInt(cursor.getColumnIndex("voiceControl"));
		}
		cursor.close();
		return i;
	}

	public void setBumpRemind(String username, int option) {
		wdb.execSQL(insert, new Object[] { username, 0, 0, 0, 0 });
		wdb.execSQL("update option set bumpRemind = ? where username =?",
				new Object[] { option, username });
	}

	public int getBumpRemind(String username) {
		int i = 0;
		Cursor cursor = rdb.rawQuery(
				"select bumpRemind from option where username=?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			i = cursor.getInt(cursor.getColumnIndex("bumpRemind"));
		}
		cursor.close();
		return i;
	}

	public void setContinueUse(String username, int option) {
		wdb.execSQL(insert, new Object[] { username, 0, 0, 0, 0 });
		wdb.execSQL("update option set continueUse = ? where username =?",
				new Object[] { option, username });
	}

	public int getContinueUse(String username) {
		int i = 0;
		Cursor cursor = rdb.rawQuery(
				"select continueUse from option where username=?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			i = cursor.getInt(cursor.getColumnIndex("continueUse"));
		}
		cursor.close();
		return i;
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
