package njupt.stitp.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static String name = "stitp.db";
	private static final String CREATE_OPTION = "CREATE table option "
			+ "(username text primary key , " + "lockScreen integer,"
			+ "voiceControl integer," + "bumpRemind integer,"
			+ "continueUse integer)";
	private static final String CREATE_USER = "CREATE table  user" + "("
			+ "username text primary key," + "timeOfContinuousUse integer,"
			+ "timeOfContinuousListen integer,"
			+ "channelId  text, lockPwd text,QQ text)";

	private static final String CREATE_USETIMECONTROL = "CREATE table  useTimeControl"
			+ "( "
			+ "username text,"
			+ "start text,"
			+ "end text,"
			+ "primary key(username,start,end))";

	private static final String CREATE_GEOFENCING = "create table GeoFencing" + /* "create table IF NOT EXISTS GeoFencing" */
	"( " + "username text primary key," + "longitude rear," + "latitude rear,"
			+ "distance rear, " + "address text" 
			+ ")";

	private static final String CREATE_RELATIONSHIP = "create table  relationship( "
			+ "childname text,"
			+ "parentname text, "
			+ "primary key (childname , parentname)" + ")";

	private static final String CREATE_TRACK = "create table  track( "
			+ "id integer primary key autoincrement," + "username text,"
			+ "longitude rear," + "latitude rear," + "addTime text,"
			+ "isCommit integer," + "address text," + "stayTime integer)";

	private static final String CREATE_MESSAGE = "create table IF NOT EXISTS message( "
			+ "id integer primary key autoincrement,"
			+ "sender text,"
			+ "receiver text," + "message text ," + "date text " + ")";

	public static final String CREATE_APP = "create table  app("
			+ "username text, " + "appUseTime integer, " + "appName text,"
			+ "addDate text ," + "icon blob ,"
			+ "primary key(username,appname,addDate))";

	public static int version = 1;

	public DBOpenHelper(Context context) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_USER);
		db.execSQL(CREATE_USETIMECONTROL);
		db.execSQL(CREATE_GEOFENCING);
		db.execSQL(CREATE_RELATIONSHIP);
		db.execSQL(CREATE_TRACK);
		db.execSQL(CREATE_MESSAGE);
		db.execSQL(CREATE_APP);
		db.execSQL(CREATE_OPTION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
