package Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	// Tables
	public static final String TABLE_SPREADSHEETS = "spreadsheets";
	public static final String TABLE_GAMES = "games";
	// Spreadsheet table columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ROI = "roi";
	public static final String COLUMN_SHEETID = "sheetid";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_LAST_ADDED = "lastadded";
	public static final String COLUMN_NUMBER_OF_GAMES = "games";
	// Game table columns
	public static final String COLUMN_DATE = "Datum";
	public static final String COLUMN_TIME = "Tid";
	public static final String COLUMN_TEAM1 = "Hemmalag";
	public static final String COLUMN_TEAM2 = "Bortalag";
	public static final String COLUMN_SIGN = "Tecken";
	public static final String COLUMN_SIGN2 = "Tecken2";
	public static final String COLUMN_AMOUNT = "Insats";
	public static final String COLUMN_ODDS = "Odds";
	public static final String COLUMN_SPORT = "Sport";
	public static final String COLUMN_COUNTRY = "Land";
	public static final String COLUMN_LEAGUE = "Liga";
	public static final String COLUMN_INFO = "Info";
	public static final String COLUMN_REKARE = "Rekare";
	public static final String COLUMN_COMPANY = "Bolag";
	public static final String COLUMN_LIVE = "Live";
	public static final String COLUMN_LOCKED = "Locked";
	public static final String COLUMN_PERIOD = "Period";
	public static final String COLUMN_RESULT = "Resultat";
	public static final String COLUMN_ALIVE = "Alive";
	public static final String COLUMN_OWNER = "Ägare";
	public static final String COLUMN_GAMEID = "gameid";

	private static final String DATABASE_NAME = "sharps.db";
	private static final int DATABASE_VERSION = 1;
	// Database creation sql statement
	private static final String DATABASE_CREATE_SPREADSHEETS = "create table "
			+ TABLE_SPREADSHEETS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_ROI
			+ " text not null, " + COLUMN_SHEETID + " text not null, "
			+ COLUMN_OWNER + " text not null, " + COLUMN_NUMBER_OF_GAMES
			+ " text not null, " + COLUMN_LAST_ADDED + " text not null, "
			+ COLUMN_TITLE + " text not null);";

	private static final String DATABASE_CREATE_GAMES = "create table "
			+ TABLE_GAMES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_DATE
			+ " text not null, " + COLUMN_TIME + " text not null, "
			+ COLUMN_TEAM1 + " text not null, " + COLUMN_TEAM2
			+ " text not null, " + COLUMN_SIGN + " text not null, "
			+ COLUMN_SIGN2 + " text not null, " + COLUMN_AMOUNT
			+ " text not null, " + COLUMN_ODDS + " text not null, "
			+ COLUMN_SPORT + " text not null, " + COLUMN_COUNTRY
			+ " text not null, " + COLUMN_LEAGUE + " text not null, "
			+ COLUMN_INFO + " text not null, " + COLUMN_REKARE
			+ " text not null, " + COLUMN_ALIVE + " text not null, "
			+ COLUMN_COMPANY + " text not null, " + COLUMN_LIVE
			+ " text not null, " + COLUMN_PERIOD + " text not null, "
			+ COLUMN_LOCKED + " text not null, " + COLUMN_RESULT
			+ " text not null, " + COLUMN_SHEETID + " text not null, "
			+ COLUMN_GAMEID + " text not null);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_SPREADSHEETS);
		db.execSQL(DATABASE_CREATE_GAMES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPREADSHEETS);
		onCreate(db);
	}

}
