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

package com.kemas.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.R;
import com.kemas.hupernikao;
import com.kemas.item.adapters.AttendancesItemAdapter;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class EndlessListViewActivity extends Activity {
	private DataSourceAttendance DataSource;
	private static final int PAGESIZE = 15;
	private View footerView;
	private boolean loading = false;
	private ListAdapter CurrentAdapter;

	private TextView textViewDisplaying;
	private ListView lvAttendance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.endless);

		// Lineas para habilitar el acceso a la red
		// y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		lvAttendance = (ListView) findViewById(R.id.lvAttendanceList);
		textViewDisplaying = (TextView) findViewById(R.id.displaying);

		DataSource = new DataSourceAttendance(this);
		footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_list, null, false);
		lvAttendance.addFooterView(footerView, null, false);
		CurrentAdapter = new AttendancesItemAdapter(this, DataSource.getData(0, PAGESIZE));
		lvAttendance.setAdapter(CurrentAdapter);
		lvAttendance.removeFooterView(footerView);

		lvAttendance.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// nothing here
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.v("===============================", "===============================");
				Log.v("firstVisibleItem", firstVisibleItem + "");
				Log.v("visibleItemCount", visibleItemCount + "");
				Log.v("totalItemCount", totalItemCount + "");
				if (load(firstVisibleItem, visibleItemCount, totalItemCount)) {
					loading = true;
					lvAttendance.addFooterView(footerView, null, false);
					(new LoadNextPage()).execute("");
				}
			}
		});

		lvAttendance.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				Toast.makeText(EndlessListViewActivity.this, lvAttendance.getAdapter().getItem(position) + " " + getString(R.string.selected), Toast.LENGTH_SHORT).show();
			}
		});
		updateDisplayingTextView();
	}

	protected void updateDisplayingTextView() {
		textViewDisplaying = (TextView) findViewById(R.id.displaying);
		String text = getString(R.string.display);
		text = String.format(text, lvAttendance.getCount(), DataSource.getSize());
		textViewDisplaying.setText(text);
	}

	protected boolean load(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int aux = firstVisibleItem + visibleItemCount;
		boolean lastItem = aux == totalItemCount && lvAttendance.getChildAt(visibleItemCount - 1) != null && lvAttendance.getChildAt(visibleItemCount - 1).getBottom() <= lvAttendance.getHeight();
		boolean moreRows = lvAttendance.getAdapter().getCount() < DataSource.getSize();
		return moreRows && lastItem && !loading;
	}

	protected class LoadNextPage extends AsyncTask<String, Void, String> {
		private List<HashMap<String, Object>> newData = null;

		@Override
		protected String doInBackground(String... arg0) {
			newData = DataSource.getData(lvAttendance.getAdapter().getCount() - 1, PAGESIZE);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			AttendancesItemAdapter adp = (AttendancesItemAdapter) CurrentAdapter;
			for (HashMap<String, Object> value : newData) {
				adp.add(value);
			}
			adp.notifyDataSetChanged();
			CurrentAdapter = (ListAdapter) adp;
			lvAttendance.removeFooterView(footerView);
			updateDisplayingTextView();
			loading = false;
		}
	}

	public class DataSourceAttendance {
		private Configuration config;
		private OpenERP oerp_connection;
		private List<Long> data = null;
		private int SIZE = 0;

		public DataSourceAttendance(Context CTX) {
			config = new Configuration(CTX);
			oerp_connection = hupernikao.BuildOpenERPConnection(config);
			Long[] attendance_ids = oerp_connection.search("kemas.attendance", new Object[] {});
			SIZE = attendance_ids.length;
			data = new ArrayList<Long>(SIZE);
			for (Long id : attendance_ids) {
				data.add(id);
			}
		}

		public int getSize() {
			return SIZE;
		}

		public List<HashMap<String, Object>> getData(int offset, int limit) {
			List<HashMap<String, Object>> result = null;
			String[] fields_to_read = new String[] { "code" };
			List<Long> newList = new ArrayList<Long>(limit);

			int end = offset + limit;
			if (end > data.size()) {
				end = data.size();
			}
			newList.addAll(data.subList(offset, end));
			result = oerp_connection.read("kemas.attendance", newList, fields_to_read);
			return result;
		}

	}
}