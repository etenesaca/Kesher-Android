package com.kemas.activities;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kemas.Configuration;
import com.kemas.OpenERPconn;
import com.kemas.R;
import com.kemas.hupernikao;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class ConnectionActivity extends ActionBarActivity implements OnClickListener, OnTouchListener {
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
			boolean TestConnection = OpenERPconn.TestConnection(Key_SERVER, saved_port);
			ArrayAdapter<String> adaptador;
			if (TestConnection) {
				String[] list_db = OpenERPconn.getDatabaseList(Key_SERVER, saved_port);
				adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_db);
				cmbDb.setAdapter(adaptador);
				for (int i = 0; i < list_db.length; i++) {
					if (list_db[i].equals(Key_DATABASE)) {
						cmbDb.setSelection(i);
					}
				}
			} else {
				String[] list_db = {};
				adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_db);
			}
		}
	}

	public boolean save_collaborator_info(Configuration config, String collaborator_id, OpenERPconn oerp_connection) {
		long ln_collaborator_id = Long.parseLong(collaborator_id + "");
		return save_collaborator_info(config, ln_collaborator_id, oerp_connection);
	}

	public boolean save_collaborator_info(Configuration config, Long[] collaborator_ids, OpenERPconn oerp_connection) {
		long collaborator_id = collaborator_ids[0];
		return save_collaborator_info(config, collaborator_id, oerp_connection);
	}

	public boolean save_collaborator_info(Configuration config, long collaborator_id, OpenERPconn oerp_connection) {
		String[] fields_to_read = {};

		fields_to_read = new String[] { "user_id" };
		HashMap<String, Object> Collaborator = oerp_connection.read("kemas.collaborator", collaborator_id, fields_to_read);

		// Leer los datos del perfil del Usuario
		Object[] User_tpl = (Object[]) Collaborator.get("user_id");
		fields_to_read = new String[] { "image_medium", "partner_id" };
		HashMap<String, Object> User = oerp_connection.read("res.users", Long.parseLong(User_tpl[0] + ""), fields_to_read);
		User.put("name", User_tpl[1] + "");

		Long config_id = oerp_connection.search("kemas.config", new Object[] {}, 1)[0];
		fields_to_read = new String[] { "mobile_background", "mobile_background_text_color" };
		HashMap<String, Object> System_Config = oerp_connection.read("kemas.config", config_id, fields_to_read);

		// Guardar los datos
		config.setServer(oerp_connection.getServer());
		config.setPort(oerp_connection.getPort() + "");
		config.setDataBase(oerp_connection.getDatabase());
		config.setLogin(oerp_connection.getUserName() + "");
		config.setPassword(oerp_connection.getPassword());
		config.setUserID(oerp_connection.getUserId() + "");
		config.setCollaboratorID(collaborator_id + "");
		
		config.setBackground(System_Config.get("mobile_background").toString());
		config.setTextColor(System_Config.get("mobile_background_text_color").toString());

		config.setName((String) User.get("name"));
		config.setPhoto((String) User.get("image_medium"));
		return true;
	}

	public boolean save_collaborator_info(Configuration config, String collaborator_id, String Server, int Port, String user, String pass) {
		long ln_collaborator_id = Long.parseLong(collaborator_id + "");
		return save_collaborator_info(config, ln_collaborator_id, Server, Port, user, pass);
	}

	public boolean save_collaborator_info(Configuration config, Long[] collaborator_ids, String Server, int Port, String user, String pass) {
		long collaborator_id = collaborator_ids[0];
		return save_collaborator_info(config, collaborator_id, Server, Port, user, pass);
	}

	public boolean save_collaborator_info(Configuration config, long collaborator_id, String Server, int Port, String user, String pass) {
		OpenERPconn oerp_connection = OpenERPconn.connect(Server, Port, cmbDb.getSelectedItem().toString(), user, pass);
		return save_collaborator_info(config, collaborator_id, oerp_connection);
	}

	public void save() {
		if (!hupernikao.TestNetwork(Context)) {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ConnectionActivity.this);
			dlgAlert.setTitle("Error").setIcon(android.R.drawable.ic_delete);
			dlgAlert.setPositiveButton("OK", null);
			dlgAlert.setCancelable(true);
			dlgAlert.setMessage("No se puede Establecer conexión. Revise su conexión a Internet");
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

		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
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
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Guardar").setMessage("¿Guardar los datos Ahora?").setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ConnectionActivity.this);
					dlgAlert.setTitle("Error").setIcon(android.R.drawable.ic_delete);
					dlgAlert.setPositiveButton("OK", null);
					dlgAlert.setCancelable(true);

					int Port = Integer.parseInt(txtPort.getText().toString());

					if (OpenERPconn.TestConnection(Server, Port)) {
						OpenERPconn oerp = OpenERPconn.connect(Server, Port, cmbDb.getSelectedItem().toString(), user, pass);

						if (oerp == null) {
							dlgAlert.setMessage("Usuario o Contraseña No Válidos.");
							dlgAlert.create().show();
						} else {
							// Verificar que la base de datos tenga
							// instalado el modulo Control de Horario
							if (!oerp.Module_Installed("kemas")) {
								dlgAlert.setMessage("La base de datos seleccionada no tiene instalado el Modulo de Gestión del Eventos y Control de Actividades (ke+).");
								dlgAlert.create().show();
							} else {
								// Verificar que el Usuario sea un empleado
								Long[] collaborator_ids = oerp.search("kemas.collaborator", new Object[] { new Object[] { "user_id", "=", oerp.getUserId() } }, 1);
								if (collaborator_ids.length < 1) {
									dlgAlert.setMessage("La credenciales ingresadas no pertenecen a un Colaborador.");
									dlgAlert.create().show();
								} else {
									// Guardar los datos del empleado
									if (save_collaborator_info(config, collaborator_ids, oerp)) {
										Toast.makeText(ConnectionActivity.this, "Lo Datos Se Guardaron Correctamente.", Toast.LENGTH_SHORT).show();
										finish();
									}
								}
							}
						}
					} else {
						dlgAlert.setMessage("No se pudo conectar al servidor, Verifique los parametros de Conexión.");
						dlgAlert.create().show();
					}
				}
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
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

				boolean TestConnection = OpenERPconn.TestConnection(server, port);
				ArrayAdapter<String> adaptador;
				if (TestConnection) {
					String value_database = "";
					try {
						value_database = cmbDb.getSelectedItem().toString();
					} catch (Exception e) {
					}
					String[] list_db = OpenERPconn.getDatabaseList(server, port);
					adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_db);
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
					adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_db);
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
}