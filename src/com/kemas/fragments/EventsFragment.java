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
import com.kemas.datasources.DataSourceEvent;
import com.kemas.item.adapters.EventsItemAdapter;

/*  Fragment para ver las asistencias */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EventsFragment extends Fragment {
	private DataSourceEvent DataSource;
	private View footerView;
	private boolean loading = false;
	private boolean ScrollComplete = false;
	private ListAdapter CurrentAdapter;

	private TextView tvDisplaying;
	private ListView lvEvent;

	String[] OptionsListNavigation = new String[] { "Todos", "En Curso", "Finalizado" };
	String[] EventStates = new String[] { "all", "on_going", "closed" };
	int CurrentEventState;

	public EventsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_events, container, false);

		ArrayAdapter<String> ActionBarListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, OptionsListNavigation);
		((ActionBarActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		((ActionBarActivity) getActivity()).getSupportActionBar().setListNavigationCallbacks(ActionBarListAdapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int arg0, long arg1) {
				// Toast.makeText(getActivity(), "Seleccionada opcion: " +
				// OptionsListNavigation[arg0], Toast.LENGTH_SHORT).show();
				CurrentEventState = arg0;

				SearchRegisters Task = new SearchRegisters(EventStates[CurrentEventState]);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					Task.execute();
				}
				return false;
			}
		});

		lvEvent = (ListView) rootView.findViewById(R.id.lvEventList);
		tvDisplaying = (TextView) rootView.findViewById(R.id.displaying);

		footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_list, null, false);
		lvEvent.setOnScrollListener(new OnScrollListener() {
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

		lvEvent.setRecyclerListener(new RecyclerListener() {
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

		lvEvent.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				Toast.makeText(getActivity(), lvEvent.getAdapter().getItem(position) + " " + getString(R.string.selected), Toast.LENGTH_SHORT).show();
			}
		});

		tvDisplaying.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				lvEvent.smoothScrollToPosition(0);
				return false;
			}
		});

		setHasOptionsMenu(true);
		((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle("Asistencias");
		return rootView;
	}

	protected void updateDisplayingTextView() {
		String text = getString(R.string.display);
		text = String.format(text, lvEvent.getCount(), DataSource.getSize());
		tvDisplaying.setText(text);
	}

	protected boolean load(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		boolean result = false;
		if (lvEvent.getAdapter() != null) {
			int aux = firstVisibleItem + visibleItemCount;
			int numItems = lvEvent.getAdapter().getCount();
			boolean lastItem = aux == totalItemCount && lvEvent.getChildAt(visibleItemCount - 1) != null && lvEvent.getChildAt(visibleItemCount - 1).getBottom() <= lvEvent.getHeight();
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
		inflater.inflate(R.menu.menu_events, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.mnEventsRefresh || item.getItemId() == R.id.mnEventsRefresh) {
			SearchRegisters Task = new SearchRegisters(EventStates[CurrentEventState]);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				Task.execute();
			}
		}
		return result;
	}

	/**
	 * Clase Asincrona para recuparar los datos la primera ves que se muestrar
	 * la activity al usuario
	 **/
	protected class SearchRegisters extends AsyncTask<String, Void, String> {
		ProgressDialog pDialog;
		String EventsState;

		public SearchRegisters(String EventsState) {
			this.EventsState = EventsState;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Cargando Datos");
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
			lvEvent.addFooterView(footerView, null, false);
		}

		@Override
		protected String doInBackground(String... params) {
			DataSource = new DataSourceEvent(getActivity(), this.EventsState, 15);
			ScrollComplete = false;
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			CurrentAdapter = new EventsItemAdapter(getActivity(), DataSource.getData());
			lvEvent.setAdapter(CurrentAdapter);
			lvEvent.removeFooterView(footerView);
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
			lvEvent.addFooterView(footerView, null, false);
		}

		@Override
		protected String doInBackground(String... arg0) {
			newData = DataSource.getData();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			EventsItemAdapter adp = (EventsItemAdapter) CurrentAdapter;
			for (HashMap<String, Object> value : newData) {
				adp.add(value);
			}
			adp.notifyDataSetChanged();
			CurrentAdapter = (ListAdapter) adp;
			lvEvent.removeFooterView(footerView);
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

		}
	}
}