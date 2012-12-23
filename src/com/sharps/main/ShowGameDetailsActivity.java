package com.sharps.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cellr.noid.actionbar.ActionBarListActivity;
import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class ShowGameDetailsActivity extends ActionBarListActivity {
	NetworkMediator mediator = NetworkMediator.getSingletonObject();
	ArrayList<HashMap<String, String>> content = new ArrayList<HashMap<String, String>>();
	String id;
	int index;
	Hashtable<String, String> game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_game);
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		index = intent.getIntExtra("index", -1);
		game = mediator.getLibrary().getGames().get(id).get(index);
		reloadContent();
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		setListAdapter(new ShowGameAdapter(this, content,
				android.R.layout.simple_list_item_2, from, to, game));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.win:
			mediator.setResultToGame(id, "win", game);
			game.put(
					"result",
					String.valueOf(Double.parseDouble(game.get("amount"))
							* Double.parseDouble(game.get("odds"))
							- Double.parseDouble(game.get("amount"))));
			reloadContent();
			getListView().invalidateViews();
			break;
		case R.id.push:
			mediator.setResultToGame(id, "push", game);
			game.put("result", "0");
			reloadContent();
			getListView().invalidateViews();
			break;
		case R.id.loss:
			mediator.setResultToGame(id, "loss", game);
			game.put(
					"result",
					"-"
							+ String.valueOf(Double.parseDouble(game
									.get("amount"))
									* Double.parseDouble(game.get("odds"))
									- Double.parseDouble(game.get("amount"))));
			reloadContent();
			getListView().invalidateViews();
			break;
		case R.id.addGame:
			final ArrayList<String> mySheets = new ArrayList<String>();
			final ArrayList<String> sheetid = new ArrayList<String>();
			mySheets.clear();
			sheetid.clear();
			for (String string : mediator.getLibrary().getMySheets().keySet()) {
				if (mediator.getLibrary().getMySheets().get(string)
						.get("sheetGroup").equals("my")) {
					mySheets.add(mediator.getLibrary().getMySheets()
							.get(string).get("title"));
					sheetid.add(mediator.getLibrary().getMySheets().get(string)
							.get("id"));
				}
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setTitle("Välj spreadsheet");
			ListView modeList = new ListView(getContext());
			ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(
					getContext(), android.R.layout.simple_list_item_1,
					android.R.id.text1, mySheets);
			modeList.setAdapter(modeAdapter);
			builder.setView(modeList);
			final Dialog dialog = builder.create();
			modeList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent myIntent = new Intent(ShowGameDetailsActivity.this,
							AddGameActivity.class);
					myIntent.putExtra("id", sheetid.get(arg2));
					myIntent.putExtra("rek", game);
					myIntent.putExtra("mode", "rek");
					dialog.dismiss();
					ShowGameDetailsActivity.this.startActivity(myIntent);
				}

			});

			dialog.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mediator.getLibrary().getMySheets().get(id).get("sheetGroup")
				.equals("my")) {
			// Inflate the menu; this adds items to the action bar if it is
			// present.
			getMenuInflater().inflate(R.menu.activity_show_game_details, menu);
		} else {
			// Inflate the menu; this adds items to the action bar if it is
			// present.
			getMenuInflater().inflate(R.menu.activity_add_game, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	private Context getContext() {
		return this;
	}

	private void reloadContent() {
		content.clear();
		if (mediator.getLibrary().getGames().get(id) != null) {
			Hashtable<String, String> hashtable = mediator.getLibrary()
					.getGames().get(id).get(index);
			for (int i = 0; i < mediator.getKeys().length; i++) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				if (hashtable.get(mediator.getKeys()[i]) != null) {
					hashMap.put("line1", hashtable.get(mediator.getKeys()[i]));
					hashMap.put("line2", mediator.getTitles()[i]);
					content.add(hashMap);
				}
			}
		}
	}
}
