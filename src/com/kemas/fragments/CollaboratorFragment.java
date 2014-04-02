package com.kemas.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
	HashMap<String, Object> Collaborator = new HashMap<String, Object>();
	private Configuration config;
	private ScrollView scroll;
	private LinearLayout Contenedor;
	private ImageView imgPhoto;
	private ImageView imgTeam;
	private TextView txtCode;
	private TextView txtCI;
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

		((ActionBarActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(getActivity());

		// Cargar los datos del colaborador
		Contenedor = (LinearLayout) rootView.findViewById(R.id.Contenedor);
		imgPhoto = (ImageView) rootView.findViewById(R.id.imgPhoto);
		imgTeam = (ImageView) rootView.findViewById(R.id.imgTeam);
		txtCode = (TextView) rootView.findViewById(R.id.txtCode);
		txtCI = (TextView) rootView.findViewById(R.id.txtCI);
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

		if (savedInstanceState == null) {
			// Ejecutar la Tareas Asincrona de acuerdo a la version de Android
			LoadInfo Task = new LoadInfo();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				Task.execute();
			}
		} else
			restoreMe(savedInstanceState);

		((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle("Mis Datos");
		return rootView;
	}

	private void restoreMe(Bundle state) {
		if (state == null)
			return;

		Collaborator.put("name", state.getString("name"));
		Collaborator.put("code", state.getString("code"));
		Collaborator.put("personal_id", state.getString("personal_id"));
		Collaborator.put("nick_name", state.getString("nick_name"));
		Collaborator.put("birth", state.getString("birth"));
		Collaborator.put("age", state.getString("age"));
		Collaborator.put("marital_status", state.getString("marital_status"));
		Collaborator.put("address", state.getString("address"));
		Collaborator.put("mobile", state.getString("mobile"));
		Collaborator.put("telef1", state.getString("telef1"));
		Collaborator.put("telef2", state.getString("telef2"));
		Collaborator.put("email", state.getString("email"));
		Collaborator.put("im_account", state.getString("im_account"));
		Collaborator.put("join_date", state.getString("join_date"));
		Collaborator.put("age_in_ministry", state.getString("age_in_ministry"));
		Collaborator.put("points", state.getString("points"));
		Collaborator.put("level", state.getString("level"));

		Collaborator.put("team", "");
		String[] TeamArray = state.getStringArray("team");
		if (Collaborator.get("team").toString() != "") {
			HashMap<String, Object> Team = (HashMap<String, Object>) Collaborator.get("team");
			Collaborator.put("team", Team);
		}
		Collaborator.put("areas", "");
		ShowInfo();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (Collaborator != null) {
			outState.putString("name", Collaborator.get("name").toString());
			outState.putString("code", Collaborator.get("code").toString());
			outState.putString("personal_id", Collaborator.get("personal_id").toString());
			outState.putString("nick_name", Collaborator.get("nick_name").toString());
			outState.putString("birth", Collaborator.get("birth").toString());
			outState.putString("age", Collaborator.get("age").toString());
			outState.putString("marital_status", Collaborator.get("marital_status").toString());
			outState.putString("address", Collaborator.get("address").toString());
			outState.putString("mobile", Collaborator.get("mobile").toString());
			outState.putString("telef1", Collaborator.get("telef1").toString());
			outState.putString("telef2", Collaborator.get("telef2").toString());
			outState.putString("email", Collaborator.get("email").toString());
			outState.putString("im_account", Collaborator.get("im_account").toString());
			outState.putString("join_date", Collaborator.get("join_date").toString());
			outState.putString("age_in_ministry", Collaborator.get("age_in_ministry").toString());
			outState.putString("points", Collaborator.get("points").toString());
			outState.putString("level", Collaborator.get("level").toString());

			outState.putStringArray("team", null);
			if (Collaborator.get("team").toString() != "") {
			
				HashMap<String, Object> Team = (HashMap<String, Object>) Collaborator.get("team");
				TeamArray = new String[] { Team.get("id").toString(), Team.get("name").toString() };
				outState.putStringArray("team", TeamArray);
			}
		}
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

	@SuppressWarnings("unchecked")
	void ShowInfo() {
		// Cargar la Foto
		imgPhoto.setImageBitmap(hupernikao.getRoundedCornerBitmap(config.getCollaboratorPhoto(), true));

		/*
		 * CAMPOS A MOSTAR - code - name - nick_name - birth - marital_status -
		 * address - image_medium - mobile - telef1 - telef2 - email -
		 * im_account - team - join_date - points - level
		 */
		txtCode.setText(Collaborator.get("code").toString());
		txtCI.setText(Collaborator.get("personal_id").toString());
		if ((Collaborator.get("personal_id").toString()).equals("")) {
			txtCI.setText("--");
		}
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

		txtTeam.setVisibility(View.INVISIBLE);
		imgTeam.setVisibility(View.INVISIBLE);
		if (Collaborator.get("team").toString() != "") {
			HashMap<String, Object> Team = (HashMap<String, Object>) Collaborator.get("team");
			txtTeam.setText(Team.get("name").toString());
			txtTeam2.setText(Team.get("name").toString());
			txtTeam.setVisibility(View.VISIBLE);
			imgTeam.setVisibility(View.VISIBLE);

			// Ejecutar la Tarea de acuerdo a la version de Android
			LoadTeamLogo Task = new LoadTeamLogo(Long.parseLong(Team.get("id").toString()));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				Task.execute();
			}
		} else {
			txtTeam2.setText("-- ");
		}

		// Procesar las areas
		if (Collaborator.get("areas").toString() != "") {
			List<AreasItem> ItemsAreas = new ArrayList<AreasItem>();
			Object[] Areas = (Object[]) Collaborator.get("areas");
			for (Object Area : Areas) {
				ItemsAreas.add(new AreasItem(Area));
			}
			lstAreas.setAdapter(new AreasItemAdapter(getActivity(), ItemsAreas));
			ListViewDinamicSize.getListViewSize(lstAreas);
			makeScroll(0);
		}

		Contenedor.setVisibility(View.VISIBLE);

		// Cargar la foto Actualiza del Colaborador
		LoadCollaboratorPhoto Task = new LoadCollaboratorPhoto(Long.parseLong(config.getCollaboratorID()));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			Task.execute();
		}
	}

	class LoadInfo extends AsyncTask<Integer, Void, Integer> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Collaborator = null;
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

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			if (Collaborator != null) {
				pDialog.dismiss();
				ShowInfo();
			} else {
				pDialog.dismiss();
				Toast.makeText(getActivity(), "No se pudieron recuperar los datos.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Clase Asincrona para recuperar el logo del Equipo
	 **/
	protected class LoadTeamLogo extends AsyncTask<String, Void, String> {
		HashMap<String, Object> Team = new HashMap<String, Object>();
		long TeamID;

		public LoadTeamLogo(long TeamID) {
			this.TeamID = TeamID;
			Team = null;
		}

		@Override
		protected String doInBackground(String... params) {
			OpenERP oerp = hupernikao.BuildOpenERPConnection(config);
			Team = oerp.read("kemas.team", this.TeamID, new String[] { "logo_medium" });
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (Team != null) {
				// Cargar la imagen del Equipo
				byte[] logo = Base64.decode(Team.get("logo_medium").toString(), Base64.DEFAULT);
				Bitmap bmp = BitmapFactory.decodeByteArray(logo, 0, logo.length);
				imgTeam.setImageBitmap(hupernikao.getRoundedCornerBitmapSimple(bmp));
			}
		}
	}

	/**
	 * Clase Asincrona para recuparar la foto del Colaborador
	 **/
	protected class LoadCollaboratorPhoto extends AsyncTask<String, Void, String> {
		HashMap<String, Object> Collaborator = null;
		long CollaboratorID;

		public LoadCollaboratorPhoto(long CollaboratorID) {
			this.CollaboratorID = CollaboratorID;
		}

		@Override
		protected String doInBackground(String... params) {
			OpenERP oerp = hupernikao.BuildOpenERPConnection(config);
			Collaborator = oerp.read("kemas.collaborator", this.CollaboratorID, new String[] { "photo_large" });
			config.setCollaboratorPhoto(Collaborator.get("photo_large").toString());
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Cargar la imagen del usuario
			imgPhoto.setImageBitmap(hupernikao.getRoundedCornerBitmap(config.getCollaboratorPhoto(), true));
		}
	}
}