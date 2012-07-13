package com.sharps.main;

import java.util.ArrayList;

import android.app.ListActivity;
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

import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class AddGame extends ListActivity {
	String id;
	private ListView myList;
	private MyAdapter myAdapter;
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_game);
		Intent i = getIntent();
		id = i.getStringExtra("id");
		Button button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(layGameButtonPushed);
		myList = (ListView) findViewById(android.R.id.list);
		myList.setItemsCanFocus(true);
		myAdapter = new MyAdapter();
		myList.setAdapter(myAdapter);

	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		public ArrayList<ListItem> myItems = new ArrayList<ListItem>();
		public MyAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			for (int i = 0; i < mediator.getKeys().length; i++) {
				ListItem listItem = new ListItem();
				listItem.caption = mediator.getKeys()[i];
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
			holder.caption.setHint(((ListItem)myItems.get(position)).caption);
			holder.caption.setId(position);
			// we need to update adapter once we finish with editing
			holder.caption
					.setOnFocusChangeListener(new OnFocusChangeListener() {
						public void onFocusChange(View v, boolean hasFocus) {
							if (!hasFocus) {
								final int position = v.getId();
								final EditText Caption = (EditText) v;
								((ListItem)myItems.get(position)).caption = Caption
										.getText().toString();
							}
						}
					});
			return convertView;
		}
	}
	private OnClickListener layGameButtonPushed = new OnClickListener() {

		public void onClick(View v) {
			mediator.layGame(myAdapter.myItems,id);
			finish();
		}

	};

}
