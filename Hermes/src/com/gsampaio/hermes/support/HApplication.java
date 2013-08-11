package com.gsampaio.hermes.support;

import java.util.ArrayList;

import com.gsampaio.hermes.MainBoard;

import android.app.Application;

public class HApplication extends Application {

	private ArrayList<String> sentences;
	
	@Override
	public void onCreate(){
		sentences = new ArrayList<String>();
		super.onCreate();			
	}
	
	public void addSentence (String sentence){
		sentences.add(sentence);
	}
	
	public void removeLastSentence(){
		if(sentences.size()>0){
			sentences.remove(sentences.size()-1);
			if(sentences.size()>0){
				if(sentences.get(sentences.size()-1).equals("não")){
					sentences.remove(sentences.size()-1);
				}
			}
		}
		
	}
	
	public void speakSentences(String finalsymbol){
		String fullSentence="";
		
		for(int i=0; i<sentences.size(); i++){
			fullSentence += sentences.get(i)+" ";
		}
		
		MainBoard.speak(fullSentence+" "+finalsymbol);
				
	}
	
	
}
