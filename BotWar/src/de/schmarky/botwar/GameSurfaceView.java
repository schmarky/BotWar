package de.schmarky.botwar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * The game view and main thread.
 * 
 * @author Mark Pommerening (sdchmarky.de)
 * @version 1.0
 */
public class GameSurfaceView extends SurfaceView implements Runnable{
  
  // constants
  private static final String LOG_TAG   = "BotWar"; 
  private static final int    TILE_EDGE = 100;
  
  // globals
  private Context       mContext;
  private SurfaceHolder mHolder;
  private Thread        mThread    = null;
  private Boolean       mRunning   = false;
  private int           mTouchedId = 0; 
  private int           mTurn      = 1;
  private int           mTeam      = GameUnit.TEAM_BLUE; // blue is start player
  
  private int mBlue = 100;
  private int mRed  = 0;
  
  private List<GameTile>          mGameTileList;
  
  //private LinkedHashMap<Integer,GameUnit> mUnitMap;
  private LinkedHashMap<Integer,GameUnit> mUnitsTeamBlue;
  private LinkedHashMap<Integer,GameUnit> mUnitsTeamRed; 
  //private HashMap<Integer,GameUi> mUiControls;
  private GameUi mNextPlayer;
  
	/**
	 * Constructor - instantiates a GameSurfaceView object
	 * @param context
	 */
	public GameSurfaceView(Context context) {
		super(context);
		mContext = context;
		mHolder = getHolder();
		mGameTileList  = new ArrayList<GameTile>();
		// create LinkedHashMap that has the last accessed object on top 
		mUnitsTeamBlue = new LinkedHashMap<Integer, GameUnit>(8,1,true);
		mUnitsTeamRed  = new LinkedHashMap<Integer, GameUnit>(8,1,true);
		//mUiControls    = new HashMap<Integer,GameUi>();
		
		initLevel(context);
		initPlayer(context);
		initUi(context);
	}
	
	/**
	 * Initialize the standard level
	 * Set position, drawable and type of each floor tile
	 * 
	 * @param context
	 */
	private void initLevel(Context context){
	  GameTile curTile;
	  int drawable = R.drawable.blue_floor;
	  int xCnt, yCnt, xPos, yPos, tileType;
	  
	  Point p = new Point();
	  tileType = GameTile.TYPE_EMPTY;
	  xPos = yPos = TILE_EDGE;
    xCnt = yCnt = 1;
	  
	  // loop over all gametile objects and set
	  // drawable and position
	  for(int i=0;i<24;i++){
	    if(i==6||i==12||i==18){
	      yCnt++;
	      xCnt=1;
	    }
	    
	    // set color depending on position
	    switch (xCnt) {
      case 1:
        if(yCnt==2||yCnt==3){
          drawable = R.drawable.lightblue_floor;
          tileType = GameTile.TYPE_BASE;
        } else{
          drawable = R.drawable.blue_floor;
          tileType = GameTile.TYPE_EMPTY;
        }
        break;
      case 2:
        if(yCnt==2||yCnt==3){
          drawable = R.drawable.blue_floor;
          tileType = GameTile.TYPE_EMPTY;
        }     
        break;
      case 3:
        drawable = R.drawable.white_floor;
        tileType = GameTile.TYPE_EMPTY;
        break;
      case 5:
        drawable = R.drawable.red_floor;
        tileType = GameTile.TYPE_EMPTY;
        break;
      case 6:
        if(yCnt==2||yCnt==3){
          drawable = R.drawable.lightred_floor;
          tileType = GameTile.TYPE_BASE;
        } 
        break;
      }
	    
      p.set(xPos*xCnt, yPos*yCnt); 
      curTile = new GameTile(context, drawable, p);
      curTile.setType(tileType);
      mGameTileList.add(curTile);
	    xCnt++;
    }  
	}
	
