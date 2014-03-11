package com.kemas.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kemas.Configuration;
import com.kemas.ListViewDinamicSize;
import com.kemas.OpenERPconn;
import com.kemas.R;
import com.kemas.hupernikao;
import com.kemas.item.adapters.AreasItem;
import com.kemas.item.adapters.AreasItemAdapter;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CollaboratorActivity extends ActionBarActivity {
	private Configuration config;
	Context Context = (Context) this;
	private ScrollView scroll;
	private LinearLayout Contenedor;
	private ImageView imgPhoto;
	private ImageView imgTeam;
	private TextView txtCode;
	private TextView txtName;
	private TextView txtNickname;
	private TextView txtBirth;
	private TextView txtAge;
	private TextView txtMaritalStatus;
	private TextView txtAddress;
	private TextView txtMobile;
	private TextView txtTelef1;
	private TextView txtTelef2;
	private TextView txtEmail;
	private TextView txtIM;
	private TextView txtJoinDate;
	private TextView txtAgeInMinistry;
	private TextView txtPoints;
	private TextView txtLevel;
	private TextView txtTeam;
	private TextView txtTeam2;
	private ListView lstAreas;

	private TextView lblPersonalInfo;
	private TextView lblContact;
	private TextView lblkemas;
	private TextView lblAreas;

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
		imgTeam = (ImageView) findViewById(R.id.imgTeam);
		txtCode = (TextView) findViewById(R.id.txtCode);
		txtName = (TextView) findViewById(R.id.txtName);
		txtNickname = (TextView) findViewById(R.id.txtNickname);
		txtBirth = (TextView) findViewById(R.id.txtBirth);
		txtAge = (TextView) findViewById(R.id.txtAge);
		txtMaritalStatus = (TextView) findViewById(R.id.txtMaritalStatus);
		txtAddress = (TextView) findViewById(R.id.txtAddress);
		txtMobile = (TextView) findViewById(R.id.txtMobile);
		txtTelef1 = (TextView) findViewById(R.id.txtTelef1);
		txtTelef2 = (TextView) findViewById(R.id.txtTelef2);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
		txtIM = (TextView) findViewById(R.id.txtIM);
		txtJoinDate = (TextView) findViewById(R.id.txtJoinDate);
		txtAgeInMinistry = (TextView) findViewById(R.id.txtAgeInMinistry);
		txtPoints = (TextView) findViewById(R.id.txtPoints);
		txtLevel = (TextView) findViewById(R.id.txtLevel);
		txtTeam = (TextView) findViewById(R.id.txtTeam);
		txtTeam2 = (TextView) findViewById(R.id.txtTeam2);
		lstAreas = (ListView) findViewById(R.id.lstAreas);

		lblPersonalInfo = (TextView) findViewById(R.id.lblPersonalInfo);
		lblContact = (TextView) findViewById(R.id.lblContact);
		lblkemas = (TextView) findViewById(R.id.lblkemas);
		lblAreas = (TextView) findViewById(R.id.lblAreas);

		Typeface Roboto_light = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		Typeface Roboto_light_italic = Typeface.createFromAsset(getAssets(), "fonts/Roboto-LightItalic.ttf");

		lblPersonalInfo.setTypeface(Roboto_light);
		lblContact.setTypeface(Roboto_light);
		lblkemas.setTypeface(Roboto_light);
		lblAreas.setTypeface(Roboto_light);
		txtTeam.setTypeface(Roboto_light_italic);

		scroll = (ScrollView) findViewById(R.id.scroll);
		new LoadInfo(Context).execute();
	}

	private void makeScroll(final int go) {
		scroll.post(new Runnable() {
			public void run() {
				scroll.scrollTo(0, go * txtTeam.getLineHeight());
			}
		});
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
				OpenERPconn oerp = hupernikao.BuildOpenERPconn(config);
				Collaborator = oerp.getCollaborator(Integer.parseInt(config.getCollaboratorID().toString()));
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			if (Collaborator != null) {
				/*
				 * CAMPOS A MOSTAR - code - name - nick_name - birth -
				 * marital_status - address - image_medium - mobile - telef1 -
				 * telef2 - email - im_account - team - join_date - points -
				 * level
				 */
				txtCode.setText(Collaborator.get("code").toString());
				txtName.setText(Collaborator.get("name").toString());
				txtNickname.setText(Collaborator.get("nick_name").toString());
				txtBirth.setText(Collaborator.get("birth").toString());
				txtAge.setText(Collaborator.get("age").toString());
				txtMaritalStatus.setText(Collaborator.get("marital_status").toString());
				txtAddress.setText(Collaborator.get("address").toString());
				txtMobile.setText(Collaborator.get("mobile").toString());
				txtTelef1.setText(Collaborator.get("telef1").toString());
				txtTelef2.setText(Collaborator.get("telef2").toString());
				txtEmail.setText(Collaborator.get("email").toString());
				txtIM.setText(Collaborator.get("im_account").toString());
				txtJoinDate.setText(Collaborator.get("join_date").toString());
				txtAgeInMinistry.setText(Collaborator.get("age_in_ministry").toString());
				txtPoints.setText(Collaborator.get("points").toString());
				txtLevel.setText(Collaborator.get("level").toString());
				if (Collaborator.get("image_medium") != "") {
					// Cargar la Foto
					byte[] photo = Base64.decode(Collaborator.get("image_medium").toString(), Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
					imgPhoto.setImageBitmap(hupernikao.getRoundedCornerBitmap(bmp, true));
				}

				txtTeam.setVisibility(View.INVISIBLE);
				imgTeam.setVisibility(View.INVISIBLE);
				if (Collaborator.get("team").toString() != "") {
					HashMap<String, Object> Team = (HashMap<String, Object>) Collaborator.get("team");
					txtTeam.setText(Team.get("name").toString());
					txtTeam2.setText(Team.get("name").toString());
					// Cargar la Logo del Equipo
					byte[] logo = Base64.decode(Team.get("logo").toString(), Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(logo, 0, logo.length);
					imgTeam.setImageBitmap(hupernikao.getRoundedCornerBitmapSimple(bmp));

					imgTeam.setVisibility(View.VISIBLE);
					txtTeam.setVisibility(View.VISIBLE);
				} else {
					txtTeam2.setText("-- ");
				}

				// Procesar las areas
				if (Collaborator.get("areas").toString() != "") {
					List<AreasItem> ItemsAreas = new ArrayList<AreasItem>();
					Object[] Areas = (Object[]) Collaborator.get("areas");
					for (Object AreaObj : Areas) {
						HashMap<String, Object> Area = (HashMap<String, Object>) AreaObj;
						ItemsAreas.add(new AreasItem(Area.get("logo").toString(), Area.get("name").toString()));
					}
					lstAreas.setAdapter(new AreasItemAdapter(CollaboratorActivity.this, ItemsAreas));
					ListViewDinamicSize.getListViewSize(lstAreas);
					makeScroll(0);
				}

				getSupportActionBar().setTitle(Collaborator.get("nick_name").toString());
				Contenedor.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(CollaboratorActivity.this, "No se pudieron recuperar los datos.", Toast.LENGTH_SHORT).show();
				finish();
			}
			pDialog.dismiss();
		}
	}
}
