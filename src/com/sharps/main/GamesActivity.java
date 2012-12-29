package com.sharps.main;

import Database.MySQLiteHelper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.cellr.noid.actionbar.ActionBarListActivity;
import com.sharps.R;
import com.sharps.Network.GamesDownloader;

public class GamesActivity extends ActionBarListActivity implements
		OnScrollListener, OnItemClickListener {
	private String sheetID;
	private Cursor cursor;
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_TEAM1,
			MySQLiteHelper.COLUMN_TEAM2, MySQLiteHelper.COLUMN_DATE,
			MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_SIGN,
			MySQLiteHelper.COLUMN_RESULT, MySQLiteHelper.COLUMN_ODDS,
			MySQLiteHelper.COLUMN_AMOUNT, MySQLiteHelper.COLUMN_GAMEID,
			MySQLiteHelper.COLUMN_SHEETID, MySQLiteHelper.COLUMN_RESULT,
			MySQLiteHelper.COLUMN_ID };
	private String selection;
	private String orderBy = MySQLiteHelper.COLUMN_DATE + " DESC ";
	private boolean downloading = false;
	public static boolean isLastPageReched = false;
	private int page = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.games_view);
		Intent i = getIntent();
		sheetID = i.getStringExtra("sheetID");
		selection = MySQLiteHelper.COLUMN_SHEETID + " = " + sheetID;
		getListView().setOnScrollListener(this);
		getListView().setOnItemClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper = new MySQLiteHelper(getApplicationContext());
		database = dbHelper.getWritableDatabase();
		int[] to = { android.R.id.text1, android.R.id.text2 };
		cursor = database.query(MySQLiteHelper.TABLE_GAMES, allColumns,
				selection, null, null, null, orderBy);
		page = (cursor.getCount() + 1) / 10;
		ShowGamesAdapter adapter = new ShowGamesAdapter(
				getApplicationContext(),
				R.layout.simple_list_item_2_black_text, cursor, allColumns, to,
				0) {

			@Override
			public int getColor(Cursor c) {
				String str = c.getString(c
						.getColumnIndex(MySQLiteHelper.COLUMN_RESULT));
				double d = Double.parseDouble(str);
				if (d > 0) {
					return Color.GREEN;
				} else if (d < 0) {
					return Color.RED;
				} else {
					return Color.GRAY;
				}
			}

		};
		setListAdapter(adapter);
		database.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isLastPageReched=false;
		cursor.close();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		boolean loadMore = false;
		loadMore = /* maybe add a padding */
		firstVisibleItem + visibleItemCount >= totalItemCount
				&& !isLastPageReched;
		if (loadMore && !downloading) {
			downloadGames(page);
			page++;
			downloading = true;
		}
	}

	private synchronized void downloadGames(final int page) {
		System.out.println("Downloading games");
		setProgressBarIndeterminateVisibility(true);
		downloading = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				new GamesDownloader(getApplicationContext(),
						"http://www.sharps.se/forums/includes/ss/app_games.php?ssid="
								+ sheetID + "&page=" + page);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						if (getListAdapter() != null) {
							dbHelper = new MySQLiteHelper(
									getApplicationContext());
							database = dbHelper.getWritableDatabase();
							cursor = database.query(MySQLiteHelper.TABLE_GAMES,
									allColumns, selection, null, null, null,
									orderBy);
							((ShowGamesAdapter) getListAdapter())
									.changeCursor(cursor);
							setProgressBarIndeterminateVisibility(false);
							database.close();
							downloading = false;
						}

					}
				});
			}
		}).start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.addGame:
			Intent myIntent = new Intent(GamesActivity.this,
					AddGameActivity.class);
			myIntent.putExtra("sheetID", sheetID);
			startActivity(myIntent);
			break;
		case R.id.menu_refresh:
			downloadGames(0);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		dbHelper = new MySQLiteHelper(getApplicationContext());
		database = dbHelper.getWritableDatabase();
		Cursor c = database.query(MySQLiteHelper.TABLE_SPREADSHEETS,
				new String[] { MySQLiteHelper.COLUMN_OWNER,
						MySQLiteHelper.COLUMN_SHEETID },
				MySQLiteHelper.COLUMN_SHEETID + " = " + sheetID, null, null,
				null, null);
		c.moveToFirst();
		String str = c.getString(c.getColumnIndex(MySQLiteHelper.COLUMN_OWNER));
		c.close();
		database.close();
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		if (str.equals("1")) {
			getMenuInflater().inflate(R.menu.activity_games, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent myIntent = new Intent(GamesActivity.this,
				ShowGameDetailsActivity.class);
		Cursor cursor = (Cursor) getListAdapter().getItem(arg2);
		String gameID = cursor.getString(cursor
				.getColumnIndex(MySQLiteHelper.COLUMN_GAMEID));
		myIntent.putExtra("gameID", gameID);
		myIntent.putExtra("sheetID", sheetID);
		startActivity(myIntent);
	}
}
