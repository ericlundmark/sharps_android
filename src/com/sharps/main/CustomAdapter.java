package com.sharps.main;

import java.util.List;
import java.util.Map;

import com.sharps.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CustomAdapter extends SimpleAdapter {

	public CustomAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = super.getView(position, convertView, parent);
		String string=((TextView)view.findViewById(R.id.list_complex_caption)).getText().toString();
		string=string.substring(0, string.length()-1);
		if (Double.parseDouble(string)>=100) {
			((TextView)view.findViewById(R.id.list_complex_caption)).setTextColor(Color.GREEN);
		}else if(Double.parseDouble(string)<100&&Double.parseDouble(string)!=0){
			((TextView)view.findViewById(R.id.list_complex_caption)).setTextColor(Color.RED);
		}else{
			((TextView)view.findViewById(R.id.list_complex_caption)).setTextColor(Color.GRAY);
		}
		  return view;
	}

}
