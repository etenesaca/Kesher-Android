/*
 * Copyright (C) 2012 Daniel Medina <http://danielme.com>
 * 
 * This file is part of "Android Paginated ListView Demo".
 * 
 * "Android Paginated ListView Demo" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * "Android Paginated ListView Demo" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 3
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0.html/>
 */
package com.kemas.item.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kemas.R;

/**
 * Custom adapter with "View Holder Pattern".
 * 
 * @author danielme.com
 * 
 */
public class AttendancesItemAdapter extends ArrayAdapter<String> {
	private LayoutInflater layoutInflater;

	public AttendancesItemAdapter(Context context, List<HashMap<String, Object>> list) {
		super(context, 0);
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// holder pattern
		AttendancesItem holder = null;
		if (convertView == null) {
			holder = new AttendancesItem();

			convertView = layoutInflater.inflate(R.layout.list_item_attendance, null);
			holder.setTextView1((TextView) convertView.findViewById(R.id.textView1));
			holder.setTextView2((TextView) convertView.findViewById(R.id.textView2));
			convertView.setTag(holder);
		} else {
			holder = (AttendancesItem) convertView.getTag();
		}

		holder.getTextView1().setText(getItem(position));
		holder.getTextView2().setText(getItem(position));
		return convertView;
	}

}