package com.sharps.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class SearchResultsView extends ListActivity implements Searchable {
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	private ArrayList<Hashtable<String, String>> result;
	private ArrayList<HashMap<String, String>> content = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_view);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ArrayList<String> arrayList=new ArrayList<String>();
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
				setResult(RESULT_OK, getIntent().putExtra("result", arrayList));
				finish();
			}

		});
		Intent intent = getIntent();
		mediator.setSearchable(this);
		String query = intent.getStringExtra(SearchManager.QUERY);
		mediator.searchGames(query);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void searchFinished(ArrayList<Hashtable<String, String>> result) {
		// TODO Auto-generated method stub
		this.result = result;
		this.updateViewContent();
	}

	public void updateViewContent() {
		// TODO Auto-generated method stub
		System.out.println(result);

		for (Hashtable<String, String> hashtable : result) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("line1",
					hashtable.get("team1") + "-" + hashtable.get("team2"));
			hashMap.put("line2", "Datum: " + hashtable.get("date") + " Tid: "
					+ hashtable.get("time"));
			content.add(hashMap);
		}
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		setListAdapter(new SimpleAdapter(this, content,
				android.R.layout.simple_list_item_2, from, to));

	}
}
