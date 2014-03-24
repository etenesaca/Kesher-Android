package com.kemas.activities;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.R;
import com.kemas.hupernikao;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class ConnectionActivity extends ActionBarActivity implements OnTouchListener {
	enum ResultSave {
		Successful, ConnectionError, BadLogin, NotModuleKemas, UserNotCollaborator, SaveError
	}

	// Declare Elements
	private Spinner cmbDb;
	private EditText txtServer;
	private EditText txtPort;
	private EditText txtUsername;
	private EditText txtPassword;
	private Configuration config;

	Context Context = (Context) this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Activar el Boton Home
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		// LIsta de Bases de Datos
		cmbDb = (Spinner) findViewById(R.id.cmbDb);
		cmbDb.setOnTouchListener(this);

		txtServer = (EditText) findViewById(R.id.txtServer);
		txtPort = (EditText) findViewById(R.id.txtPort);
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);

		// Cargar los datos desde la configuración
		String Key_SERVER = config.getServer();
		String Key_PORT = config.getPort();
		String Key_DATABASE = config.getDataBase();

		txtServer.setText(Key_SERVER);
		txtPort.setText(config.getPort());
		txtUsername.setText(config.getLogin());
		txtPassword.setText(config.getPassword());

		// Verificar si ya hay guardada una configuracion para Cargar la lista
		// de base de dartos
		if (Key_SERVER != null && Key_PORT != null && Key_DATABASE != null) {
			int saved_port = Integer.parseInt(config.getPort());
			boolean TestConnection = OpenERP.TestConnection(Key_SERVER, saved_port);
			ArrayAdapter<String> adaptador;
			if (TestConnection) {
				String[] list_db = OpenERP.getDatabaseList(Key_SERVER, saved_port);
				adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_db);
				cmbDb.setAdapter(adaptador);
				for (int i = 0; i < list_db.length; i++) {
					if (list_db[i].equals(Key_DATABASE)) {
						cmbDb.setSelection(i);
					}
				}
			} else {
				String[] list_db = {};
				adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_db);
			}
		}
	}

	public void save() {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ConnectionActivity.this);
		if (!hupernikao.TestNetwork(Context)) {
			dlgAlert.setTitle("Error").setIcon(android.R.drawable.ic_delete);
			dlgAlert.setPositiveButton("OK", null);
			dlgAlert.setCancelable(true);
			dlgAlert.setMessage("No se puede Establecer conexión. Revise su conexión a Internet y vuelva a intentarlo");
			dlgAlert.create().show();
			return;
		}

		// Guardar Los datos del Empleado
		String port_str = txtPort.getText().toString();
		final String Server = txtServer.getText().toString();
		String db = "";
		try {
			db = cmbDb.getSelectedItem().toString();
		} catch (Exception e) {
		}
		final String user = txtUsername.getText().toString();
		final String pass = txtPassword.getText().toString();

		dlgAlert.setTitle("Advertencia").setIcon(android.R.drawable.stat_sys_warning);
		dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		if ("".equals(Server)) {
			dlgAlert.setMessage("Primero Ingrese la Dirección del Servidor.");
			dlgAlert.create().show();
		} else if ("".equals(port_str)) {
			dlgAlert.setMessage("Primero Ingrese el Número del Puerto.");
			dlgAlert.create().show();
		} else if ("".equals(port_str)) {
			dlgAlert.setMessage("Primero Ingrese el Número del Puerto.");
			dlgAlert.create().show();
		} else if ("".equals(db)) {
			dlgAlert.setMessage("Primero Seleccione la Base de Datos.");
			dlgAlert.create().show();
		} else if ("".equals(user)) {
			dlgAlert.setMessage("Ingrese el nombre de Usuario.");
			dlgAlert.create().show();
		} else if ("".equals(pass)) {
			dlgAlert.setMessage("Ingrese el Password.");
			dlgAlert.create().show();
		} else {
			new SaveInfo(Server, txtPort.getText().toString(), cmbDb.getSelectedItem().toString(), user, pass).execute();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.cmbDb:
			if (!hupernikao.TestNetwork(Context)) {
				Toast msg = Toast.makeText(this, "No se puede Establecer conexión. Revise su conexión a Internet.", Toast.LENGTH_SHORT);
				msg.show();
				break;
			}

			String server = txtServer.getText().toString();
			String port_str = txtPort.getText().toString();
			if (server.equals("") || port_str.equals("")) {
				break;
			}

			String value_server = txtPort.getText().toString() + "";
			String value_port = txtPort.getText().toString() + "";
			if (value_server != "" && value_port != "") {
				int port = Integer.parseInt(txtPort.getText().toString());

				boolean TestConnection = OpenERP.TestConnection(server, port);
				ArrayAdapter<String> adaptador;
				if (TestConnection) {
					String value_database = "";
					try {
						value_database = cmbDb.getSelectedItem().toString();
					} catch (Exception e) {
					}
					String[] list_db = OpenERP.getDatabaseList(server, port);
					adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_db);
					cmbDb.setAdapter(adaptador);
					if (value_database != "") {
						for (int i = 0; i < list_db.length; i++) {
							if (list_db[i].equals(value_database)) {
								cmbDb.setSelection(i);
							}
						}
					}
				} else {
					Toast msg = Toast.makeText(this, "No se pudo conectar al servidor, Verifique los parametros de Conexión.", Toast.LENGTH_SHORT);
					msg.show();
					String[] list_db = {};
					adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_db);
					cmbDb.setAdapter(adaptador);
				}
			}
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_connection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.mnConnectionSave || item.getItemId() == R.id.mnConnectionSave) {
			save();

		} else if (item.getItemId() == android.R.id.home) {
			// Reggresar al activity de registro de asistencias
			finish();
		}

		return true;
	}

	/**
	 * Clase Asincrona para recuparar los datos la primera ves que se muestrar
	 * la activity al usuario
	 **/
	@SuppressWarnings("static-access")
	protected class SaveInfo extends AsyncTask<String, Void, String> {
		String Server, DataBase, User, Pass;
		int Port;
		OpenERP oerp;
		ProgressDialog pDialog;
		ResultSave result;

		public SaveInfo(String Server, String Port, String DataBase, String User, String Pass) {
			this.Server = Server;
			this.Port = Integer.parseInt(Port);
			this.DataBase = DataBase;
			this.User = User;
			this.Pass = Pass;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(ConnectionActivity.this);
			pDialog.setMessage("Guardando");
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			if (!OpenERP.TestConnection(this.Server, this.Port)) {
				result = result.ConnectionError;
				return null;
			}
			oerp = OpenERP.connect(Server, Port, this.DataBase, this.User, this.Pass);
			if (oerp == null) {
				result = result.BadLogin;
				return null;
			}

			// Verificar que la base de datos tenga
			// instalado el modulo Control de Horario
			if (!oerp.Module_Installed()) {
				result = result.NotModuleKemas;
				return null;
			}

			// Verificar que el Usuario sea un Colaborador
			Long[] collaborator_ids = oerp.search("kemas.collaborator", new Object[] { new Object[] { "user_id", "=", oerp.getUserId() } }, 1);
			if (collaborator_ids.length < 1) {
				result = result.UserNotCollaborator;
				return null;
			}
			// Guardar los datos del Colaborador
			if (save_collaborator_info(collaborator_ids[0]))
				result = result.Successful;
			else
				result = result.SaveError;
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ConnectionActivity.this);
			dlgAlert.setTitle("Error").setIcon(android.R.drawable.ic_delete);
			dlgAlert.setPositiveButton("OK", null);
			dlgAlert.setCancelable(true);

			super.onPostExecute(result);

			switch (this.result) {
			case ConnectionError:
				dlgAlert.setMessage("No se pudo conectar al servidor, Verifique los parametros de Conexión.");
				dlgAlert.create().show();
			case BadLogin:
				dlgAlert.setMessage("Usuario o Contraseña No Válidos.");
				dlgAlert.create().show();
			case NotModuleKemas:
				dlgAlert.setMessage("La base de datos seleccionada no tiene instalado el Modulo de Gestión del Eventos y Control de Actividades (ke+).");
				dlgAlert.create().show();
			case UserNotCollaborator:
				dlgAlert.setMessage("La credenciales ingresadas no pertenecen a un Colaborador.");
				dlgAlert.create().show();
			case Successful:
				Toast.makeText(ConnectionActivity.this, "Lo Datos Se Guardaron Correctamente.", Toast.LENGTH_SHORT).show();
				finish();
			case SaveError:
				Toast.makeText(ConnectionActivity.this, "No se pudieron guardar los datos, vuelve a intentarlo.", Toast.LENGTH_SHORT).show();
				finish();
			default:
				break;
			}
			pDialog.dismiss();
		}

		public boolean save_collaborator_info(long collaborator_id) {
			boolean result = false;
			try {
				HashMap<String, Object> NavigationMenuInfo = oerp.getNavigationmenuInfo(collaborator_id);
				if (NavigationMenuInfo == null) {
					result = false;
				} else {
					// Guardar los datos
					config.setServer(oerp.getServer());
					config.setPort(oerp.getPort().toString());
					config.setDataBase(oerp.getDatabase());
					config.setLogin(oerp.getUserName().toString());
					config.setPassword(oerp.getPassword());
					config.setUserID(oerp.getUserId().toString());
					config.setCollaboratorID(collaborator_id + "");

					// Menu
					config.setBackground(NavigationMenuInfo.get("mobile_background").toString());
					config.setTextColor(NavigationMenuInfo.get("mobile_background_text_color").toString());

					config.setName(NavigationMenuInfo.get("name").toString());
					config.setPhoto(NavigationMenuInfo.get("image").toString());
					config.setTeam(NavigationMenuInfo.get("team").toString());
					result = true;
				}
			} catch (Exception e) {
				result = false;
			}
			return result;
		}
	}
}