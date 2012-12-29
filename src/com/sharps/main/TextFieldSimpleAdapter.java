package com.sharps.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sharps.R;

public class TextFieldSimpleAdapter extends SimpleAdapter implements
		OnFocusChangeListener {
	public ArrayList<String> getItemStrings() {
		ArrayList<String> list = new ArrayList<String>();
		for (Integer i : itemStrings.keySet()) {
			list.add(itemStrings.get(i));
		}
		return list;
	}

	private HashMap<Integer, String> itemStrings = new HashMap<Integer, String>();
	private Context context;

	public TextFieldSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		String line1 = ((HashMap<String, String>) this.getItem(position))
				.get("line1");
		String line2 = ((HashMap<String, String>) this.getItem(position))
				.get("line2");
		if (this.itemStrings.containsKey(position)) {
			line1 = itemStrings.get(position);
		}
		if (convertView == null
				|| convertView.getId() != R.layout.textfield_item) {
			/**
			 * Finns ingen vy som vi kan återanvända, skapar en ny!
			 */
			convertView = inflater.inflate(R.layout.textfield_item, parent,
					false);
			EditText editText = (EditText) convertView
					.findViewById(R.id.edit_text);

			if (line1.equals(line2)) {
				editText.setHint(line2);
				itemStrings.put(position, line2);
			} else {
				editText.setText(line1);
				itemStrings.put(position, line1);
			}
			TextView textView = (TextView) convertView
					.findViewById(android.R.id.text2);
			textView.setHint(line2);
			editText.setOnFocusChangeListener(this);
			editText.setId(position);
		}

		return convertView;
	}

	@Override
	public void onFocusChange(final View v, boolean hasFocus) {
		((EditText) v).addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				itemStrings.put(v.getId(), s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
}
