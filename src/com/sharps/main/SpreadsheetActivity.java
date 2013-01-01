package com.sharps.main;

import Database.MySQLiteHelper;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.cellr.noid.actionbar.ActionBarListActivity;
import com.sharps.R;
import com.sharps.Network.SheetDownloader;

public class SpreadsheetActivity extends ActionBarListActivity implements
		OnItemClickListener {
	public final static String CATEGORY_MY_SPREADSHEETS = "Mina spreadsheets";
	public final static String CATEGORY_MY_FAVOURITES = "Mina favoriter";
	private SQLiteDatabase database;
	private String[] allColumns = { MySQLiteHelper.COLUMN_TITLE,
			MySQLiteHelper.COLUMN_ROI, MySQLiteHelper.COLUMN_SHEETID,
			MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_OWNER,
			MySQLiteHelper.COLUMN_LAST_ADDED,
			MySQLiteHelper.COLUMN_UNVIEWED_GAMES };
	private Cursor cursor;
	private int[] to = { android.R.id.text1, android.R.id.text2 };
	private String orderBy = MySQLiteHelper.COLUMN_LAST_ADDED + " DESC ";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spreadsheets_view);
		database = ((MyApplication) getApplication()).getDatabase();
		// You can also assign the title programmatically by passing a
		// CharSequence or resource id.
		// actionBar.setTitle(R.string.some_title);
		getListView().setOnItemClickListener(this);
		downloadSpreadsheets();
		getActionBarHelper().setRefreshActionItemState(true);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.hasExtra("update")) {
			downloadSpreadsheets();
			System.out.println("Updating spreadsheets");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SeparatedListAdapter adapter = new SeparatedListAdapter(
				getApplicationContext());
		cursor = database
				.query(MySQLiteHelper.TABLE_SPREADSHEETS, allColumns,
						MySQLiteHelper.COLUMN_OWNER + " = 1", null, null, null,
						orderBy);
		MyAdapter temp = new MyAdapter(getApplicationContext(),
				R.layout.simple_list_item_2_black_text, cursor, allColumns, to,
				0) {

			@Override
			public int getColor(Cursor c) {
				String str = c.getString(c
						.getColumnIndex(MySQLiteHelper.COLUMN_ROI));
				double d = Double.parseDouble(str);
				if (d >= 100) {
					return Color.GREEN;
				} else if (d < 100 && d != 0) {
					return Color.RED;
				} else {
					return Color.GRAY;
				}
			}

			@Override
			public int getBackgroundColor(Cursor c) {
				// TODO strul med bakgrundsfärgen, ändras tillbaka till grå utan
				// att nya har lagts till, kontrollera vad som sker.
				String str = c.getString(c
						.getColumnIndex(MySQLiteHelper.COLUMN_UNVIEWED_GAMES));
				int i = Integer.parseInt(str);
				if (i > 0) {
					return Color.LTGRAY;
				}
				return super.getBackgroundColor(cursor);
			}

		};
		adapter.addSection(CATEGORY_MY_SPREADSHEETS, temp);
		cursor = database
				.query(MySQLiteHelper.TABLE_SPREADSHEETS, allColumns,
						MySQLiteHelper.COLUMN_OWNER + " = 0", null, null, null,
						orderBy);
		temp = new MyAdapter(getApplicationContext(),
				R.layout.simple_list_item_2_black_text, cursor, allColumns, to,
				0) {

			@Override
			public int getColor(Cursor c) {
				String str = c.getString(c
						.getColumnIndex(MySQLiteHelper.COLUMN_ROI));
				double d = Double.parseDouble(str);
				if (d >= 100) {
					return Color.GREEN;
				} else if (d < 100 && d != 0) {
					return Color.RED;
				} else {
					return Color.GRAY;
				}
			}

			@Override
			public int getBackgroundColor(Cursor c) {
				String str = c.getString(c
						.getColumnIndex(MySQLiteHelper.COLUMN_UNVIEWED_GAMES));
				int i = Integer.parseInt(str);
				if (i > 0) {
					return Color.LTGRAY;
				}
				return super.getBackgroundColor(cursor);
			}
		};
		adapter.addSection(CATEGORY_MY_FAVOURITES, temp);
		setListAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cursor.close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			getActionBarHelper().setRefreshActionItemState(true);
			downloadSpreadsheets();
			break;
		case R.id.menu_settings:
			Intent intent = new Intent(getApplicationContext(),
					SettingsActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_spreadsheets, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private synchronized void downloadSpreadsheets() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				new SheetDownloader(database, SheetDownloader.MY,
						"http://www.sharps.se/forums/includes/ss/app_mysheets.php");
				new SheetDownloader(database, SheetDownloader.FAVOURITE,
						"http://www.sharps.se/forums/includes/ss/app_favsheets.php");
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						cursor = database.query(
								MySQLiteHelper.TABLE_SPREADSHEETS, allColumns,
								MySQLiteHelper.COLUMN_OWNER + " = 1", null,
								null, null, orderBy);
						((MyAdapter) ((SeparatedListAdapter) getListAdapter()).sections
								.get(CATEGORY_MY_SPREADSHEETS))
								.changeCursor(cursor);
						cursor = database.query(
								MySQLiteHelper.TABLE_SPREADSHEETS, allColumns,
								MySQLiteHelper.COLUMN_OWNER + " = 0", null,
								null, null, orderBy);
						((MyAdapter) ((SeparatedListAdapter) getListAdapter()).sections
								.get(CATEGORY_MY_FAVOURITES))
								.changeCursor(cursor);
						((SeparatedListAdapter) getListAdapter())
								.notifyDataSetChanged();
						getActionBarHelper().setRefreshActionItemState(false);
					}
				});

			}
		}).start();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(SpreadsheetActivity.this,
				GamesActivity.class);
		Cursor c = (Cursor) getListAdapter().getItem(arg2);
		String str = c.getString(c
				.getColumnIndex(MySQLiteHelper.COLUMN_SHEETID));
		intent.putExtra("sheetID", str);
		ContentValues contentValues = new ContentValues();
		contentValues.put(MySQLiteHelper.COLUMN_UNVIEWED_GAMES, 0);
		String where = MySQLiteHelper.COLUMN_SHEETID + " = " + str;
		database.update(MySQLiteHelper.TABLE_SPREADSHEETS, contentValues,
				where, null);
		startActivity(intent);
	}

}
