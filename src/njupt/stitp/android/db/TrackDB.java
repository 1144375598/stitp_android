package njupt.stitp.android.db;

import java.util.ArrayList;
import java.util.List;

import njupt.stitp.android.model.Track;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TrackDB {
	private DBOpenHelper helper;
	private SQLiteDatabase rdb;
	private SQLiteDatabase wdb;

	public TrackDB(Context context) {
		helper = new DBOpenHelper(context);
		rdb = helper.getReadableDatabase();
		wdb = helper.getWritableDatabase();
	}

	public void addTrack(Track track, int isCommit) {
		ContentValues values = new ContentValues();
		values.put("username", track.getUsername());
		values.put("longtitude", track.getLongitude());
		values.put("latitude", track.getLatitude());
		values.put("addTime", track.getAddTime());
		values.put("isCommit", isCommit);
		wdb.insert("track", null, values);
	}

	// isCommit为0表示已提交至服务器，为1表示未提交至服务器，为2表示是从服务器下载的轨迹
	public void addTracks(List<Track> list, int isCommit) {
		for (Track track : list) {
			addTrack(track, isCommit);
		}
	}

	public List<Track> getUncommitTrack(String username) {
		List<Track> list = new ArrayList<Track>();
		Cursor cursor = rdb.rawQuery(
				"select * from track where username=? and isCommit = 1",
				new String[] { username });
		if (cursor.moveToFirst()) {
			do {
				Track track = new Track();
				track.setAddTime(cursor.getString(cursor
						.getColumnIndex("addTime")));
				track.setLatitude(cursor.getDouble(cursor
						.getColumnIndex("latitude")));
				track.setLongitude(cursor.getDouble(cursor
						.getColumnIndex("longitude")));
				track.setUsername(cursor.getString(cursor
						.getColumnIndex("username")));
				list.add(track);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public void updateTrack(List<Track> tracks) {
		for (Track track : tracks) {
			wdb.execSQL(
					"update track set isCommit = 0 where addTime=? and username=?",
					new String[] { track.getAddTime(), track.getUsername() });
		}
	}
}
