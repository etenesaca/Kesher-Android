package com.kemas.activities;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.R;
import com.kemas.hupernikao;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class PointsDetailActivity extends ActionBarActivity {
	private Configuration config;
	private long RecordID;
	Context Context = (Context) this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_points_detail);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Activar el Boton Home
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		Bundle bundle = getIntent().getExtras();
		RecordID = bundle.getLong("ID");

		// Ejecutar la Carga de Datos
		((ActionBarActivity) PointsDetailActivity.this).getSupportActionBar().setTitle("Historial de Puntos");
		new LoadInfo().execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// Reggresar al activity de registro de asistencias
			finish();
		}

		return true;
	}

	/** Clase Asincrona para recuparar los datos del registro de Puntaje **/
	protected class LoadInfo extends AsyncTask<String, Void, String> {
		ProgressDialog pDialog;
		HashMap<String, Object> PointsDetail = null;
		String DetailName;

		public LoadInfo() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(PointsDetailActivity.this);
			pDialog.setMessage("Cargando Datos");
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			int Port = Integer.parseInt(config.getPort().toString());
			String Server = config.getServer().toString();

			boolean TestConnection = OpenERP.TestConnection(Server, Port);
			if (TestConnection) {
				OpenERP oerp = hupernikao.BuildOpenERPConnection(config);
				PointsDetail = oerp.read("kemas.history.points", RecordID, new String[] { "code", "date", "reg_uid", "attendance_id", "type", "description", "summary", "points" });
				DetailName = PointsDetail.get("code").toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			((ActionBarActivity) PointsDetailActivity.this).getSupportActionBar().setSubtitle(DetailName);
			pDialog.dismiss();
		}

	}
}
