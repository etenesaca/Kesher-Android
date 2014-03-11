package com.kemas.item.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kemas.R;
import com.kemas.hupernikao;

public class AreasItemAdapter extends BaseAdapter {

	private Context context;
	private List<AreasItem> items;

	public AreasItemAdapter(Context context, List<AreasItem> items) {
		this.context = context;
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

		// Create a new view into the list.
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_item_area, parent, false);

		// Set data into the view.
		TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
		tvTitle.setText(this.items.get(position).getTitle());
		
		ImageView ivItem = (ImageView) rowView.findViewById(R.id.ivItem);
		String LogoStr = this.items.get(position).getImage().toString();
		if (LogoStr != "") {
			byte[] logo = Base64.decode(LogoStr, Base64.DEFAULT);
			Bitmap bmp = BitmapFactory.decodeByteArray(logo, 0, logo.length);
			ivItem.setImageBitmap(hupernikao.getRoundedCornerBitmapSimple(bmp));
		}

		return rowView;
	}
}
