package com.kemas.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.kemas.R;
import com.kemas.activities.ConnectionActivity;

/*  Fragment para ver las asistencias */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AttendancesFragment extends Fragment {
	String[] OptionsListNavigation = new String[] { "Todos", "A Tiempo", "Atrazos", "Inasistencias" };

	public AttendancesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_attedances, container, false);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		ArrayAdapter<String> ActionBarListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, OptionsListNavigation);
		((ActionBarActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		((ActionBarActivity) getActivity()).getSupportActionBar().setListNavigationCallbacks(ActionBarListAdapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int arg0, long arg1) {
				Toast.makeText(getActivity(), "Seleccionada opcion: " + OptionsListNavigation[arg0] , Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		return rootView;
	}

}