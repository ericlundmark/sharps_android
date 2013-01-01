package com.sharps.main;

import java.util.ArrayList;
import java.util.HashMap;

import Database.MySQLiteHelper;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cellr.noid.actionbar.ActionBarListActivity;
import com.sharps.R;
import com.sharps.Network.CorrectionHandler;

public class ShowGameDetailsActivity extends ActionBarListActivity {
	public static enum Results {
		WIN, PUSH, LOSS
	}

	private SQLiteDatabase database;
	private String[] allColumns = { MySQLiteHelper.COLUMN_TEAM1,
			MySQLiteHelper.COLUMN_TEAM2, MySQLiteHelper.COLUMN_DATE,
			MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_SIGN,
			MySQLiteHelper.COLUMN_SIGN2, MySQLiteHelper.COLUMN_SPORT,
			MySQLiteHelper.COLUMN_COUNTRY, MySQLiteHelper.COLUMN_LEAGUE,
			MySQLiteHelper.COLUMN_COMPANY, MySQLiteHelper.COLUMN_PERIOD,
			MySQLiteHelper.COLUMN_INFO, MySQLiteHelper.COLUMN_REKARE,
			MySQLiteHelper.COLUMN_AMOUNT, MySQLiteHelper.COLUMN_ODDS,
			MySQLiteHelper.COLUMN_RESULT };
	private String selection;
	private String gameID;
	private String sheetID;
	private String[] from = { "line1", "line2" };
	private int[] to = { android.R.id.text1, android.R.id.text2 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_game);
		database = ((MyApplication) getApplication()).getDatabase();
		Intent intent = getIntent();
		gameID = intent.getStringExtra("gameID");
		sheetID = intent.getStringExtra("sheetID");
		selection = MySQLiteHelper.COLUMN_GAMEID + " = " + gameID;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
				getData(), R.layout.simple_list_item_2_black_text, from, to);
		setListAdapter(adapter);
	}

	private ArrayList<HashMap<String, String>> getData() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_GAMES, allColumns,
				selection, null, null, null, null);
		cursor.moveToFirst();
		for (int i = 0; i < allColumns.length; i++) {
			String str = cursor.getString(cursor.getColumnIndex(allColumns[i]));
			if (!str.equals("")) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(from[0],
						cursor.getString(cursor.getColumnIndex(allColumns[i])));
				map.put(from[1], allColumns[i]);
				list.add(map);
			}
		}
		cursor.close();
		return list;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.win:
			new CorrectionHandler(sheetID, gameID, "win").start();
			break;
		case R.id.push:
			new CorrectionHandler(sheetID, gameID, "push").start();
			break;
		case R.id.loss:
			new CorrectionHandler(sheetID, gameID, "loss").start();
			break;
		case R.id.addGame:
			createDialog();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Välj ett spreadsheet");

		ListView modeList = new ListView(getApplicationContext());

		String[] spreadsheetColumns = { MySQLiteHelper.COLUMN_TITLE,
				MySQLiteHelper.COLUMN_ROI, MySQLiteHelper.COLUMN_SHEETID,
				MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_OWNER };

		Cursor cursor = database.query(MySQLiteHelper.TABLE_SPREADSHEETS,
				spreadsheetColumns, MySQLiteHelper.COLUMN_OWNER + " = 1", null,
				null, null, null);
		final MyAdapter temp = new MyAdapter(getApplicationContext(),
				R.layout.simple_list_item_2_black_text, cursor,
				spreadsheetColumns, to, 0) {

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
		};
		modeList.setAdapter(temp);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		dialog.show();
		modeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(ShowGameDetailsActivity.this,
						AddGameActivity.class);
				Cursor c = (Cursor) temp.getItem(arg2);
				String str = c.getString(c
						.getColumnIndex(MySQLiteHelper.COLUMN_SHEETID));
				intent.putExtra("sheetID", str);
				intent.putExtra("gameID", gameID);
				dialog.dismiss();
				startActivity(intent);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Cursor c = database.query(MySQLiteHelper.TABLE_SPREADSHEETS,
				new String[] { MySQLiteHelper.COLUMN_OWNER,
						MySQLiteHelper.COLUMN_SHEETID },
				MySQLiteHelper.COLUMN_SHEETID + " = " + sheetID, null, null,
				null, null);
		c.moveToFirst();
		String str = c.getString(c.getColumnIndex(MySQLiteHelper.COLUMN_OWNER));
		if (str.equals("1")) {
			// Inflate the menu; this adds items to the action bar if it is
			// present.
			getMenuInflater().inflate(R.menu.activity_show_game_details, menu);
		} else {
			// Inflate the menu; this adds items to the action bar if it is
			// present.
			getMenuInflater()
					.inflate(R.menu.activity_show_game_details_2, menu);
		}
		c.close();
		return super.onCreateOptionsMenu(menu);
	}
}
