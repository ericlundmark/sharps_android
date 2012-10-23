package com.sharps.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class ShowGames extends ListActivity {
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
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		if (mediator.getLibrary().getMySheets().get(id)
				.get("sheetGroup").equals("my")) {
			actionBar.addAction(new Action() {
				
				@Override
				public void performAction(View view) {
					// TODO Auto-generated method stub
					mediator.setResultToGame(id,"win",game);
					game.put("result", String.valueOf(Double.parseDouble(game.get("amount"))*Double.parseDouble(game.get("odds"))-Double.parseDouble(game.get("amount"))));
					reloadContent();
					getListView().invalidateViews();
				}
				
				@Override
				public int getDrawable() {
					// TODO Auto-generated method stub
					return R.drawable.win;
				}
			});
			actionBar.addAction(new Action() {
				
				@Override
				public void performAction(View view) {
					// TODO Auto-generated method stub
					mediator.setResultToGame(id,"push",game);
					game.put("result","0");
					reloadContent();
					getListView().invalidateViews();
				}
				
				@Override
				public int getDrawable() {
					// TODO Auto-generated method stub
					return R.drawable.push;
				}
			});
			actionBar.addAction(new Action() {
				
				@Override
				public void performAction(View view) {
					// TODO Auto-generated method stub
					mediator.setResultToGame(id,"loss",game);
					game.put("result", "-"+String.valueOf(Double.parseDouble(game.get("amount"))*Double.parseDouble(game.get("odds"))-Double.parseDouble(game.get("amount"))));
					reloadContent();
					getListView().invalidateViews();
				}
				
				@Override
				public int getDrawable() {
					// TODO Auto-generated method stub
					return R.drawable.loss;
				}
			});
		}else{
			actionBar.addAction(new Action() {
				private ArrayList<String> mySheets=new ArrayList<String>();
				private ArrayList<String> sheetid= new ArrayList<String>();
				@Override
				public void performAction(View view) {
					// TODO Auto-generated method stub
					mySheets.clear();
					sheetid.clear();
					for (String string : mediator.getLibrary().getMySheets().keySet()) {
						if (mediator.getLibrary().getMySheets().get(string).get("sheetGroup").equals("my")) {
							mySheets.add(mediator.getLibrary().getMySheets().get(string).get("title"));
							sheetid.add(mediator.getLibrary().getMySheets().get(string).get("id"));
						}
					}
					
					AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
					builder.setTitle("VŠlj spreadsheet");
					ListView modeList = new ListView(getContext());
					ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1,mySheets);
					modeList.setAdapter(modeAdapter);
					builder.setView(modeList);
					final Dialog dialog = builder.create();
					modeList.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							Intent myIntent = new Intent(ShowGames.this, AddGame.class);
							myIntent.putExtra("id", sheetid.get(arg2));
							myIntent.putExtra("rek",game);
							myIntent.putExtra("mode", "rek");
							dialog.dismiss();
							ShowGames.this.startActivity(myIntent);
						}
						
					});

					

					dialog.show();
					
				}

				@Override
				public int getDrawable() {
					// TODO Auto-generated method stub
					return R.drawable.ic_menu_add;
				}
			});
		}
		index=intent.getIntExtra("index", -1);
		game=mediator.getLibrary().getGames().get(id).get(index);
		reloadContent();
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		setListAdapter(new ShowGameAdapter(this, content,
				android.R.layout.simple_list_item_2, from, to,game));
	}
	private Context getContext(){
		return this;
	}
	private void reloadContent(){
		content.clear();
		if (mediator.getLibrary().getGames().get(id) != null) {
			Hashtable<String, String> hashtable = mediator.getLibrary()
					.getGames().get(id).get(index);
			for (int i=0;i<mediator.getKeys().length;i++) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				if (hashtable.get(mediator.getKeys()[i])!=null) {
					hashMap.put("line1", hashtable.get(mediator.getKeys()[i]));
					hashMap.put("line2", mediator.getTitles()[i]);
					content.add(hashMap);
				}
			}
		}
	}
}
