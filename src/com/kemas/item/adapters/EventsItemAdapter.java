package com.kemas.item.adapters;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.R;
import com.kemas.hupernikao;

/**
 * Custom adapter with "View Holder Pattern".
 * 
 * @author danielme.com
 * 
 */
@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class EventsItemAdapter extends ArrayAdapter<HashMap<String, Object>> {
	private LayoutInflater layoutInflater;
	private Context CTX;
	private Configuration config;

	Typeface Roboto_Bold;
	Typeface Roboto_Light;

	public EventsItemAdapter(Context context, List<HashMap<String, Object>> objects) {
		super(context, 0, objects);
		this.CTX = context;
		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(CTX);

		layoutInflater = LayoutInflater.from(context);
		Roboto_Bold = Typeface.createFromAsset(CTX.getAssets(), "fonts/Roboto-Bold.ttf");
		Roboto_Light = Typeface.createFromAsset(CTX.getAssets(), "fonts/Roboto-Light.ttf");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// holder pattern
		AttendancesItem holder = null;
		if (convertView == null) {
			holder = new AttendancesItem();
			convertView = layoutInflater.inflate(R.layout.list_item_event, null);
			convertView.setTag(holder);
		} else {
			holder = (AttendancesItem) convertView.getTag();
		}

		HashMap<String, Object> Record = getItem(position);

		// Ejecutar la Tarea de acuerdo a la version de Android
		LoadView Task = new LoadView(convertView, Record);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			Task.execute();
		}
		return convertView;
	}

	/** Clase Asincrona para recuperar los datos de la fila **/
	protected class LoadView extends AsyncTask<String, Void, String> {
		protected HashMap<String, Object> Record;
		protected View convertView;

		protected TextView tvName;
		protected TextView txtHours;
		protected TextView tvNumberDay;
		protected TextView tvDay;
		protected TextView tvMonthYear;
		protected TextView tvState;

		protected ImageView ivCl1;
		protected ImageView ivCl2;
		protected ImageView ivCl3;
		protected ImageView ivCl4;
		protected ImageView ivCl5;
		protected ImageView ivCl6;
		protected TextView tvMoreCollaborators;

		public LoadView(View convertView, HashMap<String, Object> Record) {
			this.Record = Record;
			this.convertView = convertView;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			tvName = (TextView) convertView.findViewById(R.id.tvName);
			txtHours = (TextView) convertView.findViewById(R.id.txtHours);
			tvDay = (TextView) convertView.findViewById(R.id.tvDay);
			tvNumberDay = (TextView) convertView.findViewById(R.id.tvNumberDay);
			tvMonthYear = (TextView) convertView.findViewById(R.id.tvMonthYear);
			tvState = (TextView) convertView.findViewById(R.id.tvState);

			ivCl1 = (ImageView) convertView.findViewById(R.id.ivCl1);
			ivCl2 = (ImageView) convertView.findViewById(R.id.ivCl2);
			ivCl3 = (ImageView) convertView.findViewById(R.id.ivCl3);
			ivCl4 = (ImageView) convertView.findViewById(R.id.ivCl4);
			ivCl5 = (ImageView) convertView.findViewById(R.id.ivCl5);
			ivCl6 = (ImageView) convertView.findViewById(R.id.ivCl6);
			tvMoreCollaborators = (TextView) convertView.findViewById(R.id.tvMoreCollaborators);

			tvName.setVisibility(View.INVISIBLE);
			tvDay.setVisibility(View.INVISIBLE);

			ivCl1.setVisibility(View.VISIBLE);
			ivCl2.setVisibility(View.VISIBLE);
			ivCl2.setVisibility(View.VISIBLE);
			ivCl3.setVisibility(View.VISIBLE);
			ivCl4.setVisibility(View.VISIBLE);
			ivCl5.setVisibility(View.VISIBLE);
			ivCl6.setVisibility(View.VISIBLE);
			tvMoreCollaborators.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... arg0) {
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(String result) {
			tvName.setTypeface(Roboto_Bold);
			tvName.setText(Record.get("service").toString());
			tvState.setTypeface(Roboto_Light);
			tvState.setText(Record.get("state").toString());

			HashMap<String, Object> Date = (HashMap<String, Object>) Record.get("date");
			tvDay.setText(Date.get("day_name").toString());
			tvNumberDay.setText(Date.get("day").toString());
			tvMonthYear.setText(Date.get("month_name").toString() + " " + Date.get("year").toString());
			txtHours.setText(Record.get("hours").toString());

			tvName.setVisibility(View.VISIBLE);
			tvDay.setVisibility(View.VISIBLE);

			Object[] collaborator_ids = (Object[]) Record.get("collaborator_ids");

			int numCollaborators = collaborator_ids.length;
			int maxCollaborators = 6;

			Log.v("ID=" + Record.get("id").toString() + "  -- size=" + numCollaborators, collaborator_ids[0].toString() + ", " + collaborator_ids[1].toString());
			tvMoreCollaborators.setText(numCollaborators + "");
			if (numCollaborators > maxCollaborators) {
				tvMoreCollaborators.setText("+" + (numCollaborators - maxCollaborators));
			} else {
				tvMoreCollaborators.setVisibility(View.GONE);
				if (numCollaborators == 5) {
					ivCl6.setVisibility(View.GONE);
				} else if (numCollaborators == 4) {
					ivCl5.setVisibility(View.GONE);
					ivCl6.setVisibility(View.GONE);
				} else if (numCollaborators == 3) {
					ivCl4.setVisibility(View.GONE);
					ivCl5.setVisibility(View.GONE);
					ivCl6.setVisibility(View.GONE);
				} else if (numCollaborators == 2) {
					ivCl3.setVisibility(View.GONE);
					ivCl4.setVisibility(View.GONE);
					ivCl5.setVisibility(View.GONE);
					ivCl6.setVisibility(View.GONE);
				} else if (numCollaborators == 1) {
					ivCl2.setVisibility(View.GONE);
					ivCl3.setVisibility(View.GONE);
					ivCl4.setVisibility(View.GONE);
					ivCl5.setVisibility(View.GONE);
					ivCl6.setVisibility(View.GONE);
				}
			}
			if (!Record.containsKey("CollaboratorsProceceds")) {
				int count = 0;
				for (Object CollaboratorID : collaborator_ids) {
					count++;
					// Ejecutar la Tarea de acuerdo a la version de Android
					ImageView ivCollaborator = null;
					if (count == 1)
						ivCollaborator = ivCl1;
					else if (count == 2)
						ivCollaborator = ivCl2;
					else if (count == 3)
						ivCollaborator = ivCl3;
					else if (count == 4)
						ivCollaborator = ivCl4;
					else if (count == 5)
						ivCollaborator = ivCl5;
					else if (count == 6)
						ivCollaborator = ivCl6;
					else
						break;

					getCollaboratorEvent Task = new getCollaboratorEvent(Record, Long.parseLong(CollaboratorID.toString()), ivCollaborator);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						Task.execute();
					}
				}
			}
		}
	}

	/** Clase Asincrona para reciclar los los items del listview **/
	protected class getCollaboratorEvent extends AsyncTask<String, Void, String> {
		HashMap<String, Object> Record;
		HashMap<String, Object> Collaborator;
		long CollaboratorID;
		ImageView ivCollaborator;

		public getCollaboratorEvent(HashMap<String, Object> Record, long CollaboratorID, ImageView ivCollaborator) {
			this.Record = Record;
			this.CollaboratorID = CollaboratorID;
			this.ivCollaborator = ivCollaborator;
		}

		@Override
		protected String doInBackground(String... params) {
			if (!hupernikao.TestNetwork(CTX))
				return null;

			if (OpenERP.TestConnection(config.getServer(), Integer.parseInt(config.getPort().toString()))) {
				OpenERP oerp = hupernikao.BuildOpenERPConnection(config);

				Collaborator = oerp.getCollaboratorforEvent(CollaboratorID);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (Collaborator != null) {
				try {
					byte[] photo = Base64.decode(Collaborator.get("photo_small").toString(), Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
					ivCollaborator.setImageBitmap(hupernikao.getRoundedCornerBitmapSimple(bmp));
				} catch (Exception e) {
				}
			}
		}
	}
}