package com.kemas.fragments;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kemas.R;
import com.kemas.datasources.DataSourceAttendance;
import com.kemas.item.adapters.AttendancesItemAdapter;

/*  Fragment para ver las asistencias */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AttendancesFragment extends Fragment {
	private DataSourceAttendance DataSource;
	private View footerView;
	private boolean loading = false;
	private boolean ScrollComplete = false;
	private ListAdapter CurrentAdapter;

	private TextView tvDisplaying;
	private ListView lvAttendance;

	String[] OptionsListNavigation = new String[] { "Todos", "A Tiempo", "Atrazos", "Inasistencias" };
	String[] AttendanceTypes = new String[] { "all", "just_time", "late", "absence" };
	int CurrentAttendanceType;

	public AttendancesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_attedances, container, false);

		ArrayAdapter<String> ActionBarListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, OptionsListNavigation);
		((ActionBarActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		((ActionBarActivity) getActivity()).getSupportActionBar().setListNavigationCallbacks(ActionBarListAdapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int arg0, long arg1) {
				// Toast.makeText(getActivity(), "Seleccionada opcion: " + OptionsListNavigation[arg0], Toast.LENGTH_SHORT).show();
				CurrentAttendanceType = arg0;

				SearchRegisters Task = new SearchRegisters(AttendanceTypes[CurrentAttendanceType]);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					Task.execute();
				}
				return false;
			}
		});

		lvAttendance = (ListView) rootView.findViewById(R.id.lvAttendanceList);
		tvDisplaying = (TextView) rootView.findViewById(R.id.displaying);

		footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_list, null, false);
		lvAttendance.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// nothing here
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (ScrollComplete)
					return;
				if (load(firstVisibleItem, visibleItemCount, totalItemCount)) {
					LoadNextPage Task = new LoadNextPage();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						Task.execute();
					}
				}
			}
		});

		lvAttendance.setRecyclerListener(new RecyclerListener() {
			@Override
			public void onMovedToScrapHeap(View view) {
				// Ejecutar la Tarea de acuerdo a la version de Android
				RecycleListViewItem Task = new RecycleListViewItem(view);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					Task.execute();
				}
			}
		});

		lvAttendance.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				Toast.makeText(getActivity(), lvAttendance.getAdapter().getItem(position) + " " + getString(R.string.selected), Toast.LENGTH_SHORT).show();
			}
		});

		tvDisplaying.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				lvAttendance.smoothScrollToPosition(0);
				return false;
			}
		});

		setHasOptionsMenu(true);
		((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle("Asistencias");
		return rootView;
	}

	protected void updateDisplayingTextView() {
		String text = getString(R.string.display);
		text = String.format(text, lvAttendance.getCount(), DataSource.getSize());
		tvDisplaying.setText(text);
	}

	protected boolean load(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		boolean result = false;
		if (lvAttendance.getAdapter() != null) {
			int aux = firstVisibleItem + visibleItemCount;
			int numItems = lvAttendance.getAdapter().getCount();
			boolean lastItem = aux == totalItemCount && lvAttendance.getChildAt(visibleItemCount - 1) != null && lvAttendance.getChildAt(visibleItemCount - 1).getBottom() <= lvAttendance.getHeight();
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
		inflater.inflate(R.menu.menu_attendances, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.mnAttendancesRefresh || item.getItemId() == R.id.mnAttendancesRefresh) {
			SearchRegisters Task = new SearchRegisters(AttendanceTypes[CurrentAttendanceType]);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				Task.execute();
			}
		}
		return result;
	}

	/**
	 * Clase Asincrona para recuparar los datos la primera ves que se muestrar la activity al usuario
	 **/
	protected class SearchRegisters extends AsyncTask<String, Void, String> {
		ProgressDialog pDialog;
		String AttendancesType;

		public SearchRegisters(String AttendancesType) {
			this.AttendancesType = AttendancesType;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Cargando Datos");
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
			lvAttendance.addFooterView(footerView, null, false);
		}

		@Override
		protected String doInBackground(String... params) {
			DataSource = new DataSourceAttendance(getActivity(), this.AttendancesType, 15);
			ScrollComplete = false;
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			CurrentAdapter = new AttendancesItemAdapter(getActivity(), DataSource.getData());
			lvAttendance.setAdapter(CurrentAdapter);
			lvAttendance.removeFooterView(footerView);
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
			lvAttendance.addFooterView(footerView, null, false);
		}

		@Override
		protected String doInBackground(String... arg0) {
			newData = DataSource.getData();
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

	/** Clase Asincrona para reciclar los los items del listview **/
	protected class RecycleListViewItem extends AsyncTask<String, Void, String> {
		View view;

		public RecycleListViewItem(View view) {
			this.view = view;
		}

		@Override
		protected String doInBackground(String... params) {
			return null;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String result) {
			final TextView tvService = (TextView) view.findViewById(R.id.tvService);
			final TextView tvNumber = (TextView) view.findViewById(R.id.tvNumber);
			final TextView tvType = (TextView) view.findViewById(R.id.tvType);
			final TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
			final TextView tvHour = (TextView) view.findViewById(R.id.tvHour);
			final TextView tvDay = (TextView) view.findViewById(R.id.tvDay);

			tvService.setText(null);
			tvService.setTypeface(null);
			tvNumber.setText(null);
			tvType.setText(null);
			tvType.setBackgroundDrawable(null);
			tvDate.setText(null);
			tvHour.setText(null);
			tvDay.setText(null);
			tvService.setTypeface(null);
		}
	}
}