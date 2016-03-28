package njupt.stitp.android.db;

import java.util.ArrayList;
import java.util.List;

import njupt.stitp.android.model.Friend;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RelationshipDB {
	private DBOpenHelper helper;
	private SQLiteDatabase rdb;
	private SQLiteDatabase wdb;

	public RelationshipDB(Context context) {
		helper = new DBOpenHelper(context);
		rdb = helper.getReadableDatabase();
		wdb = helper.getWritableDatabase();

	}

	public void addRelationship(String parentName, String childName) {
		wdb.execSQL(
				"insert into relationship(parentname,childname) values (?,?)",
				new String[] { parentName, childName });
	}

	public List<Friend> getFriend(String username) {
		List<Friend> friends = new ArrayList<Friend>();
		Cursor cursor = rdb.rawQuery(
				"select parentname from relationship where childname =?",
				new String[] { username });
		Cursor cursor2 = rdb.rawQuery(
				"select childname from relationship where parentname =?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			do {
				Friend friend = new Friend();
				friend.setRelationship("家长");
				friend.setUsername(cursor.getString(cursor
						.getColumnIndex("parentname")));
				friends.add(friend);
			} while (cursor.moveToNext());
		}
		if (cursor2.moveToFirst()) {
			do {
				Friend friend = new Friend();
				friend.setRelationship("孩子");
				friend.setUsername(cursor2.getString(cursor2
						.getColumnIndex("childname")));
				friends.add(friend);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor2.close();
		return friends;
	}

	public void deleteFriend(String parentName, String childName) {
		wdb.execSQL(
				"delete from relationship where parentname=? and childname=?",
				new String[] { parentName, childName });
	}

	public List<String> getFriendName(String username) {
		List<String> friends = new ArrayList<String>();
		friends.add(username);
		Cursor cursor = rdb.rawQuery(
				"select parentname from relationship where childname =?",
				new String[] { username });
		Cursor cursor2 = rdb.rawQuery(
				"select childname from relationship where parentname =?",
				new String[] { username });
		if (cursor.moveToFirst()) {
			do {
				String friend = cursor.getString(cursor
						.getColumnIndex("parentname"));
				friends.add(friend);
			} while (cursor.moveToNext());
		}
		if (cursor2.moveToFirst()) {
			do {
				String friend = cursor2.getString(cursor2
						.getColumnIndex("childname"));
				friends.add(friend);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor2.close();
		return friends;
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
