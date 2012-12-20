package com.sharps.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class SpreadsheetView extends ListActivity implements
		NetworkContentContainer {
	NetworkMediator mediator = NetworkMediator.getSingletonObject();
	private ArrayList<HashMap<String, String>> myContent = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> favContent = new ArrayList<HashMap<String, String>>();
	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spreadsheets_view);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		// You can also assign the title programmatically by passing a
		// CharSequence or resource id.
		// actionBar.setTitle(R.string.some_title);
		actionBar.addAction(new Action() {

			@Override
			public void performAction(View view) {
				// TODO Auto-generated method stub
				mediator.refreshSheets();
			}

			@Override
			public int getDrawable() {
				return R.drawable.ic_menu_refresh;
				// TODO Auto-generated method stub
			}
		});
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				System.out.println(arg2);
				Intent myIntent = new Intent(SpreadsheetView.this,
						GamesActivity.class);
				ArrayList<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
				temp.addAll(myContent);
				temp.addAll(favContent);
				if (arg2 < myContent.size() + 1) {
					myIntent.putExtra("id", temp.get(arg2 - 1).get("id"));
				} else {
					myIntent.putExtra("id", temp.get(arg2 - 2).get("id"));
				}

				SpreadsheetView.this.startActivity(myIntent);
			}

		});
		mediator.setContentContainer(this);
		mediator.downloadSpreadsheets();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!mediator.isLoggedIn()) {
			AlertDialog ballarUr=new AlertDialog.Builder(this).create();
			ballarUr.setMessage("Utloggad");
			ballarUr.setButton("OK", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
				   finish();
					Intent myIntent = new Intent(SpreadsheetView.this,
							LoginActivity.class);
					SpreadsheetView.this.startActivity(myIntent);
			   }
			});
			ballarUr.show();
		}
	}
	@Override
	public void updateViewContent(ViewContent mode) {
		
		// TODO Auto-generated method stub
		if (mode == ViewContent.SPREADSHEETS) {
			myContent.clear();
			favContent.clear();
			if (mediator.getLibrary().getMySheets().size() != 0) {
				for (String key : mediator.getLibrary().getMySheets().keySet()) {
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put(ITEM_TITLE, mediator.getLibrary().getMySheets()
							.get(key).get("title"));
					hashMap.put(ITEM_CAPTION, mediator.getLibrary()
							.getMySheets().get(key).get("roi")
							+ "%");
					hashMap.put("id", key);
					if (mediator.getLibrary().getMySheets().get(key)
							.get("sheetGroup").equals("my")) {
						myContent.add(hashMap);
					} else {
						favContent.add(hashMap);
					}

				}

				Comparator<HashMap<String, String>> com = new Comparator<HashMap<String, String>>() {
					@Override
					public int compare(HashMap<String, String> lhs,
							HashMap<String, String> rhs) {
						// TODO Auto-generated method stub
						String s = mediator.getLibrary().getMySheets()
								.get(lhs.get("id")).get("lastadded");
						String p = mediator.getLibrary().getMySheets()
								.get(rhs.get("id")).get("lastadded");
						return Integer.parseInt(p) - Integer.parseInt(s);
					}
				};
				Collections.sort(myContent, com);
				Collections.sort(favContent, com);

			}
			if (getListAdapter() == null) {
				SeparatedListAdapter adapter = new SeparatedListAdapter(this);
				adapter.addSection("Mina sheets", new CustomAdapter(this,
						myContent, R.layout.list_complex, new String[] {
								ITEM_TITLE, ITEM_CAPTION }, new int[] {
								R.id.list_complex_title,
								R.id.list_complex_caption }));
				adapter.addSection("Favorit sheets", new CustomAdapter(this,
						favContent, R.layout.list_complex, new String[] {
								ITEM_TITLE, ITEM_CAPTION }, new int[] {
								R.id.list_complex_title,
								R.id.list_complex_caption }));
				setListAdapter(adapter);
			} else {
				System.out.println("notify");
				((SeparatedListAdapter) getListAdapter())
						.notifyDataSetChanged();
			}

		}
	}
}