	private void initPlayer(Context context){
	  GameUnit curUnit;
	  Random rRan = new Random();
	  int drawable;
	  int xCnt, yCnt, xPos, yPos, dieValue;
	  int xOffsetRed = 4*TILE_EDGE;
	  xCnt = yCnt = 1;
	  
	  // each player has 8 units
	  for(int i=0;i<8;i++){
	    // multiplier for positioning
      if(xCnt==3){
        yCnt++;
        xCnt=1;
      }
      
      // calculate position
      xPos=TILE_EDGE*xCnt;
      yPos=TILE_EDGE*yCnt;
          
	    // this might have to be a different logic
	    // not random
	    dieValue= (rRan.nextInt(5)+1);
	    
      // blue player
	    drawable = getResources().getIdentifier(
          "blue_die_"+dieValue, "drawable", mContext.getPackageName());
      curUnit = new GameUnit(context, drawable, i+1, GameUnit.TEAM_BLUE);
      curUnit.setValue(dieValue);
      curUnit.setX(xPos);
      curUnit.setY(yPos);
      //mUnitMap.put(curUnit.getId(), curUnit);
      mUnitsTeamBlue.put(curUnit.getId(), curUnit);
      
      // red player
      drawable = getResources().getIdentifier(
          "red_die_"+dieValue, "drawable", mContext.getPackageName()); 
      curUnit = new GameUnit(context, drawable, i+1, GameUnit.TEAM_RED);
      curUnit.setValue(dieValue);
      curUnit.setX(xPos+xOffsetRed);
      curUnit.setY(yPos);
	    //mUnitMap.put(curUnit.getId(), curUnit);
	    mUnitsTeamRed.put(curUnit.getId(), curUnit);
	    xCnt++;
	  }
	  
	}
	
	private void initUi(Context context){
		  mNextPlayer = new GameUi(mContext, R.drawable.arrow_red);
		  mNextPlayer.setX(100);
		  mNextPlayer.setY(500);
		  mNextPlayer.setStateNormal();
		  
		  //mNextPlayer.setDrawableStateActive(mDrawableStateActive);
		  //mNextPlayer.setDrawableStateInactive(mDrawableStateInactive);
		  //mNextPlayer.setDrawableStateReady(mDrawableStateActive);
	 
		  //mUiControls.put(curUnit.getId(), NextPlayer);
		}
	
