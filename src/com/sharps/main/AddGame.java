package com.sharps.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class AddGame extends ListActivity implements OnItemClickListener {
	public static final int GET_SEARCH_RESULTS = 0;
	String id;
	private ListView myList;
	private TextFieldSimpleAdapter myAdapter;
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	private ArrayList<String> searchResult = new ArrayList<String>();
	private ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
	private String[] from = { "line1", "line2" };
	private int[] to = { R.id.edit_text, android.R.id.text2 };

	private void generateContent() {
		listData.clear();
		String[] titles = mediator.getTitles();
		for (int i = 0; i < titles.length; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put(from[0], titles[i]);
			temp.put(from[1], titles[i]);
			listData.add(temp);
		}
	}

	private void generateContent(ArrayList<String> list) {
		listData.clear();
		String[] titles = mediator.getTitles();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put(from[0], list.get(i));
			temp.put(from[1], titles[i]);
			listData.add(temp);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_game);
		Intent i = getIntent();
		id = i.getStringExtra("id");
		String mode = i.getStringExtra("mode");
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(layGameButtonPushed);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.addAction(new Action() {

			@Override
			public void performAction(View view) {
				onSearchRequested();
			}

			@Override
			public int getDrawable() {
				return R.drawable.ic_menu_search;
			}
		});
		myList = (ListView) findViewById(android.R.id.list);
		myList.setItemsCanFocus(true);
		generateContent();
		if (savedInstanceState != null
				&& savedInstanceState.containsKey("game") && mode == null) {
			searchResult.addAll(savedInstanceState.getStringArrayList("game"));
			generateContent(searchResult);
		} else if (mode == null) {
			searchResult.addAll(Arrays.asList(mediator.getTitles()));
			generateContent(searchResult);
		} else {
			HashMap<String, String> game = (HashMap<String, String>) i
					.getSerializableExtra("rek");
			ArrayList<String> list = new ArrayList<String>();
			for (int j = 0; j < mediator.getKeys().length; j++) {
				String temp = game.get(mediator.getKeys()[j]);
				if (temp != null) {
					list.add(temp);
				} else {
					list.add(mediator.getTitles()[j]);
				}
			}
			searchResult.addAll(list);
			generateContent(searchResult);
		}
		myAdapter = new TextFieldSimpleAdapter(this, listData,
				R.layout.textfield_item, from, to);
		myList.setAdapter(myAdapter);
		myList.setOnItemClickListener(this);
		handleIntent(getIntent());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putStringArrayList("game", searchResult);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			searchResult = data.getStringArrayListExtra("result");
			generateContent(searchResult);
			myAdapter = new TextFieldSimpleAdapter(this, listData,
					R.layout.textfield_item, from, to);
			myList.setAdapter(myAdapter);
		}
	}

	private void handleIntent(Intent intent) {
		// Get the intent, verify the action and get the query
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			// manually launch the real search activity
			final Intent searchIntent = new Intent(getApplicationContext(),
					SearchResultsView.class);
			// add query to the Intent Extras
			searchIntent.putExtra(SearchManager.QUERY, query);
			startActivityForResult(searchIntent, GET_SEARCH_RESULTS);
			intent.setAction(null);
		}
	}

	private OnClickListener layGameButtonPushed = new OnClickListener() {

		public void onClick(View v) {
			mediator.layGame(myAdapter.getItemStrings(), id);
			finish();
		}

	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		EditText editText = (EditText) arg1.findViewById(R.id.edit_text);
		if (editText != null) {
			editText.requestFocus();
		}
	}

}
