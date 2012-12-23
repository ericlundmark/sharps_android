package com.sharps.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.cellr.noid.actionbar.ActionBarListActivity;
import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class GamesActivity extends ActionBarListActivity implements
		NetworkContentContainer, OnScrollListener {
	private ArrayList<HashMap<String, String>> content = new ArrayList<HashMap<String, String>>();
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	private String id;
	boolean downloading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.games_view);

		getListView().setOnScrollListener(this);
		mediator.setContentContainer(this);
		Intent i = getIntent();
		id = i.getStringExtra("id");

		if (mediator.getLibrary().getGames().get(id) != null) {

			for (Hashtable<String, String> hashtable : mediator.getLibrary()
					.getGames().get(id)) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("line1",
						hashtable.get("team1") + "-" + hashtable.get("team2"));
				hashMap.put("line2", "Insats: " + hashtable.get("amount")
						+ " Odds: " + hashtable.get("odds") + " Netto: "
						+ hashtable.get("result"));
				hashMap.put("id", id);
				content.add(hashMap);
			}

		}
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };

		setListAdapter(new GameAdapter(this, content,
				android.R.layout.simple_list_item_2, from, to, id));
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent myIntent = new Intent(GamesActivity.this,
						ShowGameDetailsActivity.class);
				myIntent.putExtra("id", id);
				myIntent.putExtra("index", arg2);
				GamesActivity.this.startActivity(myIntent);
			}

		});
	}

	@Override
	public void updateViewContent(ViewContent mode) {
		if (mode == ViewContent.GAMES) {
			System.out.println(mediator.getLibrary().getGames().get(id));
			if (mediator.getLibrary().getGames().get(id) != null) {
				content.clear();
				for (Hashtable<String, String> hashtable : mediator
						.getLibrary().getGames().get(id)) {
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("line1", hashtable.get("team1") + "-"
							+ hashtable.get("team2"));
					hashMap.put("line2", "Insats: " + hashtable.get("amount")
							+ " Odds: " + hashtable.get("odds") + " Netto: "
							+ hashtable.get("result"));
					hashMap.put("id", id);
					content.add(hashMap);
				}

			}
			String[] from = { "line1", "line2" };
			int[] to = { android.R.id.text1, android.R.id.text2 };
			if (getListAdapter() == null) {
				setListAdapter(new GameAdapter(this, content,
						android.R.layout.simple_list_item_2, from, to, id));
			} else {
				System.out.println("notify " + content.size());
				((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
				downloading = false;
				getActionBarHelper().setRefreshActionItemState(false);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		boolean loadMore = false;
		if (mediator.getLibrary().getMySheets().get(id) != null) {
			loadMore = /* maybe add a padding */
			firstVisibleItem + visibleItemCount >= totalItemCount
					&& totalItemCount < Integer.parseInt(mediator.getLibrary()
							.getMySheets().get(id).get("numberOfGames"));
		}

		if (loadMore && !downloading) {
			downloading = true;
			System.out.println("load more " + (content.size() + 1) / 10);
			mediator.downloadNextGames(id);
			getActionBarHelper().setRefreshActionItemState(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.addGame:
			Intent myIntent = new Intent(GamesActivity.this, AddGameActivity.class);
			myIntent.putExtra("id", id);
			startActivity(myIntent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.activity_add_game	, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}
}
