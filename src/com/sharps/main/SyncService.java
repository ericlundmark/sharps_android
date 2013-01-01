package com.sharps.main;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import Database.MySQLiteHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.sharps.R;
import com.sharps.Network.GamesDownloader;

public class SyncService extends Service implements Observer {

	private SQLiteDatabase database;
	private int amountOfSheets = 0;
	private int sheetsDoneDownloading = 0;
	private int amountOfNewGames = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		database = ((MyApplication) getApplication()).getDatabase();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				ArrayList<String> sheetID = getAllSheetID();
				amountOfSheets = sheetID.size();
				System.out.println(amountOfSheets);
				for (String string : sheetID) {
					downloadGames(0, string);
				}
			}
		}, 0, 300000);
	}

	private void generateNotification(int i) {
		NotificationManager notificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		Intent notificationIntent = new Intent(getApplicationContext(),
				SpreadsheetActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.putExtra("update", "1");
		PendingIntent intent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent, 0);
		Uri defaultSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		Notification notification = new NotificationCompat.Builder(
				getApplicationContext()).setContentTitle("Notification")
				.setContentText(i + " nya spel").setContentIntent(intent)
				.setSmallIcon(icon).setLights(Color.YELLOW, 1, 2)
				.setAutoCancel(true).setSound(defaultSound).build();

		notificationManager.notify(0, notification);
	}

	private synchronized void downloadGames(final int page, final String sheetID) {
		GamesDownloader gamesDownloader = new GamesDownloader(database,
				sheetID, page);
		gamesDownloader.addObserver(this);
		new Thread(gamesDownloader).start();
	}

	private synchronized ArrayList<String> getAllSheetID() {
		ArrayList<String> sheetID = new ArrayList<String>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SPREADSHEETS,
				new String[] { MySQLiteHelper.COLUMN_SHEETID, }, MySQLiteHelper.COLUMN_OWNER + " = 0", null,
				null, null, null);
		while (cursor.moveToNext()) {
			String str = cursor.getString(cursor
					.getColumnIndex(MySQLiteHelper.COLUMN_SHEETID));
			sheetID.add(str);
		}
		cursor.close();
		return sheetID;
	}

	@Override
	public void update(Observable observable, Object data) {
		String[] updateData = ((String) data).split(":");
		amountOfNewGames += Integer.parseInt(updateData[1]);
		ContentValues contentValues = new ContentValues();
		contentValues.put(MySQLiteHelper.COLUMN_UNVIEWED_GAMES, updateData[1]);
		String where = MySQLiteHelper.COLUMN_SHEETID + " = " + updateData[0];
		database.update(MySQLiteHelper.TABLE_SPREADSHEETS, contentValues, where,
				null);
		sheetsDoneDownloading++;
		if (amountOfSheets == sheetsDoneDownloading) {
			if (amountOfNewGames > 0) {
				generateNotification(amountOfNewGames);
			}
			amountOfNewGames = 0;
			sheetsDoneDownloading = 0;
		}
	}
}
