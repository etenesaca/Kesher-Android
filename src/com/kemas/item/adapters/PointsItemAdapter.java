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
import android.os.AsyncTask;
import android.os.Build;
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
	Typeface Roboto_Bold;
	Typeface Roboto_Light;

	public PointsItemAdapter(Context context, List<HashMap<String, Object>> objects) {
		super(context, 0, objects);
		this.CTX = context;
		layoutInflater = LayoutInflater.from(context);
		Roboto_Bold = Typeface.createFromAsset(CTX.getAssets(), "fonts/Roboto-Bold.ttf");
		Roboto_Light = Typeface.createFromAsset(CTX.getAssets(), "fonts/Roboto-Light.ttf");
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
		// Ejecutar la Tarea de acuerdo a la version de Android
		LoadView Task = new LoadView(convertView, Record);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			Task.execute();
		}
		return convertView;
	}

	/** Clase Asincrona para recuperar los datos de la fila **/
	protected class LoadView extends AsyncTask<String, Void, String> {
		HashMap<String, Object> Record;
		View convertView;

		ImageView imgType;
		TextView tvDate;
		TextView tvHour;
		TextView tvDay;
		TextView tvPoints;

		public LoadView(View convertView, HashMap<String, Object> Record) {
			this.Record = Record;
			this.convertView = convertView;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			imgType = (ImageView) convertView.findViewById(R.id.imgType);
			tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			tvHour = (TextView) convertView.findViewById(R.id.tvHour);
			tvDay = (TextView) convertView.findViewById(R.id.tvDay);
			tvPoints = (TextView) convertView.findViewById(R.id.tvPoints);

			imgType.setVisibility(View.INVISIBLE);
			tvDate.setVisibility(View.INVISIBLE);
			tvHour.setVisibility(View.INVISIBLE);
			tvDay.setVisibility(View.INVISIBLE);
			tvPoints.setVisibility(View.INVISIBLE);
		}

		@Override
		protected String doInBackground(String... arg0) {
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
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

			imgType.setVisibility(View.VISIBLE);
			tvDate.setVisibility(View.VISIBLE);
			tvHour.setVisibility(View.VISIBLE);
			tvDay.setVisibility(View.VISIBLE);
			tvPoints.setVisibility(View.VISIBLE);
		}
	}
}