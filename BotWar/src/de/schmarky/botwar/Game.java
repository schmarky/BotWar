package de.schmarky.botwar;

import android.os.Bundle;
import android.view.Window;

import android.app.Activity;

/**
 * The Game activity creates a new GameSurfaceView instance and starts
 * the game.
 * 
 * @author Mark Pommerening (sdchmarky.de)
 * @version 1.0
 */
public class Game extends Activity{

	GameSurfaceView mSurface;
	float x, y;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSurface = new GameSurfaceView(this);
        //mSurface.setOnTouchListener(this);
        setContentView(mSurface);
    }

    
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSurface.pause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSurface.resume();
	}

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }*/
    
}
