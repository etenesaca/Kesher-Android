package com.kemas.fragments;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kemas.R;
import com.kemas.activities.PointsDetailActivity;
import com.kemas.datasources.DataSourcePoint;
import com.kemas.item.adapters.PointsItemAdapter;

/*  Fragment para ver las asistencias */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PointsFragment extends Fragment {
	private DataSourcePoint DataSource;
	private static final int PAGESIZE = 15;
	private View footerView;
	private boolean loading = false;
	private boolean ScrollComplete = false;
	private ListAdapter CurrentAdapter;

	private TextView textViewDisplaying;
	private ListView lvPoints;

	String[] OptionsListNavigation = new String[] { "Todo", "(+)", "(-)" };
	String[] PointsTypes = new String[] { "all", "increase", "decrease" };
	int CurrentPointType;

	public PointsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_points, container, false);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		ArrayAdapter<String> ActionBarListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, OptionsListNavigation);
		((ActionBarActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		((ActionBarActivity) getActivity()).getSupportActionBar().setListNavigationCallbacks(ActionBarListAdapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int arg0, long arg1) {
				// Toast.makeText(getActivity(), "Seleccionada opcion: " +
				// OptionsListNavigation[arg0], Toast.LENGTH_SHORT).show();
				CurrentPointType = arg0;
				new SearchRegisters(PointsTypes[CurrentPointType]).execute();
				return false;
			}
		});

		lvPoints = (ListView) rootView.findViewById(R.id.lvPointsList);
		textViewDisplaying = (TextView) rootView.findViewById(R.id.displaying);

		footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_list, null, false);
		lvPoints.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// nothing here
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (ScrollComplete)
					return;
				if (load(firstVisibleItem, visibleItemCount, totalItemCount)) {
					new LoadNextPage().execute();
				}
			}
		});

		lvPoints.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				HashMap<String, Object> Record = (HashMap<String, Object>) lvPoints.getAdapter().getItem(position);
				Intent points_detail_act = new Intent(getActivity(), PointsDetailActivity.class);
				points_detail_act.putExtra("ID", Integer.parseInt(Record.get("id").toString()));
				startActivity(points_detail_act);
			}
		});

		setHasOptionsMenu(true);
		((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle("Puntos");
		return rootView;
	}

	protected void updateDisplayingTextView() {
		String text = getString(R.string.display);
		text = String.format(text, lvPoints.getCount(), DataSource.getSize());
		textViewDisplaying.setText(text);
	}

	protected boolean load(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		boolean result = false;
		if (lvPoints.getAdapter() != null) {
			int aux = firstVisibleItem + visibleItemCount;
			int numItems = lvPoints.getAdapter().getCount();
			boolean lastItem = aux == totalItemCount && lvPoints.getChildAt(visibleItemCount - 1) != null && lvPoints.getChildAt(visibleItemCount - 1).getBottom() <= lvPoints.getHeight();
			boolean moreRows = numItems < DataSource.getSize();
			result = moreRows && lastItem && !loading;
			if (numItems == DataSource.getSize()) {
				ScrollComplete = true;
			}
		}
		return result;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_points, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.mnPointsRefresh || item.getItemId() == R.id.mnPointsRefresh) {
			new SearchRegisters(PointsTypes[CurrentPointType]).execute();
		}
		return result;
	}

	/**
	 * Clase Asincrona para recuparar los datos la primera ves que se muestrar
	 * la activity al usuario
	 **/
	protected class SearchRegisters extends AsyncTask<String, Void, String> {
		ProgressDialog pDialog;
		String PointsType;

		public SearchRegisters(String PointsType) {
			this.PointsType = PointsType;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Cargando Datos");
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
			lvPoints.addFooterView(footerView, null, false);
		}

		@Override
		protected String doInBackground(String... params) {
			DataSource = new DataSourcePoint(getActivity(), this.PointsType);
			ScrollComplete = false;
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			CurrentAdapter = new PointsItemAdapter(getActivity(), DataSource.getData(0, PAGESIZE));
			lvPoints.setAdapter(CurrentAdapter);
			lvPoints.removeFooterView(footerView);
			updateDisplayingTextView();
			pDialog.dismiss();
		}

	}

	/** Clase Asincrona para recuparar los datos de la paginación **/
	protected class LoadNextPage extends AsyncTask<String, Void, String> {
		private List<HashMap<String, Object>> newData = null;

		public LoadNextPage() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loading = true;
			lvPoints.addFooterView(footerView, null, false);
		}

		@Override
		protected String doInBackground(String... arg0) {
			newData = DataSource.getData(lvPoints.getAdapter().getCount() - 1, PAGESIZE);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			PointsItemAdapter adp = (PointsItemAdapter) CurrentAdapter;
			for (HashMap<String, Object> value : newData) {
				adp.add(value);
			}
			adp.notifyDataSetChanged();
			CurrentAdapter = (ListAdapter) adp;
			lvPoints.removeFooterView(footerView);
			updateDisplayingTextView();
			loading = false;
		}
	}
}