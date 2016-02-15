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
		/*
		 * rdb = helper.getReadableDatabase(); wdb =
		 * helper.getWritableDatabase();
		 */
	}

	public void saveGeo(String username, GeoFencing geoFencing) {
		wdb = helper.getWritableDatabase();
		String insert = "insert or ignore into GeoFencing(username,longitude,latitude,distance,address,geoName) values(?,?,?,?,?,?)";
		wdb.execSQL(
				insert,
				new Object[] { username, geoFencing.getLongitude(),
						geoFencing.getLatitude(), geoFencing.getDistance(),
						geoFencing.getAddress(), geoFencing.getGeoName() });
		wdb.close();
	}

	public List<GeoFencing> getGeos(String username) {
		rdb = helper.getReadableDatabase();
		List<GeoFencing> geoFencings = new ArrayList<GeoFencing>();
		Cursor cursor = rdb.rawQuery(
				"select * from GeoFencing where username=?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			do {
				GeoFencing geoFencing=new GeoFencing();
				geoFencing.setUsername(cursor.getString(cursor
						.getColumnIndex("username")));
				geoFencing.setLatitude(cursor.getDouble(cursor
						.getColumnIndex("latitude")));
				geoFencing.setLongitude(cursor.getDouble(cursor
						.getColumnIndex("longitude")));
				geoFencing.setAddress(cursor.getString(cursor
						.getColumnIndex("address")));
				geoFencing.setGeoName(cursor.getString(cursor
						.getColumnIndex("geoName")));
				geoFencing.setDistance(cursor.getDouble(cursor
						.getColumnIndex("distance")));
				geoFencings.add(geoFencing);
			} while (cursor.moveToNext());
		}
		cursor.close();
		rdb.close();
		return geoFencings;
	}
}
