package njupt.stitp.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static String name = "stitp.db";
	private static final String CREATE_USER = "CREATE table  user" + "("
			+ "username text primary key," + "password text,"
			+ "timeOfContinuousUse integer,"
			+ "timeOfContinuousListen integer," + "channelId  text," + ")";

	private static final String CREATE_USETIMECONTROL = "CREATE table  usetimeControl"
			+ "( "
			+ "id integer primary key autoincrement,"
			+ "username text,"
			+ "start text," + "end text," + ")";

	private static final String CREATE_GEOFENCING = "create table GeoFencing" + /* "create table IF NOT EXISTS GeoFencing" */
	"( " + "username text  primary," + "longtitude rear," + "latitude rear,"
			+ "distance rear," + ")";

	private static final String CREATE_RELATIONSHIP = "create table  relationship( "
			+ /* IF NOT EXISTS */
			"childname text,"
			+ "parentname text, "
			+ "primary key (childname , parentname)" + ")";

	private static final String CREATE_TRACK = "create table  track( " + /* "IF NOT EXISTS" */
	"id integer primary key autoincrement," + "username text,"
			+ "longitude rear," + "latitude rear," + "addTime text," + "isCommit integer)";

	private static final String CREATE_MESSAGE = "create table IF NOT EXISTS message( "
			+ "id integer primary key autoincrement,"
			+ "sender text,"
			+ "receiver text," + "message text ," + "date text " + ")";

	public static final String CREATE_APP = "create table  app("
			+ "username text, " + "appusetime integer, " + "appname text,"
			+ "addtime text ," + "primary key(username,appname,addtime))";

	public static int version = 1;
	private Context mContext;
	public DBOpenHelper(Context context) {
		super(context, name, null, version);
		mContext=context;
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
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
