package com.kemas.activities;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kemas.Configuration;
import com.kemas.OpenERPconn;
import com.kemas.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CollaboratorActivity extends ActionBarActivity {
	private Configuration config;
	Context Context = (Context) this;

	private LinearLayout Contenedor;
	private ImageView imgPhoto;
	private TextView txtName;
	private TextView txtNickname;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collaborator);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Activar el Boton Home
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		// Cargar los datos del colaborador
		Contenedor = (LinearLayout) findViewById(R.id.Contenedor);
		imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
		txtName = (TextView) findViewById(R.id.txtName);
		txtNickname = (TextView) findViewById(R.id.txtNickname);
		new LoadInfo(Context).execute();
	}

	void edit() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_collaborator, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.mnCollaboratorEdit || item.getItemId() == R.id.mnCollaboratorEdit) {
			edit();

		} else if (item.getItemId() == android.R.id.home) {
			// Reggresar al activity de registro de asistencias
			finish();
		}

		return true;
	}

	class LoadInfo extends AsyncTask<Integer, Void, Integer> {
		Context context;
		ProgressDialog pDialog;
		HashMap<String, Object> Collaborator = null;

		public LoadInfo(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			Contenedor.setVisibility(View.INVISIBLE);
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Cargando Datos");
			pDialog.setCancelable(true);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int Port = Integer.parseInt(config.getPort().toString());
			String Server = config.getServer().toString();

			boolean TestConnection = OpenERPconn.TestConnection(Server, Port);
			if (TestConnection) {
				OpenERPconn oerp = OpenERPconn.connect(Server, Port, config.getDataBase(), config.getLogin(), config.getPassword());
				Collaborator = oerp.getCollaborator(Integer.parseInt(config.getCollaboratorID().toString()));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			if (Collaborator != null) {
				txtName.setText(Collaborator.get("name").toString());
				txtNickname.setText(Collaborator.get("nick_name").toString());
				if (Collaborator.get("image_medium") != "") {
					// Cargar la Foto
					byte[] photo = Base64.decode(Collaborator.get("image_medium").toString(), Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
					imgPhoto.setImageBitmap(bmp);
				}
				
				Contenedor.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(CollaboratorActivity.this, "No se pudieron recuperar los datos.", Toast.LENGTH_SHORT).show();
				finish();
			}
			pDialog.dismiss();
		}

	}
}
