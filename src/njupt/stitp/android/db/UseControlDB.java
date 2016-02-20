package njupt.stitp.android.db;

import java.util.ArrayList;
import java.util.List;

import njupt.stitp.android.model.UseTimeControl;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UseControlDB {
	private DBOpenHelper helper;
	private SQLiteDatabase rdb;
	private SQLiteDatabase wdb;

	public UseControlDB(Context context) {
		helper = new DBOpenHelper(context);
		rdb = helper.getReadableDatabase();
		wdb = helper.getWritableDatabase();

	}

	public void addUseTimeControl(String username, UseTimeControl useTimeControl) {
		wdb.execSQL(
				"insert into useTimeControl(username,start,end) values(?,?,?)",
				new String[] { useTimeControl.getUsername(),
						useTimeControl.getStart(), useTimeControl.getEnd() });
	}

	public List<UseTimeControl> getUseTimeControl(String username) {
		List<UseTimeControl> list = new ArrayList<UseTimeControl>();
		Cursor cursor = rdb.rawQuery(
				"select * from useTimeControl where username=?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			do {
				UseTimeControl useTimeControl = new UseTimeControl();
				useTimeControl.setUsername(cursor.getString(cursor
						.getColumnIndex("username")));
				useTimeControl.setStart(cursor.getString(cursor
						.getColumnIndex("start")));
				useTimeControl.setEnd(cursor.getString(cursor
						.getColumnIndex("end")));
				list.add(useTimeControl);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public void deleteAndAdd(String username, List<UseTimeControl> list) {
		wdb.rawQuery("delete from useTimeControl where username=?",
				new String[] { username });
		for (UseTimeControl useTimeControl : list) {
			addUseTimeControl(username, useTimeControl);
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
