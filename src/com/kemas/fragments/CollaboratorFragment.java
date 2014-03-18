package com.kemas.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kemas.Configuration;
import com.kemas.ListViewDinamicSize;
import com.kemas.OpenERP;
import com.kemas.R;
import com.kemas.hupernikao;
import com.kemas.item.adapters.AreasItem;
import com.kemas.item.adapters.AreasItemAdapter;

/*  Fragment para seccion perfil */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CollaboratorFragment extends Fragment {
	private Configuration config;
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

	public CollaboratorFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_collaborator, container, false);
		setHasOptionsMenu(true);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(getActivity());

		// Cargar los datos del colaborador
		Contenedor = (LinearLayout) rootView.findViewById(R.id.Contenedor);
		imgPhoto = (ImageView) rootView.findViewById(R.id.imgPhoto);
		imgTeam = (ImageView) rootView.findViewById(R.id.imgTeam);
		txtCode = (TextView) rootView.findViewById(R.id.txtCode);
		txtName = (TextView) rootView.findViewById(R.id.txtName);
		txtNickname = (TextView) rootView.findViewById(R.id.txtNickname);
		txtBirth = (TextView) rootView.findViewById(R.id.txtBirth);
		txtAge = (TextView) rootView.findViewById(R.id.txtAge);
		txtMaritalStatus = (TextView) rootView.findViewById(R.id.txtMaritalStatus);
		txtAddress = (TextView) rootView.findViewById(R.id.txtAddress);
		txtMobile = (TextView) rootView.findViewById(R.id.txtMobile);
		txtTelef1 = (TextView) rootView.findViewById(R.id.txtTelef1);
		txtTelef2 = (TextView) rootView.findViewById(R.id.txtTelef2);
		txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
		txtIM = (TextView) rootView.findViewById(R.id.txtIM);
		txtJoinDate = (TextView) rootView.findViewById(R.id.txtJoinDate);
		txtAgeInMinistry = (TextView) rootView.findViewById(R.id.txtAgeInMinistry);
		txtPoints = (TextView) rootView.findViewById(R.id.txtPoints);
		txtLevel = (TextView) rootView.findViewById(R.id.txtLevel);
		txtTeam = (TextView) rootView.findViewById(R.id.txtTeam);
		txtTeam2 = (TextView) rootView.findViewById(R.id.txtTeam2);
		lstAreas = (ListView) rootView.findViewById(R.id.lstAreas);

		lblPersonalInfo = (TextView) rootView.findViewById(R.id.lblPersonalInfo);
		lblContact = (TextView) rootView.findViewById(R.id.lblContact);
		lblkemas = (TextView) rootView.findViewById(R.id.lblkemas);
		lblAreas = (TextView) rootView.findViewById(R.id.lblAreas);

		Typeface Roboto_light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
		Typeface Roboto_light_italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-LightItalic.ttf");

		lblPersonalInfo.setTypeface(Roboto_light);
		lblContact.setTypeface(Roboto_light);
		lblkemas.setTypeface(Roboto_light);
		lblAreas.setTypeface(Roboto_light);
		txtTeam.setTypeface(Roboto_light_italic);

		scroll = (ScrollView) rootView.findViewById(R.id.scroll);
		new LoadInfo().execute();

		return rootView;
	}

	private void makeScroll(final int go) {
		scroll.post(new Runnable() {
			public void run() {
				scroll.scrollTo(0, go * txtTeam.getLineHeight());
			}
		});
	}

	void edit() {
		Toast.makeText(getActivity(), "Editando datos.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_collaborator, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.mnCollaboratorEdit || item.getItemId() == R.id.mnCollaboratorEdit) {
			edit();

		} else if (item.getItemId() == android.R.id.home) {
			// Regresar al activity de registro de asistencias
		}
		return result;
	}

	class LoadInfo extends AsyncTask<Integer, Void, Integer> {
		ProgressDialog pDialog;
		HashMap<String, Object> Collaborator = null;

		public LoadInfo() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Contenedor.setVisibility(View.INVISIBLE);
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Cargando Datos");
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int Port = Integer.parseInt(config.getPort().toString());
			String Server = config.getServer().toString();

			boolean TestConnection = OpenERP.TestConnection(Server, Port);
			if (TestConnection) {
				OpenERP oerp = hupernikao.BuildOpenERPConnection(config);
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
					lstAreas.setAdapter(new AreasItemAdapter(getActivity(), ItemsAreas));
					ListViewDinamicSize.getListViewSize(lstAreas);
					makeScroll(0);
				}

				((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Mis Datos");
				// getSupportActionBar().setTitle(Collaborator.get("nick_name").toString());
				Contenedor.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(getActivity(), "No se pudieron recuperar los datos.", Toast.LENGTH_SHORT).show();
			}
			pDialog.dismiss();
		}
	}
}