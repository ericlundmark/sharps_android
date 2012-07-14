package com.sharps.main;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sharps.Network.NetworkMediator;

public class GameAdapter extends SimpleAdapter {
	private NetworkMediator mediator=NetworkMediator.getSingletonObject();
	private String id;
	public GameAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,String id) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		this.id=id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = super.getView(position, convertView, parent);
		Hashtable<String, String> game=mediator.getLibrary().getGames().get(id).get(position);
		double d=Double.parseDouble(game.get("Netto"));
		if (d>0) {
			((TextView) view.findViewById(android.R.id.text2))
					.setTextColor(Color.GREEN);
		} else if (d < 0) {
			((TextView) view.findViewById(android.R.id.text2))
					.setTextColor(Color.RED);
		}else{
			((TextView) view.findViewById(android.R.id.text2))
			.setTextColor(Color.GRAY);
		}
		return view;
	}

}
