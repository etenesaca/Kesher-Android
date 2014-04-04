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
import android.widget.TextView;

import com.kemas.R;

/**
 * Custom adapter with "View Holder Pattern".
 * 
 * @author danielme.com
 * 
 */
@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class AttendancesItemAdapter extends ArrayAdapter<HashMap<String, Object>> {
	private LayoutInflater layoutInflater;
	private Context CTX;
	Typeface Roboto_Bold;
	Typeface Roboto_Light;

	public AttendancesItemAdapter(Context context, List<HashMap<String, Object>> objects) {
		super(context, 0, objects);
		this.CTX = context;
		layoutInflater = LayoutInflater.from(context);
		Roboto_Bold = Typeface.createFromAsset(CTX.getAssets(), "fonts/Roboto-Bold.ttf");
		Roboto_Light = Typeface.createFromAsset(CTX.getAssets(), "fonts/Roboto-Light.ttf");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// holder pattern
		AttendancesItem holder = null;
		if (convertView == null) {
			holder = new AttendancesItem();
			convertView = layoutInflater.inflate(R.layout.list_item_attendance, null);
			convertView.setTag(holder);
		} else {
			holder = (AttendancesItem) convertView.getTag();
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

		TextView tvService;
		TextView tvNumber;
		TextView tvType;
		TextView tvDate;
		TextView tvHour;
		TextView tvDay;

		public LoadView(View convertView, HashMap<String, Object> Record) {
			this.Record = Record;
			this.convertView = convertView;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			tvService = (TextView) convertView.findViewById(R.id.tvService);
			tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
			tvType = (TextView) convertView.findViewById(R.id.tvType);
			tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			tvHour = (TextView) convertView.findViewById(R.id.tvHour);
			tvDay = (TextView) convertView.findViewById(R.id.tvDay);

			tvService.setVisibility(View.INVISIBLE);
			tvNumber.setVisibility(View.INVISIBLE);
			tvType.setVisibility(View.INVISIBLE);
			tvDate.setVisibility(View.INVISIBLE);
			tvHour.setVisibility(View.INVISIBLE);
			tvDay.setVisibility(View.INVISIBLE);
		}

		@Override
		protected String doInBackground(String... arg0) {
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			tvService.setTypeface(Roboto_Bold);
			tvService.setText(Record.get("service").toString());

			tvNumber.setText("#" + Record.get("id").toString());
			tvDate.setText(Record.get("date").toString());
			tvHour.setText(Record.get("hour").toString());
			tvDay.setText(Record.get("day").toString());
			tvDay.setTypeface(Roboto_Light);
			if ((Record.get("type").toString()).equals("just_time")) {
				tvType.setText("A Tiempo");
				tvType.setBackgroundDrawable(CTX.getResources().getDrawable(R.drawable.shape_atiempo));
			} else if ((Record.get("type").toString()).equals("late")) {
				tvType.setText("Atrazo");
				tvType.setBackgroundDrawable(CTX.getResources().getDrawable(R.drawable.shape_tarde));
			} else {
				tvType.setText("Insistencia");
				tvType.setBackgroundDrawable(CTX.getResources().getDrawable(R.drawable.shape_falta));
			}

			tvService.setVisibility(View.VISIBLE);
			tvNumber.setVisibility(View.VISIBLE);
			tvType.setVisibility(View.VISIBLE);
			tvDate.setVisibility(View.VISIBLE);
			tvHour.setVisibility(View.VISIBLE);
			tvDay.setVisibility(View.VISIBLE);
		}
	}
}