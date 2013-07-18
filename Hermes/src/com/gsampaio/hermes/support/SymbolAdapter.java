package com.gsampaio.hermes.support;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
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
	private Uri outputFileUri;

	private static final int PICTURE_REQUEST_CODE = 1;
	
	protected int outputX = 400;
	protected int outputY = 400;
	protected int aspectX = 1;
	protected int aspectY = 1;

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
		  btn.setBackgroundResource(R.drawable.add);
		  btn.setId(position);
		  btn.setOnClickListener(new blankSymbol());
	  }else{
		  if(symbol.isPermanent()==1){
			  int resID = mContext.getResources().getIdentifier(symbol.getImage_path(), "drawable", mContext.getPackageName());
			  btn.setBackgroundDrawable(mContext.getResources().getDrawable(resID));
		  }else{
			  btn.setBackgroundDrawable(Drawable.createFromPath(symbol.getImage_path()));
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
	 
	 OnClickListener finalSymbol = new OnClickListener(){
		@Override
		public void onClick(View v) {
			MainBoard.speak(v.getTag().toString());
		}
	 };
	 
	 class blankSymbol implements OnClickListener{
		 
		 public blankSymbol(){
		 }
		 
		 public void onClick(View v){
			openImageIntent(v.getId());
		 }
	 }
	 
	 class categorySymbol implements OnClickListener{
		 private final int child_board_id;
		 
		 public categorySymbol(int child_board_id){
			 this.child_board_id = child_board_id;
		 }
		 
		 public void onClick(View v){
			MainBoard.speak(v.getTag().toString());
			Intent intent = new Intent(mContext, MainBoard.class);
			intent.putExtra("board_id", child_board_id);
			mContext.startActivity(intent);
		 }
	 }
	 
	 private void openImageIntent(int btn_id) {

		// Determina Uri da imagem para salva
	    final File root = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "Hermes" + File.separator);
		root.mkdirs();
		String fname = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		final File sdImageMainDirectory = new File(root, fname);
		outputFileUri = Uri.fromFile(sdImageMainDirectory);
	
		    // Camera.
		    final List<Intent> cameraIntents = new ArrayList<Intent>();
		    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    final PackageManager packageManager = mContext.getPackageManager();
		    final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
		    for(ResolveInfo res : listCam) {
		        final String packageName = res.activityInfo.packageName;
		        final Intent intent = new Intent(captureIntent);
		        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        intent.setPackage(packageName);
		        
		        intent.putExtra("crop", "true");
		        intent.putExtra("aspectX", aspectX);
		        intent.putExtra("aspectY", aspectY);
		        intent.putExtra("outputX",outputX);	
		        intent.putExtra("outputY", outputY);
		        intent.putExtra("scale", true);
			    
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		        cameraIntents.add(intent);
		    }
	
		    // Filesystem
		    final Intent galleryIntent = new Intent();
		    galleryIntent.setType("image/*");
		    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
		    galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		    
		    galleryIntent.putExtra("crop", "true");
		    galleryIntent.putExtra("aspectX", aspectX);
		    galleryIntent.putExtra("aspectY", aspectY);
		    galleryIntent.putExtra("outputX", outputX);	
		    galleryIntent.putExtra("outputY", outputY);
		    galleryIntent.putExtra("scale", true);
		    // opções
		    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
	
		    // Adiciona a camera nas opções
		    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
		    ((MainBoard) mContext).setUri(outputFileUri, btn_id);
		    ((MainBoard) mContext).startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
		    
	 }
		
	
}
