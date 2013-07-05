package com.gsampaio.hermes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class HButton extends Button{
	public HButton (Context context, AttributeSet attrs){
		super(context, attrs);		
		setOnClickListener(sayButton);
	}
	
	private OnClickListener sayButton = new OnClickListener() {		
		public void onClick(View v) {
			Voice.speak(getContext(), getTag().toString(), false);	
		}
	};
}
