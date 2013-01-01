package com.sharps.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import Database.MySQLiteHelper;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cellr.noid.actionbar.ActionBarListActivity;
import com.sharps.R;
import com.sharps.Network.GameAdder;
import com.sharps.Network.SearchGamesGetter;

public class AddGameActivity extends ActionBarListActivity implements
		OnItemClickListener {
	private String sheetID;
	private String gameID;
	private ListView myList;
	private TextFieldSimpleAdapter adapter;
	private String[] from = { "line1", "line2" };
	private int[] to = { R.id.edit_text, android.R.id.text2 };
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
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_game);
		database = ((MyApplication) getApplication()).getDatabase();
		myList = (ListView) findViewById(android.R.id.list);
		myList.setItemsCanFocus(true);
		Intent intent = getIntent();
		gameID = intent.getStringExtra("gameID");
		sheetID = intent.getStringExtra("sheetID");
		selection = MySQLiteHelper.COLUMN_GAMEID + " = " + gameID;
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(layGameButtonPushed);
		if (gameID != null) {
			adapter = new TextFieldSimpleAdapter(getApplicationContext(),
					getData(), R.layout.textfield_item, from, to);
			setListAdapter(adapter);
		} else {
			adapter = new TextFieldSimpleAdapter(getApplicationContext(),
					getEmptyData(), R.layout.textfield_item, from, to);
			setListAdapter(adapter);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}
	}

	private ArrayList<HashMap<String, String>> getData() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_GAMES, allColumns,
				selection, null, null, null, null);
		cursor.moveToFirst();
		for (int i = 0; i < allColumns.length; i++) {
			String str = cursor.getString(cursor.getColumnIndex(allColumns[i]));
			if (!str.equals("")) {
				String line1 = cursor.getString(cursor
						.getColumnIndex(allColumns[i]));
				HashMap<String, String> map = new HashMap<String, String>();
				if (line1.equals("")) {
					map.put(from[0], allColumns[i]);
				} else {
					map.put(from[0], line1);
				}

				map.put(from[1], allColumns[i]);
				list.add(map);
			}
		}
		cursor.close();
		return list;
	}

	private void doSearch(String query) {
		SearchGamesGetter searchGamesGetter = new SearchGamesGetter(
				"http://www.sharps.se/forums/includes/ss/app_infoga.php?letters="
						+ query) {
			@Override
			protected void onPostExecute(
					final ArrayList<Hashtable<String, String>> result) {
				super.onPostExecute(result);
				final ListView listView = (ListView) findViewById(R.id.searchResultsList);
				ArrayList<HashMap<String, String>> content = new ArrayList<HashMap<String, String>>();
				for (Hashtable<String, String> hashtable : result) {
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("line1", hashtable.get("team1") + "-"
							+ hashtable.get("team2"));
					hashMap.put("line2", "Datum: " + hashtable.get("date")
							+ " Tid: " + hashtable.get("time"));
					content.add(hashMap);
				}
				SimpleAdapter adapter = new SimpleAdapter(
						getApplicationContext(), content,
						R.layout.simple_list_item_2_black_text, new String[] {
								"line1", "line2" }, new int[] {
								android.R.id.text1, android.R.id.text2 });
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						ArrayList<String> arrayList = new ArrayList<String>();
						arrayList.add(result.get(arg2).get("team1"));
						arrayList.add(result.get(arg2).get("team2"));
						arrayList.add(result.get(arg2).get("date"));
						arrayList.add(result.get(arg2).get("time"));
						arrayList.add("Tecken");
						arrayList.add("Tecken2");
						arrayList.add(result.get(arg2).get("sport"));
						arrayList.add(result.get(arg2).get("country"));
						arrayList.add(result.get(arg2).get("league"));
						arrayList.add("Bolag");
						arrayList.add("Period");
						arrayList.add("Info");
						arrayList.add("Rekare");
						arrayList.add("Insats");
						arrayList.add("Odds");
						arrayList.add("Netto");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								listView.setVisibility(View.GONE);
								myList.setVisibility(View.VISIBLE);
								button.setVisibility(View.VISIBLE);
							}
						});
						TextFieldSimpleAdapter adapter = new TextFieldSimpleAdapter(
								getApplicationContext(),
								getDataFromList(arrayList),
								R.layout.textfield_item, from, to);
						setListAdapter(adapter);

					}

				});
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listView.setVisibility(View.VISIBLE);
						myList.setVisibility(View.GONE);
						button.setVisibility(View.GONE);
					}
				});
			}
		};
	}

	private ArrayList<HashMap<String, String>> getDataFromList(
			ArrayList<String> arrayList) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < allColumns.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			String line1 = arrayList.get(i);
			if (line1 == null) {
				map.put(from[0], allColumns[i]);
			} else {
				map.put(from[0], line1);
			}
			map.put(from[1], allColumns[i]);
			list.add(map);
		}
		return list;
	}

	private ArrayList<HashMap<String, String>> getEmptyData() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < allColumns.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[0], allColumns[i]);
			map.put(from[1], allColumns[i]);
			list.add(map);
		}
		return list;
	}

	private OnClickListener layGameButtonPushed = new OnClickListener() {

		@Override
		public void onClick(View v) {
			layGame(((TextFieldSimpleAdapter) getListAdapter())
					.getItemStrings(),
					sheetID);
			finish();
		}

	};

	private void layGame(ArrayList<String> itemStrings, String sheetID) {
		new Thread(new GameAdder(itemStrings, sheetID)).start();

	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_search:
			onSearchRequested();
			break;
		}
		return super.onOptionsItemSelected(item);
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_game, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		EditText editText = (EditText) arg1.findViewById(R.id.edit_text);
		if (editText != null) {
			editText.requestFocus();
		}
	}

}
