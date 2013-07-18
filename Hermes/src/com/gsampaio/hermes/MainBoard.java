package com.gsampaio.hermes;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.gsampaio.hermes.database.DBHelper;
import com.gsampaio.hermes.database.Symbol;
import com.gsampaio.hermes.support.BoardPagerAdapter;
import com.gsampaio.hermes.support.PageFragment;
public class MainBoard extends FragmentActivity {
	
    private BoardPagerAdapter pagerAdapter;
    private ViewPager mPager;
    private static final int NUMBER_PAGES = 3;
    private int board_id;
    private Uri outputFileUri;
    private int btn_id;
	private static TextToSpeech mTTS;
    private boolean isCategory=false;
    
	private static final int PICTURE_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_board);
        
        Intent intent = this.getIntent();
        board_id = intent.getIntExtra("board_id", 0);
        
        mPager = (ViewPager) findViewById(R.id.viewpager);
        List<Fragment> fragments = getFragments();
        pagerAdapter = new BoardPagerAdapter(getSupportFragmentManager(), fragments);
        mPager.setAdapter(pagerAdapter);
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
        mTTS = new TextToSpeech(this, new OnInitListener() {
			@Override
			public void onInit(int status) {}	
		});
    }
    
    @Override
    protected void onPause(){
        if (mTTS != null) {
			mTTS.stop();
			mTTS.shutdown();
			Log.i("destroyed","shutted up");
		}
        super.onPause();
    }
    
    private List<Fragment> getFragments(){
    	List<Fragment> fList = new ArrayList<Fragment>();
    	for(int i=0; i<NUMBER_PAGES; i++){
    		fList.add(PageFragment.newInstance(this, board_id, i));
    	}
    	return fList;
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
	    if(resultCode == RESULT_OK){
	        if(requestCode == PICTURE_REQUEST_CODE){
	        	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	        	dialog.setTitle("Novo símbolo");
	        	LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		View layout = inflater.inflate(R.layout.dialog, null);
	    		final EditText etText = (EditText) layout.findViewById(R.id.newSymbol);
	        	dialog.setView(layout);
	        	
	        	dialog.setPositiveButton("OK", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String text = etText.getText().toString();
						addNewSymbol(text);
						dialog.dismiss();
					}
				});
	        	dialog.setNegativeButton("Cancelar", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
	        	dialog.create().show();
	        	
	            
	        }
	        
	    }
	}
    
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.radio.categoria:
                if (checked)
                	isCategory = true;
                break;
            case R.radio.symbol_final:
                if (checked)
                	isCategory = false;
                break;
        }
    }

    public void addNewSymbol (String text){
    	DBHelper db = new DBHelper(this);
        int child_board_id = db.getNextBoardId();
        int type;
        if(isCategory){
        	type=1;
        }else{
        	type=2;
        }
    	db.addSymbol(new Symbol(board_id, child_board_id, text, outputFileUri.getPath(),
    			type, btn_id , mPager.getCurrentItem()));
    	finish();
    	Intent intent = new Intent(this, MainBoard.class);
    	intent.putExtra("board_id", board_id);
    	startActivity(intent);
    }
    
	public void setUri(Uri uri, int btn_id){
		this.outputFileUri = uri;
		this.btn_id = btn_id;
	}
	
	public static void speak(final String text) {
			mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			while(mTTS.isSpeaking()){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	}
}
