package com.kemas.activities;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kemas.Configuration;
import com.kemas.OpenERPconn;
import com.kemas.R;
import com.kemas.hupernikao;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class HomeActivity extends ActionBarActivity {
	private Configuration config;
	private DrawerLayout drawerLayout;
	private ListView drawer;
	private ActionBarDrawerToggle toggle;
	private static final String[] opciones = { "Configurar Conexión", "Mi Datos", "Eventos", "Salir" };

	@Override
	protected void onStart() {
		super.onStart();
		// Declarar e inicializar componentes para el Navigation Drawer
		drawer = (ListView) findViewById(R.id.drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		if (config.getBackground() != null && drawer.getHeaderViewsCount() == 0) {
			OpenERPconn oerp = OpenERPconn.connect(config.getServer(), Integer.parseInt(config.getPort().toString()), config.getDataBase(), config.getLogin(), config.getPassword());

			Long config_id = oerp.search("kemas.config", new Object[] {}, 1)[0];
			String[] fields_to_read = new String[] { "mobile_background", "mobile_background_text_color" };
			HashMap<String, Object> System_Config = oerp.read("kemas.config", config_id, fields_to_read);

			// Declaramos la cabecera
			View header = getLayoutInflater().inflate(R.layout.header, null);
			TextView txtLogin = (TextView) header.findViewById(R.id.txtLogin);
			ImageView imgAvatar = (ImageView) header.findViewById(R.id.imgAvatar);
			LinearLayout HeaderContainer = (LinearLayout) header.findViewById(R.id.HeaderContainer);

			// Cargar el fondo
			byte[] background = Base64.decode(System_Config.get("mobile_background").toString(), Base64.DEFAULT);
			Bitmap bmp_background = BitmapFactory.decodeByteArray(background, 0, background.length);
			Drawable dw = new BitmapDrawable(getResources(), bmp_background);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				HeaderContainer.setBackground(dw);
			} else {
				HeaderContainer.setBackgroundDrawable(dw);
			}

			// Cargar la Foto
			byte[] photo = Base64.decode(config.getPhoto(), Base64.DEFAULT);
			Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
			imgAvatar.setImageBitmap(hupernikao.getRoundedCornerBitmap(bmp, true));

			// Escribir el nombre del Colaborador
			txtLogin.setText(config.getName().toString());
			drawer.addHeaderView(header);
		}
		// Declarar adapter y eventos al hacer click
		drawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, opciones));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Rescatamos el Action Bar y activamos el boton Home
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		// Declarar e inicializar componentes para el Navigation Drawer
		drawer = (ListView) findViewById(R.id.drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				drawerLayout.closeDrawers();
				switch (arg2) {
				case 0:
					// Configurar Conexión
					Intent config_act = new Intent(HomeActivity.this, ConnectionActivity.class);
					startActivity(config_act);
					break;
				case 1:
					if (config.getUserID() == null || (config.getUserID()).equals("")) {
						Toast.makeText(HomeActivity.this, "Primero configure los datos de Conexión.", Toast.LENGTH_SHORT).show();
					} else {
						// Datos del Colaborador
						Intent collaborator_act = new Intent(HomeActivity.this, CollaboratorActivity.class);
						startActivity(collaborator_act);
					}
					break;
				default:
					finish();
					break;
				}
			}
		});

		// Sombra del panel Navigation Drawer
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// Integracion boton oficial
		toggle = new ActionBarDrawerToggle(this, // Activity
				drawerLayout, // Panel del Navigation Drawer
				R.drawable.ic_drawer, // Icono que va a utilizar
				R.string.app_name, // Descripcion al abrir el drawer
				R.string.app_name // Descripcion al cerrar el drawer
		) {
			public void onDrawerClosed(View view) {
				// Drawer cerrado
				getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
				// invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				// Drawer abierto
				getSupportActionBar().setTitle("Menu");
				// invalidateOptionsMenu();
			}
		};

		drawerLayout.setDrawerListener(toggle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (toggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Activamos el toggle con el icono
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		toggle.syncState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
