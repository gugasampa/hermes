package com.gsampaio.hermes.support;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.gsampaio.hermes.R;

public class ItemAdapter extends BaseAdapter {

	private Context mContext;
	
	public String[] filesnames = {
			"File 1",
			"File 2",
			"Roflcopters",
			"1",
			"2",
			"3",
			"4",
			"5"
			};

	 public ItemAdapter(Context c) {
	  mContext = c;
	 }

	 public int getCount() {
	  return filesnames.length;
	 }

	 public Object getItem(int position) {
	  return null;
	 }

	 public long getItemId(int position) {
	  return position;
	 }

	 public View getView(int position,
	                           View convertView, ViewGroup parent) {
	  HButton btn;
	  if (convertView == null) {
	   btn = new HButton(mContext);
	   int size = (int) mContext.getResources().getDimension(R.dimen.item_size);
	   btn.setLayoutParams(new GridView.LayoutParams(size, size));
	   btn.setPadding(8, 8, 8, 8);
	   }
	  else {
	   btn = (HButton) convertView;
	  }
	  btn.setBackgroundResource(R.drawable.quero);
	  btn.setId(position);
	  return btn;
	 }
	
}
