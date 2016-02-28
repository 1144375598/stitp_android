package njupt.stitp.android.db;

import java.util.ArrayList;
import java.util.List;

import njupt.stitp.android.model.GeoFencing;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GeoDB {
	private DBOpenHelper helper;
	private SQLiteDatabase rdb;
	private SQLiteDatabase wdb;

	public GeoDB(Context context) {
		helper = new DBOpenHelper(context);
		rdb = helper.getReadableDatabase();
		wdb = helper.getWritableDatabase();

	}

	public void saveGeo(String username, GeoFencing geoFencing) {
		String insert = "insert into GeoFencing(username,longitude,latitude,distance,address) values(?,?,?,?,?,?)";
		wdb.execSQL("delete from GeoFencing where username=?",
				new Object[] { username });
		wdb.execSQL(
				insert,
				new Object[] { username, geoFencing.getLongitude(),
						geoFencing.getLatitude(), geoFencing.getDistance(),
						geoFencing.getAddress() });
	}

	public GeoFencing getGeo(String username) {
		Cursor cursor = rdb.rawQuery(
				"select * from GeoFencing where username=?",
				new String[] { username });
		if (cursor.moveToFirst()) {
				GeoFencing geoFencing = new GeoFencing();
				geoFencing.setUsername(cursor.getString(cursor
						.getColumnIndex("username")));
				geoFencing.setLatitude(cursor.getDouble(cursor
						.getColumnIndex("latitude")));
				geoFencing.setLongitude(cursor.getDouble(cursor
						.getColumnIndex("longitude")));
				geoFencing.setAddress(cursor.getString(cursor
						.getColumnIndex("address")));
				geoFencing.setDistance(cursor.getDouble(cursor
						.getColumnIndex("distance")));
				cursor.close();
				return geoFencing;
		}
		cursor.close();
		return null;
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