	public void pause(){
      mRunning = false;
      while(true){
    	try {
			mThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	break;
      }
      mThread = null;
	}
	
	public void resume(){
	  mRunning = true;
      mThread = new Thread(this);
	  mThread.start();
	}
	
	@Override
	public void run() {
	  while(mRunning){
      if(!mHolder.getSurface().isValid()){
      	continue;
      }
      Canvas canvas = mHolder.lockCanvas();
      
      //background
      canvas.drawRGB(mRed, 0, mBlue);
      //text
      Paint paint = new Paint();
      paint.setColor(Color.WHITE); 
      paint.setTextSize(30); 
      canvas.drawText("Turn: "+mTurn, 30, 30, paint);
      //level
      drawLevel(canvas);
      // units
      if(mTeam == GameUnit.TEAM_RED){
        drawPlayer(canvas, mUnitsTeamBlue);
        drawPlayer(canvas, mUnitsTeamRed);
      } else{
        drawPlayer(canvas, mUnitsTeamRed);
        drawPlayer(canvas, mUnitsTeamBlue);
      }
      // user interface
      drawUi(canvas);

//        if(mGameUnit.getCollision(mGameUnit2.getX(), mGameUnit2.getY(), mGameUnit2.getWidth(), mGameUnit2.getHeight())){
//          mBlue=0;
//        }
        
      mHolder.unlockCanvasAndPost(canvas);
	  }
		
	}
	
	private void drawLevel(Canvas canvas){

	  int gameTilesSize = mGameTileList.size();
	  for(int i=0;i<gameTilesSize;i++){
	    canvas.drawBitmap(mGameTileList.get(i).getBitmap(),mGameTileList.get(i).getX(),
	        mGameTileList.get(i).getY(), null);
	  }
	}
	
	private void drawPlayer(Canvas canvas, LinkedHashMap<Integer,GameUnit> hashMap){
	  synchronized (hashMap) {
      //for (GameUnit curUnit : mUnitMap.values()) {
      for (GameUnit curUnit : hashMap.values()) {
        //synchronized (curUnit){
          canvas.drawBitmap(curUnit.getBitmap(), curUnit.getX(),
              curUnit.getY(), null);
        //}
      }
	  }
	}
	
	private void drawUi(Canvas canvas){
	  canvas.drawBitmap(mNextPlayer.getBitmap(), mNextPlayer.getX(),
	      mNextPlayer.getY(), null);
	}
	
	
	private synchronized int findTouchedTile(LinkedHashMap<Integer,GameUnit> hashMap, int x, int y){
	  // get the touched unit if there isn't already one
    for (GameUnit curUnit : hashMap.values()) {
      if(curUnit.getImpact(x, y)){
        return curUnit.getId();
      }
    }
    return 0;
	}
	
	
	
  /* (non-Javadoc)
   * @see android.view.View#onTouchEvent(android.view.MotionEvent)
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
        
    int eventAction = event.getAction();
    switch(eventAction) {
      case MotionEvent.ACTION_DOWN:
        
        if (touchedUi(event)){
          return false;
        }
        
        //Log.e(LOG_TAG, "Action_Down");
        //Log.e(LOG_TAG, "mTouchedId: "+ mTouchedId);
        if(mTouchedId == 0){
          Log.e(LOG_TAG, "mTouchedId is null");
          Log.e(LOG_TAG, "Team is "+mTeam);
          if(mTeam == GameUnit.TEAM_RED){
            mTouchedId = findTouchedTile(mUnitsTeamRed, (int)event.getX(), (int)event.getY());
          }else{
            mTouchedId = findTouchedTile(mUnitsTeamBlue, (int)event.getX(), (int)event.getY());
          }
        }

        if(mTouchedId != 0){
          if(mTeam == GameUnit.TEAM_RED){
            //Log.e(LOG_TAG, "mTouchedId not null");
            synchronized (mUnitsTeamRed) {
              mUnitsTeamRed.get(mTouchedId).setCenterX((int)event.getX());
              mUnitsTeamRed.get(mTouchedId).setCenterY((int)event.getY());
            }
          }else{
            synchronized (mUnitsTeamBlue) {
              mUnitsTeamBlue.get(mTouchedId).setCenterX((int)event.getX());
              mUnitsTeamBlue.get(mTouchedId).setCenterY((int)event.getY());
            }
          }
        }
        //Log.e(LOG_TAG, "before break");
        break;
      case MotionEvent.ACTION_MOVE:
        if(mTouchedId != 0){
          if(mTeam == GameUnit.TEAM_RED){
            synchronized (mUnitsTeamRed) {
              //Log.e(LOG_TAG, "mTouchedId not null");
              mUnitsTeamRed.get(mTouchedId).setCenterX((int)event.getX());
              mUnitsTeamRed.get(mTouchedId).setCenterY((int)event.getY());
            }
          }else{
            synchronized (mUnitsTeamBlue) {
              mUnitsTeamBlue.get(mTouchedId).setCenterX((int)event.getX());
              mUnitsTeamBlue.get(mTouchedId).setCenterY((int)event.getY());
            }
          }
        }
        break;
      case MotionEvent.ACTION_UP:
        //Log.e(LOG_TAG, "Action_Up");
        if(mTouchedId != 0){
          mTouchedId = 0;
        }
        //printHashMap(mUnitsTeamBlue);
        //printHashMap(mUnitsTeamRed);
        break;
      }
    return true;
    //return super.onTouchEvent(event);
  }
  
  private boolean touchedUi(MotionEvent event){
    if (mNextPlayer.getImpact((int)event.getX(), (int)event.getY())){
      passTurntoNextPlayer(); // toggle
      return true;
    }
    return false;
  }
  
  private void passTurntoNextPlayer(){
    // toggle player
    if(mTeam == GameUnit.TEAM_BLUE){
      mTeam = GameUnit.TEAM_RED;
      mRed  = 100;
      mBlue = 0;
      mNextPlayer.setDrawableStateNormal(R.drawable.arrow_blue);
      //mNextPlayer.setDrawableStateActive(mDrawableStateActive);
      //mNextPlayer.setDrawableStateInactive(mDrawableStateInactive);
      //mNextPlayer.setDrawableStateReady(mDrawableStateActive);
    }else{
      mTeam = GameUnit.TEAM_BLUE;
      mRed  = 0;
      mBlue = 100;
      mNextPlayer.setDrawableStateNormal(R.drawable.arrow_red);
      //mNextPlayer.setDrawableStateActive(mDrawableStateActive);
      //mNextPlayer.setDrawableStateInactive(mDrawableStateInactive);
      //mNextPlayer.setDrawableStateReady(mDrawableStateActive);
      mTurn++;
    }
    mNextPlayer.setStateNormal();
  }
  
  
  
	private void printHashMap(LinkedHashMap<Integer,GameUnit> hashMap){  
	  Log.e(LOG_TAG, "============");
    Log.e(LOG_TAG, "==mUnitMap==");
    Log.e(LOG_TAG, "id|value|xPos|yPos");
    synchronized (hashMap) {
      for (GameUnit curUnit : hashMap.values()) {    
      Log.e(LOG_TAG, curUnit.getId()+"|"+curUnit.getValue()+"|"+curUnit.getX()+"|"+curUnit.getY());  
      }
    }
	  Log.e(LOG_TAG, "============");
  }
}
