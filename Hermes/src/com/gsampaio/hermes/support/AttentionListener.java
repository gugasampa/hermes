package com.gsampaio.hermes.support;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AttentionListener implements SensorEventListener {

	  //Movimento mínimo a ser considerado
	  private static final int MIN_FORCE = 10;
	  
	  //Quantidade mínima de vezes em que a direção do movimento precisa mudar 
	  private static final int MIN_DIRECTION_CHANGE = 3;
	
	  //Pausa máxima entre movimentos
	  private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;
	
	  //Tempo máximo de duração da "sacudida"
	  private static final int MAX_TOTAL_DURATION_OF_SHAKE = 400;
	
	  //Hora em que o gesto foi iniciado
	  private long mFirstDirectionChangeTime = 0;
	
	  //Hora em que o último gesto foi iniciado
	  private long mLastDirectionChangeTime;
	
	  //Movimentos considerados até agora
	  private int mDirectionChangeCount = 0;
	
	  private float lastX = 0;
	  private float lastY = 0;
	  private float lastZ = 0;
	
	  private OnShakeListener mShakeListener;
	
	  public interface OnShakeListener {
	    void onShake();
	  }
	
	  public void setOnShakeListener(OnShakeListener listener) {
	    mShakeListener = listener;
	  }
	
	  @Override
	  public void onSensorChanged(SensorEvent se) {
	    float x = se.values[SensorManager.DATA_X];
	    float y = se.values[SensorManager.DATA_Y];
	    float z = se.values[SensorManager.DATA_Z];
	
	    // calcula o movimento
	    float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);
	
	    if (totalMovement > MIN_FORCE) {

	      long now = System.currentTimeMillis();
	
	      if (mFirstDirectionChangeTime == 0) {
	        mFirstDirectionChangeTime = now;
	        mLastDirectionChangeTime = now;
	      }
	
	      long lastChangeWasAgo = now - mLastDirectionChangeTime;
	      if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {
	
	        mLastDirectionChangeTime = now;
	        mDirectionChangeCount++;
	
	        lastX = x;
	        lastY = y;
	        lastZ = z;
	
	        if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {
	
	          long totalDuration = now - mFirstDirectionChangeTime;
	          if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
	            mShakeListener.onShake();
	            resetShakeParameters();
	          }
	        }
	
	      } else {
	        resetShakeParameters();
	      }
	    }
	  }
	
	  private void resetShakeParameters() {
	    mFirstDirectionChangeTime = 0;
	    mDirectionChangeCount = 0;
	    mLastDirectionChangeTime = 0;
	    lastX = 0;
	    lastY = 0;
	    lastZ = 0;
	  }
	
	  @Override
	  public void onAccuracyChanged(Sensor sensor, int accuracy) {
	  }

}
