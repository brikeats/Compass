package com.example.compass;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;


public class RotatableImageView extends ImageView {

	private final String TAG = "RotatableImageView"; 
			
	private RotatableImage image = new RotatableImage(getContext().getResources());
	private float imageAngle;
	float[] pivot;
	RectF imageRect, viewRect;
	private Matrix matrix = new Matrix();
	
	CompassNeedle needle;
	private float compassAngle; // [degrees]
	
	
	// constructors
	int bgColor = Color.rgb(255, 255, 255);
	public RotatableImageView (Context context) {
        super (context);
        setBackgroundColor(bgColor);
	}

	public RotatableImageView (Context context, AttributeSet attrs) {
        super (context, attrs);
        setBackgroundColor(bgColor);
	}
	public RotatableImageView (Context context, AttributeSet attrs, int style) {
        super (context, attrs, style);
        setBackgroundColor(bgColor);
	}
	
	public void setNeedle(CompassNeedle needle){this.needle = needle;}

	public void setImage (RotatableImage.ImageName imageName) {
		image.setImage(imageName);
		imageAngle = image.getImageAngle();
		imageRect = image.getCentralRect();
	} 
		
	public void setAngle(float newAngle){
		float angle = (float) (imageAngle - newAngle);
		if (angle > Math.PI) angle -= 2*Math.PI;
		if (angle < -Math.PI) angle += 2*Math.PI;
		
		// put through CompassNeedle to produce interesting dynamics
		compassAngle = (float) needle.update(angle);
	}

	
	@Override public void onDraw(Canvas canvas) {
	    if (image == null) return;
        if(viewRect == null) setInitialViewRect();

	    // Use the same Matrix over and over again to minimize allocation in onDraw.
	    matrix.reset();
	    matrix.setRectToRect(imageRect, viewRect, Matrix.ScaleToFit.FILL);
        matrix.postRotate((float) Math.toDegrees(compassAngle + Math.PI/2), viewRect.centerX(), viewRect.centerY());

        // Finally, draw the bitmap using the matrix
        canvas.drawBitmap (image.getBitmap(), matrix, null);
	}


    public void setInitialViewRect() {
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        viewRect = new RectF(0, (viewHeight-viewWidth)/2, viewWidth, (viewHeight+viewWidth)/2);
        Log.d(TAG, "setting view rect: (W, H) = "+viewWidth+", "+viewHeight);
    }
}
