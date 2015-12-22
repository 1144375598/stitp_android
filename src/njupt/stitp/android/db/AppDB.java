package njupt.stitp.android.db;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AppDB {
	private DBOpenHelper helper;
	private SQLiteDatabase rdb;
	private SQLiteDatabase wdb;

	public AppDB(Context context) {
		helper = new DBOpenHelper(context);
		rdb = helper.getReadableDatabase();
		wdb = helper.getWritableDatabase();
	}

	public void saveMessage(String username, List<String> applicationName) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String dateString = format.format(date);
		for (String name : applicationName) {
			String insert = "insert or ignore into app(username,appname,addtime,appusetime) values(?,?,?,?)";
			String update = "update app set appusetime = appusetime+5 where username=? and appname=? and addtime=?";
			wdb.execSQL(insert, new String[] { username, name, dateString,
					new Integer(0).toString() });
			wdb.execSQL(update, new String[] { username, name, dateString });
		}
	}
}
