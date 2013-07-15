package com.gsampaio.hermes;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.gsampaio.hermes.support.BoardPagerAdapter;
import com.gsampaio.hermes.support.PageFragment;

public class MainBoard extends FragmentActivity {
	
    private BoardPagerAdapter pagerAdapter;
    private ViewPager mPager;
    private static final int NUMBER_PAGES = 3;
    private int board_id;

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
    
    private List<Fragment> getFragments(){
    	List<Fragment> fList = new ArrayList<Fragment>();
    	for(int i=0; i<NUMBER_PAGES; i++){
    		fList.add(PageFragment.newInstance(this, board_id, i));
    	}
    	return fList;
    }
}
