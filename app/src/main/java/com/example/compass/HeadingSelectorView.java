package com.example.compass;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class HeadingSelectorView extends View {
	
	public enum Heading {
	    NORTH, NORTHEAST, EAST, SOUTHEAST,
	    SOUTH, SOUTHWEST, WEST, NORTHWEST
	}
	
	private static final String TAG = "HeadingSelectorView";
	
	private Heading heading = Heading.NORTH;
	private int w, h, bigR, littleR, cx, cy;
	private Paint paint = new Paint();
	RectF boundingRect;
	float buttonGap, buttonWidth;
	MainActivity parent;

	public void setHeading(Heading newHeading){heading = newHeading;}
	public Heading getHeading(){return heading;}

	public HeadingSelectorView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    parent = (MainActivity)context;
	    paint.setAntiAlias(true);
	    paint.setStyle(Paint.Style.FILL);    
	    setBackgroundColor(Color.WHITE);
        this.onWindowFocusChanged(true);
	}
  
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		w = getWidth();
		h = getHeight();
		bigR = Math.min(w, h)/2;
		littleR = (int)(0.5*bigR);
		cx = w/2; cy = h/2; 
		buttonGap = 10f; // gap between buttons in degrees
		buttonWidth = 45 - buttonGap/2;
		boundingRect = new RectF(cx-bigR, cy-bigR, cx+bigR, cy+bigR);
		
		
		double headingAngle = -Math.toDegrees(parent.getHeadingAngle());
	    heading = degreesToHeading((int)headingAngle - 90);
	    Log.d("CompassMainActivity","parent heading "+headingAngle+", heading "+headingToString(heading));
	    
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		paint.setTextSize((bigR-littleR)/2);
		float theta0, rText = (bigR+littleR)/2;
		
		// draw wedges
		paint.setColor(Color.GRAY);
		for(float theta=0f; theta<360; theta+=45){
			theta0 = theta - buttonWidth/2;
			canvas.drawArc (boundingRect, theta0, buttonWidth, true, paint);
		}
		  
		// draw the highlighted wedge
		paint.setColor(Color.RED);
		theta0 = headingToDegrees(heading) - buttonWidth/2;
		canvas.drawArc (boundingRect, theta0, buttonWidth, true, paint);
		
		// draw text
		paint.setColor(Color.WHITE);
		for(float theta=-180f; theta<180; theta+=45){
			String headingStr = headingToString(degreesToHeading(theta));
			Log.d(TAG,"theta = "+theta+" headingStr = "+headingStr);
			Rect bounds = new Rect();
			paint.getTextBounds(headingStr, 0, 1, bounds);
			float x = cx + (float) (rText*Math.cos(theta*Math.PI/180)) - paint.measureText(headingStr)/2;
			float y = cy + (float) (rText*Math.sin(theta*Math.PI/180)) + bounds.height()/2;
			//Log.d(TAG,"Writing "+headingToString(headingThisButton)+" to "+x+","+y);
			canvas.drawText(headingStr, x,y, paint);
		}
					
		// draw inner circle
		paint.setColor(Color.WHITE);
		canvas.drawCircle(cx, cy, littleR, paint);
		
	}
	
		
	public static int headingToDegrees(Heading heading){
		switch(heading){
		case NORTH:
			return -90;
		case NORTHEAST:
			return -45;
		case EAST:
			return 0;
		case SOUTHEAST:
			return 45;
		case SOUTH:
			return 90;
		case SOUTHWEST:
			return 135;
		case WEST:
			return 180;
		case NORTHWEST:
			return -135;
		default:
			return -90;
		}
	}
	
	public static String headingToString(Heading heading){
		switch(heading){
		case NORTH:
			return "N";
		case NORTHEAST:
			return "NE";
		case EAST:
			return "E";
		case SOUTHEAST:
			return "SE";
		case SOUTH:
			return "S";
		case SOUTHWEST:
			return "SW";
		case WEST:
			return "W";
		case NORTHWEST:
			return "NW";
		default:
			return "N";
		}
	}

	public static Heading degreesToHeading(double headingDeg){
		// argument should be -180...180
		int roundedHeading = (int)(45*Math.round(headingDeg/45));
		roundedHeading = roundedHeading%360;
		switch(roundedHeading){
		case -90:
			return Heading.NORTH;
		case -45:
			return Heading.NORTHEAST;
		case 0:
			return Heading.EAST;
		case 45:
			return Heading.SOUTHEAST;
		case 90:
			return Heading.SOUTH;
		case 135:
			return Heading.SOUTHWEST;
		case 180:
			return Heading.WEST;
		case -180:
			return Heading.WEST;
		case -135:
			return Heading.NORTHWEST;
		default:
			return Heading.NORTH;
		}
	}
	
	
	// TODO: discount touches on gaps?
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	float dx = event.getX() - cx;
	float dy = event.getY() - cy;
	double r = Math.sqrt(dx*dx + dy*dy);
	double theta = Math.atan2(dy, dx);
	
	switch (event.getAction()) {
	    
		case MotionEvent.ACTION_DOWN:
			if (r <= bigR && r >= littleR){
				heading = degreesToHeading(theta*180/Math.PI);
				//Log.d("CompassMainActivity", "heading "+headingToString(heading));
				parent.setHeadingAngle(Math.toRadians(headingToDegrees(heading)+90));
			}
			break;
	    default:
	    	return false;
	    }

	    // Schedules a repaint.
	    invalidate();
	    return true;
	}
  
  
} 
