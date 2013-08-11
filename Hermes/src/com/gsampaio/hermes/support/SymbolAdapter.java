package com.gsampaio.hermes.support;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

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
			  btn.setOnLongClickListener(deleteCategorySymbol);
		  }else{
			  btn.setOnClickListener(finalSymbol);
			  btn.setOnLongClickListener(deletefinalSymbol);
		  }
	  }
	  return btn;
	 }
	 
	 OnClickListener finalSymbol = new OnClickListener(){
		@Override
		public void onClick(View v) {
			HApplication application = (HApplication) mContext.getApplicationContext();
			if(v.getTag().toString().equals("não") && v.getId()!=4){
				MainBoard.speak(v.getTag().toString());
				application.addSentence(v.getTag().toString());
			}else{
				application.speakSentences(v.getTag().toString());
			}
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
			HApplication application = (HApplication) mContext.getApplicationContext();
			application.addSentence(v.getTag().toString());
			Intent intent = new Intent(mContext, MainBoard.class);
			intent.putExtra("board_id", child_board_id);
			mContext.startActivity(intent);
		 }
	 }
	 
	 private OnLongClickListener deleteCategorySymbol = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			final int symbol_id = v.getId();
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("Cuidado!");
			dialog.setMessage("Você tem certeza que deseja apagar esse símbolo e todas as suas pranchas filhas?");
			
			dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DBHelper db = new DBHelper(mContext);
					boolean deleted = db.deleteCategorySymbol(symbol_id);
					dialog.dismiss();
					if(deleted){
						((MainBoard) mContext).finish();
						Intent intent = new Intent(mContext, MainBoard.class);
				    	intent.putExtra("board_id", board_id);
				    	mContext.startActivity(intent);
				    	Toast.makeText(mContext, "Símbolo excluído com sucesso!", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(mContext, "Este símbolo não pode ser excluído!", Toast.LENGTH_LONG).show();
					}
				}
			});
			dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.create().show();
			return true;
		}
	}; 
	
	private OnLongClickListener deletefinalSymbol = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			final int symbol_id = v.getId();
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("Deseja excluir?");
			dialog.setMessage("Você tem certeza que deseja apagar esse símbolo?");
			
			dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DBHelper db = new DBHelper(mContext);
					boolean deleted = db.deleteFinalSymbol(symbol_id);
					dialog.dismiss();
					if(deleted){
						((MainBoard) mContext).finish();
						Intent intent = new Intent(mContext, MainBoard.class);
				    	intent.putExtra("board_id", board_id);
				    	mContext.startActivity(intent);
				    	Toast.makeText(mContext, "Símbolo excluído com sucesso!", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(mContext, "Este símbolo não pode ser excluído!", Toast.LENGTH_LONG).show();
					}
				}
			});
			dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.create().show();
			return true;
		}
	}; 
	
	
	
	
	
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
