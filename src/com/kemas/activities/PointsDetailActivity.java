package com.kemas.activities;

import java.util.HashMap;

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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.R;
import com.kemas.hupernikao;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class PointsDetailActivity extends ActionBarActivity {
	private Configuration config;
	private long RecordID;
	private long AttendanceID;
	Context Context = (Context) this;

	private LinearLayout Contenedor;
	private LinearLayout ContentAttendance;
	private TextView lblAttendance;
	private TextView lblTitleUser;
	private TextView lblDetails;
	private TextView lblDateTime;

	private ImageView ivGo;
	private ImageView ivUser;
	private ImageView ivType;
	private TextView tvUser;
	private TextView tvDescription;
	private TextView tvPoints;
	private TextView tvSummary;
	private TextView tvDate;
	private TextView tvHour;
	private TextView tvDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_points_detail);

		if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			// Habilitar el acceso a la red y poder conectarse al
			// servidor de OpenERP en el Hilo Principal
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		// Activar el Boton Home
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		Bundle bundle = getIntent().getExtras();
		RecordID = bundle.getLong("ID");

		Typeface Roboto_light = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		Typeface Roboto_bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
		Contenedor = (LinearLayout) findViewById(R.id.Contenedor);
		ContentAttendance = (LinearLayout) findViewById(R.id.ContentAttendance);
		lblAttendance = (TextView) findViewById(R.id.lblAttendance);
		lblTitleUser = (TextView) findViewById(R.id.lblTitleUser);
		lblTitleUser.setTypeface(Roboto_bold);
		lblDetails = (TextView) findViewById(R.id.lblDetails);
		lblDetails.setTypeface(Roboto_light);
		lblDateTime = (TextView) findViewById(R.id.lblDateTime);
		lblDateTime.setTypeface(Roboto_light);

		ivUser = (ImageView) findViewById(R.id.ivUser);
		ivGo = (ImageView) findViewById(R.id.ivGo);
		ivType = (ImageView) findViewById(R.id.ivType);
		tvUser = (TextView) findViewById(R.id.tvUser);
		tvDescription = (TextView) findViewById(R.id.tvDescription);
		tvPoints = (TextView) findViewById(R.id.tvPoints);
		tvSummary = (TextView) findViewById(R.id.tvSummary);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvHour = (TextView) findViewById(R.id.tvHour);
		tvDay = (TextView) findViewById(R.id.tvDay);

		// Ejecutar la Carga de Datos
		((ActionBarActivity) PointsDetailActivity.this).getSupportActionBar().setTitle("Historial de Puntos");

		LoadInfo Task = new LoadInfo();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			Task.execute();
		}

		ContentAttendance.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				GotoAttendance();
				return false;
			}
		});
		ContentAttendance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GotoAttendance();
			}
		});
		ContentAttendance.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				lblAttendance.setTextColor(getResources().getColor(R.color.Black));
				ContentAttendance.setBackgroundDrawable((getResources().getDrawable(R.drawable.shape)));
				ivGo.setImageDrawable((getResources().getDrawable(R.drawable.ic_action_next_item)));
				return false;
			}
		});
	}

	void GotoAttendance() {
		Toast.makeText(PointsDetailActivity.this, "ID." + AttendanceID, Toast.LENGTH_SHORT).show();
		lblAttendance.setTextColor(getResources().getColor(R.color.White));
		ContentAttendance.setBackgroundDrawable((getResources().getDrawable(R.drawable.button_drawable)));
		ivGo.setImageDrawable((getResources().getDrawable(R.drawable.ic_action_next_item_lignt)));
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

		public LoadInfo() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Contenedor.setVisibility(View.INVISIBLE);
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

				Object[] reg_uid = (Object[]) PointsDetail.get("reg_uid");
				PointsDetail.put("NameUser", reg_uid[1].toString());
				PointsDetail.put("UserID", reg_uid[0].toString());
				PointsDetail.remove("reg_uid");
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			((ActionBarActivity) PointsDetailActivity.this).getSupportActionBar().setSubtitle(PointsDetail.get("code").toString());
			tvUser.setText(PointsDetail.get("NameUser").toString());
			tvDescription.setText(PointsDetail.get("description").toString());
			tvPoints.setText(PointsDetail.get("points").toString());
			tvSummary.setText(PointsDetail.get("summary").toString());

			HashMap<String, Object> DetailDate = hupernikao.Convert_UTCtoGMT_Str(PointsDetail.get("date").toString());
			tvDate.setText(DetailDate.get("date").toString());
			tvHour.setText(DetailDate.get("hour").toString());
			tvDay.setText(DetailDate.get("day").toString());

			String PointsType = PointsDetail.get("type").toString();
			if (PointsType.equals("increase")) {
				tvPoints.setTextColor(getResources().getColor(R.color.Green));
				ivType.setImageDrawable(getResources().getDrawable(R.drawable.add));
			} else if (PointsType.equals("decrease")) {
				tvPoints.setTextColor(getResources().getColor(R.color.Red));
				ivType.setImageDrawable(getResources().getDrawable(R.drawable.remove));
			} else if (PointsType.equals("init")) {
				tvPoints.setTextColor(getResources().getColor(R.color.Green));
				ivType.setImageDrawable(getResources().getDrawable(R.drawable.ok));
			}

			if (PointsDetail.get("attendance_id").toString().equals("false"))
				ContentAttendance.setVisibility(View.GONE);
			else {
				Object[] Attendance = (Object[]) PointsDetail.get("attendance_id");
				AttendanceID = Long.parseLong(Attendance[0].toString());
			}

			Contenedor.setVisibility(View.VISIBLE);
			pDialog.dismiss();
			LoadUserImage Task = new LoadUserImage(Long.parseLong(PointsDetail.get("UserID").toString()));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				Task.execute();
			}
		}
	}

	/**
	 * Clase Asincrona para recuparar la foto del Usuario que realizo la
	 * modificacion de los puntos
	 **/
	protected class LoadUserImage extends AsyncTask<String, Void, String> {
		HashMap<String, Object> User = null;
		long UserID;

		public LoadUserImage(long UserID) {
			this.UserID = UserID;
		}

		@Override
		protected String doInBackground(String... params) {
			OpenERP oerp = hupernikao.BuildOpenERPConnection(config);
			User = oerp.read("res.users", this.UserID, new String[] { "image_small" });
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Cargar la imagen del usuario
			byte[] logo = Base64.decode(User.get("image_small").toString(), Base64.DEFAULT);
			Bitmap bmp = BitmapFactory.decodeByteArray(logo, 0, logo.length);
			ivUser.setImageBitmap(hupernikao.getRoundedCornerBitmapSimple(bmp));
		}
	}
}
