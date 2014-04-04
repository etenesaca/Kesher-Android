package com.kemas.item.adapters;

import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.R;
import com.kemas.hupernikao;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AreasItemAdapter extends BaseAdapter {
	private Configuration config;
	private Context CTX;
	private List<AreasItem> items;

	public AreasItemAdapter(Context context, List<AreasItem> items) {
		this.CTX = context;
		this.items = items;
	}

	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public Object getItem(int position) {
		return this.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(CTX);

		// Create a new view into the list.
		LayoutInflater inflater = (LayoutInflater) CTX.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_item_area, parent, false);

		// Set data into the view.
		TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
		ImageView ivItem = (ImageView) rowView.findViewById(R.id.ivItem);

		tvTitle.setText(this.items.get(position).getName());

		LoadImageArea Task = new LoadImageArea(this.items.get(position).getID(), ivItem);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			Task.execute();
		}

		return rowView;
	}

	/**
	 * Clase Asincrona para recuperar el logo de las Areas
	 **/
	protected class LoadImageArea extends AsyncTask<String, Void, String> {
		HashMap<String, Object> Area = null;
		long AreaID;
		ImageView ivItem;

		public LoadImageArea(long UserID, ImageView ivItem) {
			this.AreaID = UserID;
			this.ivItem = ivItem;
		}

		@Override
		protected String doInBackground(String... params) {
			OpenERP oerp = hupernikao.BuildOpenERPConnection(config);
			Area = oerp.read("kemas.area", this.AreaID, new String[] { "logo_small" });
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Cargar el logo del Area
			byte[] logo = Base64.decode(Area.get("logo_small").toString(), Base64.DEFAULT);
			Bitmap bmp = BitmapFactory.decodeByteArray(logo, 0, logo.length);
			ivItem.setImageBitmap(hupernikao.getRoundedCornerBitmapSimple(bmp));
		}
	}
}
