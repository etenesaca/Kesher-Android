package com.kemas.item.adapters;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kemas.Configuration;
import com.kemas.R;
import com.kemas.hupernikao;

/**
 * Custom adapter with "View Holder Pattern".
 * 
 * @author danielme.com
 * 
 */
@SuppressLint("NewApi")
public class EventsItemAdapter extends ArrayAdapter<HashMap<String, Object>> {
	protected boolean WithoutItems = false;

	public boolean isWithoutItems() {
		return WithoutItems;
	}

	private String EventsState;
	private LayoutInflater layoutInflater;
	private Context CTX;
	@SuppressWarnings("unused")
	private Configuration config;

	Typeface Roboto_Bold;
	Typeface Roboto_Light;

	public EventsItemAdapter(Context context, List<HashMap<String, Object>> objects, String EventsState) {
		super(context, 0, objects);
		this.CTX = context;
		this.EventsState = EventsState;
		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(CTX);

		layoutInflater = LayoutInflater.from(context);
		Roboto_Bold = Typeface.createFromAsset(CTX.getAssets(), "fonts/Roboto-Bold.ttf");
		Roboto_Light = Typeface.createFromAsset(CTX.getAssets(), "fonts/Roboto-Light.ttf");

		if (objects.size() == 0) {
			this.WithoutItems = true;
			objects.add(new HashMap<String, Object>());
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// holder pattern
		AttendancesItem holder = null;
		if (this.WithoutItems) {
			if (convertView == null) {
				holder = new AttendancesItem();
				convertView = layoutInflater.inflate(R.layout.no_records, null);
				convertView.setTag(holder);
			} else {
				holder = (AttendancesItem) convertView.getTag();
			}

			TextView tvNoRecords = (TextView) convertView.findViewById(R.id.tvNoRecords);
			String text = "No hay eventos para mostrar";
			if (EventsState.equals("on_going"))
				text = "No hay eventos Próximos para mostrar";
			else if (EventsState.equals("closed"))
				text = "No hay eventos Cerrados para mostrar";

			tvNoRecords.setText(text);
			convertView.setEnabled(false);
			convertView.setOnClickListener(null);
			return convertView;
		}

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
			tvMoreCollaborators = (TextView) convertView.findViewById(R.id.tvMoreCollaborators);

			tvName.setVisibility(View.INVISIBLE);
			tvDay.setVisibility(View.INVISIBLE);

			ivCl1.setVisibility(View.VISIBLE);
			ivCl2.setVisibility(View.VISIBLE);
			ivCl3.setVisibility(View.VISIBLE);
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

			int numCollaborators = Integer.parseInt(Record.get("num_collaborators").toString());
			int maxCollaborators = 3;

			tvMoreCollaborators.setText(numCollaborators + "");
			if (numCollaborators > maxCollaborators) {
				tvMoreCollaborators.setText((numCollaborators - maxCollaborators) + "+");
			} else {
				tvMoreCollaborators.setVisibility(View.GONE);
				if (numCollaborators == 2) {
					ivCl3.setVisibility(View.GONE);
				} else if (numCollaborators == 1) {
					ivCl2.setVisibility(View.GONE);
					ivCl3.setVisibility(View.GONE);
				}
			}

			// Mostrar las fotos de los colaboradores
			List<Bitmap> Avatars = (List<Bitmap>) Record.get("avatars");
			int count = 0;
			for (Bitmap Avatar : Avatars) {
				count++;
				// Ejecutar la Tarea de acuerdo a la version de Android
				ImageView ivCollaborator = new ImageView(CTX);
				if (count == 1)
					ivCollaborator = ivCl1;
				else if (count == 2)
					ivCollaborator = ivCl2;
				else if (count == 3)
					ivCollaborator = ivCl3;
				else
					break;
				if (Avatar != null)
					ivCollaborator.setImageBitmap(hupernikao.getRoundedCornerBitmapSimple(Avatar));
			}
		}
	}
}