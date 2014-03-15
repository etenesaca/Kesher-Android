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

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kemas.R;
import com.kemas.item.adapters.AttendancesItemAdapter;

public class EndlessListViewActivity extends AbstractListViewActivity {
	private ListView lvAttendance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.endless);

		datasource = new DataSourceAttendance(this);
		footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_list, null, false);
		getListView().addFooterView(footerView, null, false);
		setListAdapter(new AttendancesItemAdapter(this, datasource.getData(0, PAGESIZE)));
		getListView().removeFooterView(footerView);

		getListView().setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// nothing here
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (load(firstVisibleItem, visibleItemCount, totalItemCount)) {
					loading = true;
					getListView().addFooterView(footerView, null, false);
					(new LoadNextPage()).execute("");
				}
			}
		});
		updateDisplayingTextView();
	}

	protected void updateDisplayingTextView() {
		textViewDisplaying = (TextView) findViewById(R.id.displaying);
		String text = getString(R.string.display);
		text = String.format(text, getListAdapter().getCount(), datasource.getSize());
		textViewDisplaying.setText(text);
	}

	protected boolean load(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		boolean lastItem = firstVisibleItem + visibleItemCount == totalItemCount && getListView().getChildAt(visibleItemCount - 1) != null
				&& getListView().getChildAt(visibleItemCount - 1).getBottom() <= getListView().getHeight();
		boolean moreRows = getListAdapter().getCount() < datasource.getSize();
		return moreRows && lastItem && !loading;

	}

	protected class LoadNextPage extends AsyncTask<String, Void, String> {
		private List<HashMap<String, Object>> newData = null;

		@Override
		protected String doInBackground(String... arg0) {
			newData = datasource.getData(getListAdapter().getCount(), PAGESIZE);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			AttendancesItemAdapter customArrayAdapter = ((AttendancesItemAdapter) getListAdapter());
			for (HashMap<String, Object> value : newData) {
				customArrayAdapter.add(value);
			}
			customArrayAdapter.notifyDataSetChanged();

			getListView().removeFooterView(footerView);
			updateDisplayingTextView();
			loading = false;
		}
	}
}