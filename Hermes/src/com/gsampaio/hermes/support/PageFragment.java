package com.gsampaio.hermes.support;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.gsampaio.hermes.R;

public class PageFragment extends Fragment {

	 public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	 public static Activity ctx;
	 
	 public static final PageFragment newInstance(Activity activity)
	 {
	   ctx = activity;
	   PageFragment f = new PageFragment();
	   return f;
	 }
	 
	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	   Bundle savedInstanceState) {
	   View v = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
	   GridView gridview = (GridView) v.findViewById(R.id.itensGrid);
	   gridview.setAdapter(new ItemAdapter(ctx));
	 
	   return v;
	 }
}
