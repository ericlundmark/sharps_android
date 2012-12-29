package com.sharps.main;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

public abstract class MyAdapter extends SimpleCursorAdapter {

	public MyAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);
		TextView list_item = (TextView) view.findViewById(android.R.id.text2);
		list_item.setTextColor(getColor(cursor));
	}

	public abstract int getColor(Cursor c);

}
