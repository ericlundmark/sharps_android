package com.sharps.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class AddGame extends ListActivity {
	public static final int GET_SEARCH_RESULTS = 0;
	String id;
	private ListView myList;
	private MyAdapter myAdapter;
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	private ArrayList<String> searchResult=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_game);
		Intent i = getIntent();
		id = i.getStringExtra("id");
		String mode=i.getStringExtra("mode");
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(layGameButtonPushed);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.addAction(new Action() {
			
			@Override
			public void performAction(View view) {
				// TODO Auto-generated method stub
				onSearchRequested();
			}
			
			@Override
			public int getDrawable() {
				// TODO Auto-generated method stub
				return R.drawable.ic_menu_search;
			}
		});
		myList = (ListView) findViewById(android.R.id.list);
		myList.setItemsCanFocus(true);
		if (savedInstanceState!=null&&savedInstanceState.containsKey("game")&&mode==null) {
			searchResult.addAll(savedInstanceState.getStringArrayList("game"));
		}else if(mode==null){
			searchResult.addAll(Arrays.asList(mediator.getTitles()));
		}else{
			HashMap<String, String> game=(HashMap<String, String>) i.getSerializableExtra("rek");
			ArrayList<String> list=new ArrayList<String>();
			for (int j = 0; j < mediator.getKeys().length; j++) {
				String temp=game.get(mediator.getKeys()[j]);
				if(temp!=null){
					list.add(temp);
				}else{
					list.add(mediator.getTitles()[j]);
				}
			}
			searchResult.addAll(list);
		}
		myAdapter = new MyAdapter(searchResult);
		myList.setAdapter(myAdapter);
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
			searchResult=data.getStringArrayListExtra("result");
			myAdapter=new MyAdapter(searchResult);
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
			startActivityForResult(searchIntent,GET_SEARCH_RESULTS);
			intent.setAction(null);
		}
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		public ArrayList<ListItem> myItems = new ArrayList<ListItem>();
		public MyAdapter(ArrayList<String> textLabels) {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			for (int i = 0; i < textLabels.size(); i++) {
				ListItem listItem = new ListItem();
				listItem.caption = textLabels.get(i);
				myItems.add(listItem);
			}
			notifyDataSetChanged();
		}

		public int getCount() {
			return myItems.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			EditText caption;
		}

		public class ListItem {
			public String caption;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item, null);
				holder.caption = (EditText) convertView
						.findViewById(R.id.ItemCaption);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// Fill EditText with the value you have in data source
			if (myItems.get(position).caption.equals(mediator.getTitles()[position])) {
				holder.caption.setHint(((ListItem) myItems.get(position)).caption);
			}else{
				holder.caption.setText(((ListItem)myItems.get(position)).caption);
			}
			
			holder.caption.setId(position);
			// we need to update adapter once we finish with editing
			holder.caption
					.setOnFocusChangeListener(new OnFocusChangeListener() {
						public void onFocusChange(View v, boolean hasFocus) {
							if (!hasFocus) {
								final int position = v.getId();
								final EditText Caption = (EditText) v;
								((ListItem) myItems.get(position)).caption = Caption
										.getText().toString();
							}
						}
					});
			return convertView;
		}
	}

	private OnClickListener layGameButtonPushed = new OnClickListener() {

		public void onClick(View v) {
			mediator.layGame(myAdapter.myItems, id);
			finish();
		}

	};

}
