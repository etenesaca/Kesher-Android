package com.kemas.activities;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.kemas.Configuration;
import com.kemas.OpenERPconn;
import com.kemas.R;
import com.kemas.hupernikao;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CollaboratorActivity extends ActionBarActivity {
	private Configuration config;
	Context Context = (Context) this;

	private TextView txtName;

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

		// Cargar los datos del colbarador
		txtName = (TextView) findViewById(R.id.txtName);
		load_collaborator_info();
	}

	void load_collaborator_info() {
		if (!hupernikao.TestNetwork(Context)) {
			Toast msg = Toast.makeText(this, "No se puede Establecer conexión. Revise su conexión a Internet.", Toast.LENGTH_SHORT);
			msg.show();
			return;
		}
		int Port = Integer.parseInt(config.getPort().toString());
		String Server = config.getServer().toString();
		boolean TestConnection = OpenERPconn.TestConnection(Server, Port);
		if (TestConnection) {
			OpenERPconn oerp = OpenERPconn.connect(Server, Port, config.getDataBase(), config.getLogin(), config.getPassword());
			HashMap<String, Object> Collaborator = oerp.getCollaborator(Integer.parseInt(config.getCollaboratorID().toString()));
			if (Collaborator != null) {
				txtName.setText(Collaborator.get("name") + "");
			} else {
				Toast.makeText(this, "No se pudieron recuperar los datos.", Toast.LENGTH_SHORT).show();
				finish();
			}

		} else {
			Toast msg = Toast.makeText(this, "No se pudo conectar al servidor, Verifique los parametros de Conexión.", Toast.LENGTH_SHORT);
			msg.show();
		}
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

}
