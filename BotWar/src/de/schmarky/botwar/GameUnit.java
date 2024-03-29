package de.schmarky.botwar;

import android.content.Context;
import android.graphics.Rect;

/**
 * An extension of GameImage, GameUnit represents a playable or non-playable
 * unit in the game. 
 * 
 * In addition to the GameImage properties, game units include a unique ID,
 * based on a static count property. This is useful when creating, and later
 * modifying, multiple units.
 * 
 * extended by Mark Pommerening (schmarky.de)
 * - added value attribute
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class GameUnit extends GameImage
{
	private int id;
	private static int count = 1; 
  private int mValue; // die value 1-6  
  private int mTeam; // is the die currently touched
  
  public static final int TEAM_BLUE = 1;
  public static final int TEAM_RED  = 2;
  
  public GameUnit(Context context, int drawable, int value, int team)
	{
		super(context, drawable);
    
		mValue=value;
		mTeam=team;
    id=count;
		count++;
	}
	
	public Rect getRect()
	{
		Rect rect = new Rect((int)mX, (int)mY, ((int)mX + this.getWidth()), ((int)mY + this.getHeight()));
		return rect;
	}
	
	public boolean getCollision(int x, int y, int width, int height)
	{
		Rect rect = new Rect((int)x, (int)y, ((int)x + width), ((int)y + height));
		return (rect.intersects((int)mX, (int)mY, ((int)mX + getWidth()), ((int)mY + getHeight())));
	}
	
	public boolean getImpact(int x, int y)
	{
		if ((x >= mX) && (x <= (mX + this.getWidth())))
		{
			if ((y >= mY) && (y <= (mY + this.getHeight())))
			{
				return true;
			}
		}

		return false;
	}

	public static int getCount()
	{
		return count;
	}

	public static void resetCount()
	{
		count = 1;
	}

	public int getId()
	{
		return id;
	}
	
	 /**
   * @return the value of the die
   */
  public int getValue() {
    return mValue;
  }

  /**
   * @param mValue the die value to set
   */
  public void setValue(int mValue) {
    this.mValue = mValue;
  }
	  
}
