package njupt.stitp.android.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import njupt.stitp.android.R.string;
import njupt.stitp.android.model.Track;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EdgeEffect;

public class TrackDB {
	private DBOpenHelper helper;
	private SQLiteDatabase rdb;
	private SQLiteDatabase wdb;

	public TrackDB(Context context) {
		helper = new DBOpenHelper(context);
		/*rdb = helper.getReadableDatabase();
		wdb = helper.getWritableDatabase();*/
	}

	public void addTrack(Track track, int isCommit) {
		wdb = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("username", track.getUsername());
		values.put("longitude", track.getLongitude());
		values.put("latitude", track.getLatitude());
		values.put("addTime", track.getAddTime());
		values.put("stayTime", track.getStayTime());
		values.put("address", track.getAddress());
		values.put("isCommit", isCommit);
		wdb.insert("track", null, values);
		wdb.close();
	}

	// isCommit为0表示已提交至服务器，为1表示未提交至服务器，为2表示是从服务器下载的轨迹
	public void addTracks(List<Track> list, int isCommit) {
		for (Track track : list) {
			addTrack(track, isCommit);
		}
	}

	public List<Track> getUncommitTrack(String username) {
		rdb = helper.getReadableDatabase();
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
				track.setAddress(cursor.getString(cursor
						.getColumnIndex("address")));
				track.setStayTime(cursor.getInt(cursor
						.getColumnIndex("stayTime")));
				list.add(track);
			} while (cursor.moveToNext());
		}
		cursor.close();
		rdb.close();
		return list;
	}

	public void updateTrack(List<Track> tracks) {
		wdb = helper.getWritableDatabase();
		for (Track track : tracks) {
			wdb.execSQL(
					"update track set isCommit = 0 where addTime=? and username=?",
					new String[] { track.getAddTime(), track.getUsername() });
		}
		wdb.close();
	}

	public void delete(String username, Date date) {
		wdb = helper.getWritableDatabase();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String temp=format.format(date);
		String start=temp+" 00:00:00";
		String end=temp+" 23:59:59";
		wdb.execSQL("delete from track where username=? and addTime between ? and ?",
				new String[] { username, start,end });
		wdb.close();
	}

	public void dropThenAddTracks(List<Track> tracks,int isCommit) {
		if (tracks.size() > 0) {
			String username=tracks.get(0).getUsername();
			String dateString=tracks.get(0).getAddTime();
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date =null;
			try {
				date = format.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			delete(username, date);
			addTracks(tracks, isCommit);
		}
	}

	public List<Track> getTracks(String username, Date date) {
		rdb = helper.getReadableDatabase();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String temp=format.format(date);
		String start=temp+" 00:00:00";
		String end=temp+" 23:59:59";
		List<Track> tracks = new ArrayList<Track>();
		Cursor cursor = rdb.rawQuery(
				"select * from track where username=? and addTime between ? and ?",
				new String[] { username, start,end });
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
				track.setAddress(cursor.getString(cursor
						.getColumnIndex("address")));
				track.setStayTime(cursor.getInt(cursor
						.getColumnIndex("stayTime")));
				tracks.add(track);
			} while (cursor.moveToNext());
		} 
		cursor.close();
		rdb.close();
		return tracks;
	}
}
