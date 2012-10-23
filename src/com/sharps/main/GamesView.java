package com.sharps.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class GamesView extends ListActivity implements NetworkContentContainer,
		OnScrollListener {
	private ArrayList<HashMap<String, String>> content = new ArrayList<HashMap<String, String>>();
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	private String id;
	private ActionBar actionBar;
	boolean downloading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.games_view);
		
		getListView().setOnScrollListener(this);
		mediator.setContentContainer(this);
		Intent i = getIntent();
		id = i.getStringExtra("id");
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		if (mediator.getLibrary().getMySheets().get(id)
				.get("sheetGroup").equals("my")) {
			actionBar.addAction(new Action() {

				@Override
				public void performAction(View view) {
					// TODO Auto-generated method stub
					Intent myIntent = new Intent(GamesView.this, AddGame.class);
					myIntent.putExtra("id", id);
					GamesView.this.startActivity(myIntent);
				}

				@Override
				public int getDrawable() {
					// TODO Auto-generated method stub
					return R.drawable.ic_menu_add;
				}
			});
		}
		actionBar.setTitle(mediator.getLibrary().getMySheets().get(id)
				.get("title"));
		if (mediator.getLibrary().getGames().get(id) != null) {

			for (Hashtable<String, String> hashtable : mediator.getLibrary()
					.getGames().get(id)) {
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

		setListAdapter(new GameAdapter(this, content,
				android.R.layout.simple_list_item_2, from, to,id));
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(GamesView.this, ShowGames.class);
				myIntent.putExtra("id", id);
				myIntent.putExtra("index", arg2);
				GamesView.this.startActivity(myIntent);
			}

		});
	}

	@Override
	public void updateViewContent(ViewContent mode) {
		// TODO Auto-generated method stub
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
						android.R.layout.simple_list_item_2, from, to,id));
			} else {
				System.out.println("notify " + content.size());
				((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
				downloading = false;
				actionBar.setProgressBarVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
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
			actionBar.setProgressBarVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
