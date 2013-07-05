package com.gsampaio.hermes;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainBoard extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_board);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_board, menu);
        return true;
    }
}
