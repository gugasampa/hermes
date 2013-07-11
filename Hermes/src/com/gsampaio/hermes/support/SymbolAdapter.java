package com.gsampaio.hermes.support;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.gsampaio.hermes.MainBoard;
import com.gsampaio.hermes.R;
import com.gsampaio.hermes.database.DBHelper;
import com.gsampaio.hermes.database.Symbol;

public class SymbolAdapter extends BaseAdapter {

	private static final int NUM_SYMBOLS = 8;
	private Context mContext;
	private int board_id;
	private int page;

	 public SymbolAdapter(Context c, int board_id, int page) {
	  mContext = c;
	  this.board_id = board_id;
	  this.page = page;
	 }

	 public int getCount() {
	  return NUM_SYMBOLS;
	 }

	 public Object getItem(int position) {
	  return null;
	 }

	 public long getItemId(int position) {
	  return position;
	 }

	 public View getView(int position, //position de 0 a 7
	                           View convertView, ViewGroup parent) {
	  Button btn;
	  if (convertView == null) {
	   btn = new Button(mContext);
	   int size = (int) mContext.getResources().getDimension(R.dimen.item_size);
	   btn.setLayoutParams(new GridView.LayoutParams(size, size));
	   btn.setPadding(8, 8, 8, 8);
	   }
	  else {
	   btn = (Button) convertView;
	  }
	  
	  DBHelper db = new DBHelper(mContext);
	  Symbol symbol = db.getSymbol(board_id, page, position);
	  if(symbol.getId() == -1){ //símbolo vazio
		  btn.setBackgroundResource(R.drawable.quero);
		  btn.setId(-1);
		  btn.setOnClickListener(openCamera);
	  }else{
		  if(symbol.isPermanent()){
			  btn.setBackgroundResource((mContext.getResources()
					  .getIdentifier(symbol.getImage_path(), "drawable", mContext.getPackageName())));
		  }else{
			  //set background com caminho para imagem no cartão
		  }
		  btn.setId(symbol.getId());
		  btn.setTag(symbol.getText());
		  if(symbol.getType() == 1){
			  btn.setOnClickListener(new categorySymbol(symbol.getChild_board_id()));
		  }else{
			  btn.setOnClickListener(finalSymbol);
		  }
	  }
	  return btn;
	 }
	 
	 OnClickListener openCamera = new OnClickListener(){
		@Override
		public void onClick(View v) {
			//Abre a camera
		}
	 };
	 
	 OnClickListener finalSymbol = new OnClickListener(){
		@Override
		public void onClick(View v) {
			Voice.speak(mContext, v.getTag().toString(), false);
		}
	 };
	 
	 class categorySymbol implements OnClickListener{
		 private final int child_board_id;
		 
		 public categorySymbol(int child_board_id){
			 this.child_board_id = child_board_id;
		 }
		 
		 public void onClick(View v){
			Voice.speak(mContext, v.getTag().toString(), false);
			Intent intent = new Intent(mContext, MainBoard.class);
			intent.putExtra("board_id", child_board_id);
		 }
	 }
	
}
