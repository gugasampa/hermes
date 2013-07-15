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
	 public static int board_id;
	 public static int page;
	 
	 public static final PageFragment newInstance(Activity activity, int _board_id, int _page)
	 {
	   ctx = activity;
	   
	   PageFragment f = new PageFragment();
	   Bundle args = new Bundle();
       args.putInt("board_id", _board_id);
       args.putInt("page", _page);
       f.setArguments(args);
	   return f;
	 }
	 
	 @Override public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        Bundle args = getArguments();
	        if (args != null) {
	        	board_id = args.getInt("board_id");
	            page = args.getInt("page");
	        }
	    }
	 
	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	   Bundle savedInstanceState) {
	   View v = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
	   GridView gridview = (GridView) v.findViewById(R.id.itensGrid);
	   gridview.setAdapter(new SymbolAdapter(ctx, board_id, page));
	 
	   return v;
	 }
}
