package njupt.stitp.android.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import njupt.stitp.android.model.APP;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppDB {
	private DBOpenHelper helper;
	private SQLiteDatabase rdb;
	private SQLiteDatabase wdb;

	public AppDB(Context context) {
		helper = new DBOpenHelper(context);	
		  rdb = helper.getReadableDatabase(); wdb =
		 helper.getWritableDatabase();
		 
	}

	public void saveMessage(String username, List<APP> runningApps) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
		Date date = new Date();
		String dateString = format.format(date);
		for (APP app : runningApps) {
			String insert = "insert or ignore into app(username,appName,addDate,appUseTime,icon) values(?,?,?,?,?)";
			String update = "update app set appUseTime = appUseTime+5 where username=? and appName=? and addDate=?";
			wdb.execSQL(insert, new Object[] { username, app.getAppName(),
					dateString, Integer.valueOf(0).toString(), app.getIcon() });
			wdb.execSQL(update, new String[] { username, app.getAppName(),
					dateString });
		}
	}

	public List<APP> getMessage(String username, Date addDate) {
		List<APP> apps = new ArrayList<APP>();
		Cursor cursor = rdb.rawQuery(
				"select * from app where username=? and addDate=?",
				new String[] { username,
						new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(addDate) });
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
		return apps;
	}
	public void updateMessage(String name,List<APP> apps){
		if(apps!=null&&apps.size()>0){
			wdb.execSQL("delete from app where username=? and addDate=?", new Object[]{name,apps.get(0).getAddDate()});
			for(APP app:apps){
				ContentValues values = new ContentValues();
				values.put("username", app.getUsername());
				values.put("appUseTime", app.getAppUseTime());
				values.put("appName", app.getAppName());
				values.put("addDate", app.getAddDate());
				values.put("icon", app.getIcon());
				wdb.insert("app", null, values);
			}
		}
	}
	public void close(){
		if(rdb!=null){
			rdb.close();
		}
		if(wdb!=null){
			wdb.close();
		}
	}
}
