package com.gsampaio.hermes.support;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class Voice {
	
	private static String mText;
	private static TextToSpeech mTTS;
	private static Context mContext;
	
	private static void speak(final Context context, final String text) {
		mText = text;
		if (mTTS == null){
			mContext = context;
			mTTS = new TextToSpeech(mContext, ttsListener);
		} else {
			mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	public static void speak(final Context context, final String texto, final boolean waitTalkAll) {
		speak(context, texto);
		while(mTTS.isSpeaking() && waitTalkAll){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static OnInitListener ttsListener = new OnInitListener() {		
		public void onInit(int status) {
			if(mTTS == null){
				speak(mContext, mText);
			} else {
				mTTS.speak(mText, TextToSpeech.QUEUE_FLUSH, null);
			}
			
		}
	};
	
	public static void shutUp() {
		if (mTTS != null) {
			mTTS.stop();
			mTTS.shutdown();
		}
	}
	
	public static void repeat() {
		mTTS.speak(mText, TextToSpeech.QUEUE_FLUSH, null);
	}

	public static void pause() {
		if (mTTS != null) {
			mTTS.stop();
		}
	}
}
