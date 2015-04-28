package com.example.compass;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.example.compass.R;


public class RotatableImage{

	private static final String TAG = "RotatableImage";
	
	public enum ImageName{
		COMPASS, GOD, ARROW, FINGER
	}

	private Bitmap bitmap;
	private float imageAngle;
	private float imageScale = 1f;
	float bmWidth0, bmHeight0;
	float pivotX, pivotY;
	private Resources resources;
	RectF bmRect;
	
	RotatableImage(Resources resources){
		this.resources = resources;
		setImage(ImageName.GOD);
	}
	
	// TODO: this stuff should be in an xml file
	// NB: BitmapFactory automatically scales depending on the device.
	//     Thus, we need to hard-code the initial image size (to determine 
	//     the pivot on the RotatableImageView).
	public void setImage(ImageName imageName){
		switch (imageName){
		case GOD:
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.god);
			imageAngle = (float)Math.toRadians(100);
			imageScale = 1f;
			pivotX = 332; pivotY = 140;
			bmWidth0 = 825; bmHeight0 = 300;
			break;
		case FINGER:
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.pointing_hand);
			imageAngle = (float)Math.toRadians(-80);
			imageScale = 1f;
			pivotX = 340; pivotY = 300;
			bmWidth0 = 635; bmHeight0 = 635;
			break;
		// TODO: the arrow doesn't look good because the shadow doesn't change with the rotation
		case ARROW:
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrow);
			imageAngle = (float)Math.toRadians(-90);
			imageScale = 1f;
			pivotX = 400; pivotY = 727/2;
			bmWidth0 = 727; bmHeight0 = 727;
			break;
		//case COMPASS: // TODO
			//break;
		default:
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.god_dot);
			imageAngle = (float)Math.toRadians(100);
			pivotX = 332; pivotY = 140;
			bmWidth0 = 825; bmHeight0 = 300;
			break;
		}
	}
	
		
	public RectF getCentralRect(){
		float bmWidth = bitmap.getWidth(); 
		float bmHeight = bitmap.getHeight();
		Log.d(TAG, "bm size "+bmWidth+"X"+bmHeight);
	    
		float[] pivotPoint = new float[2];
		pivotX *= bmWidth/bmWidth0;
		pivotY *= bmHeight/bmHeight0;	    
	    Log.d(TAG, "pivot "+pivotPoint[0]+","+pivotPoint[1]);
	    
	    return new RectF(pivotX-0.5f*bmHeight, pivotY-0.5f*bmHeight, pivotX + 0.5f*bmHeight, pivotY+0.5f*bmHeight);    
	}
	
	public float getScale(){return imageScale;}
	public float getImageAngle(){return imageAngle;}
	public Bitmap getBitmap(){return bitmap;}
	
}
