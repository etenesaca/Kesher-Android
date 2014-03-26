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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kemas.R;

/**
 * Custom adapter with "View Holder Pattern".
 * 
 * @author danielme.com
 * 
 */
@SuppressLint({ "NewApi" })
public class PointsItemAdapter extends ArrayAdapter<HashMap<String, Object>> {
	private LayoutInflater layoutInflater;
	private Context CTX;

	public PointsItemAdapter(Context context, List<HashMap<String, Object>> objects) {
		super(context, 0, objects);
		this.CTX = context;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// holder pattern
		PointsItem holder = null;
		if (convertView == null) {
			holder = new PointsItem();
			convertView = layoutInflater.inflate(R.layout.list_item_point, null);
			convertView.setTag(holder);
		} else {
			holder = (PointsItem) convertView.getTag();
		}

		HashMap<String, Object> Record = getItem(position);

		ImageView imgType = (ImageView) convertView.findViewById(R.id.imgType);
		TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
		TextView tvHour = (TextView) convertView.findViewById(R.id.tvHour);
		TextView tvDay = (TextView) convertView.findViewById(R.id.tvDay);
		TextView tvPoints = (TextView) convertView.findViewById(R.id.tvPoints);

		Typeface Roboto_Light = Typeface.createFromAsset(CTX.getAssets(), "fonts/Roboto-Light.ttf");
		Typeface Roboto_Bold = Typeface.createFromAsset(CTX.getAssets(), "fonts/Roboto-Bold.ttf");

		tvDate.setText(Record.get("date").toString());
		tvHour.setText(Record.get("hour").toString());
		tvPoints.setText(Record.get("points").toString());
		tvPoints.setTypeface(Roboto_Bold);
		tvDay.setText(Record.get("day").toString());
		tvDay.setTypeface(Roboto_Light);

		String PointsType = Record.get("type").toString();
		if (PointsType.equals("increase")) {
			tvPoints.setTextColor(CTX.getResources().getColor(R.color.Black));
			imgType.setImageDrawable(CTX.getResources().getDrawable(R.drawable.add));
		} else if (PointsType.equals("decrease")) {
			tvPoints.setTextColor(CTX.getResources().getColor(R.color.Red));
			imgType.setImageDrawable(CTX.getResources().getDrawable(R.drawable.remove));
		} else if (PointsType.equals("init")) {
			tvPoints.setTextColor(CTX.getResources().getColor(R.color.Green));
			imgType.setImageDrawable(CTX.getResources().getDrawable(R.drawable.ok));
		}
		return convertView;
	}
}