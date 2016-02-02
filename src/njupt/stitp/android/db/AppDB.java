package njupt.stitp.android.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import njupt.stitp.android.model.APP;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AppDB {
	private DBOpenHelper helper;
	private SQLiteDatabase rdb;
	private SQLiteDatabase wdb;

	public AppDB(Context context) {
		helper = new DBOpenHelper(context);
		/*
		 * rdb = helper.getReadableDatabase(); wdb =
		 * helper.getWritableDatabase();
		 */
	}

	public void saveMessage(String username, List<APP> runningApps) {
		wdb = helper.getWritableDatabase();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String dateString = format.format(date);
		for (APP app : runningApps) {
			String insert = "insert or ignore into app(username,appName,addDate,appUseTime,icon) values(?,?,?,?,?)";
			String update = "update app set appUseTime = appUseTime+5 where username=? and appName=? and addDate=?";
			wdb.execSQL(insert, new Object[] { username, app.getAppName(),
					dateString, new Integer(0).toString(), app.getIcon() });
			wdb.execSQL(update, new String[] { username, app.getAppName(),
					dateString });
		}
		wdb.close();
	}

	public List<APP> getMessage(String username, Date addDate) {
		rdb = helper.getReadableDatabase();
		List<APP> apps = new ArrayList<APP>();
		Cursor cursor = rdb.rawQuery(
				"select * from app where username=? and addDate=?",
				new String[] { username,
						new SimpleDateFormat("yyyy-MM-dd").format(addDate) });
		if (cursor.moveToFirst()) {
			do {
				APP app = new APP();
				app.setAddDate(cursor.getString(cursor
						.getColumnIndex("addDate")));
				app.setUsername(cursor.getString(cursor
						.getColumnIndex("username")));
				app.setAppName(cursor.getString(cursor
						.getColumnIndex("appName")));
				app.setAppUseTime(cursor.getInt(cursor
						.getColumnIndex("appUseTime")));
				app.setIcon(cursor.getBlob(cursor.getColumnIndex("icon")));
				apps.add(app);
			} while (cursor.moveToNext());
		}
		cursor.close();
		rdb.close();
		return apps;
	}
}
