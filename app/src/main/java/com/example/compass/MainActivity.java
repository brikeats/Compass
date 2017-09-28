package com.example.compass;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;



public class MainActivity extends Activity implements SensorEventListener {

	private static final String TAG = "CompassMainActivity";
	private static final int RESULT_SETTINGS = 1;
	
	static final String defaultImage = "god";
	static final String defaultDynamics = "Springy";
	static final int defaultNeedleMass = 100;
	static final double defaultNeedleDrag = 1.1;
	static final double defaultTimestep = 2.5;

	double headingAngle = Math.toRadians(0);
	double possibleHeadingAngle;
	AlertDialog.Builder builder;
	RotatableImageView compassView;
	Bitmap bitmap;	
	private SensorManager sensorManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        compassView = (RotatableImageView)findViewById(R.id.compassView);
		compassView.setImage(RotatableImage.ImageName.GOD);
		compassView.setNeedle(new CompassNeedle(CompassNeedle.Dynamics.WOBBLY));
		compassView.invalidate();

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		setStateFromPreferences();
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_change_settings:
	            runSettingsActivity();
	            return true;
	        case R.id.action_set_heading:
	        	showSelectHeadingDialog();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void setHeadingAngle(double headingAngleRads){possibleHeadingAngle = -headingAngleRads;}
	
	public double getHeadingAngle(){return headingAngle;}
	
	
	protected void showSelectHeadingDialog(){ 
		builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.set_heading_dialog_title);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	headingAngle = possibleHeadingAngle;
            	Log.d(TAG,"heading "+headingAngle);
            }
        });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {}
	       });
		LayoutInflater inflater = getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.heading_dialog, null));
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private void runSettingsActivity(){
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivityForResult(intent, RESULT_SETTINGS);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); 
        setStateFromPreferences();       
    }
    
    
    protected void setStateFromPreferences(){
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String imageName = sharedPrefs.getString("pref_background_image", defaultImage);
        setBackgroundImage(imageName);
        
        int needleMass = sharedPrefs.getInt("pref_needle_mass", defaultNeedleMass);
        int needleDrag = sharedPrefs.getInt("pref_needle_drag", (int)(10*defaultNeedleDrag));
        int timeStep = sharedPrefs.getInt("pref_timestep", (int)(10*defaultTimestep));  
        
        // TODO: this divide- and multiply-by-ten stuff is pretty ugly,
        // but appear necessary, since seekbars only support integers
        String dynamicsStr = sharedPrefs.getString("pref_dynamics", defaultDynamics);
        CompassNeedle needle = new CompassNeedle(needleMass,(double)needleDrag/10, (double)timeStep/10);
        needle.setDynamics(dynamicsStr);
        compassView.setNeedle(needle);
    }
    
    // selects appropriate image based on 'imageName'
    // TODO: the image names should be from res/strings.xml
	private void setBackgroundImage(String imageStr){
		RotatableImage.ImageName imageName = RotatableImage.ImageName.GOD;
		switch (imageStr){
		case "God":
			imageName = RotatableImage.ImageName.GOD; break;
		case "Finger":
			imageName = RotatableImage.ImageName.FINGER; break;
		case "Arrow":
			imageName = RotatableImage.ImageName.ARROW; break;
		}
		compassView.setImage(imageName);
		compassView.invalidate();
	}
	
	
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    
	    compassView.setAngle(0);
	    compassView.invalidate();
    }
		
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		float[] rotVec = new float[3];
		rotVec[0] = event.values[0];
		rotVec[1] = event.values[1];
		rotVec[2] = event.values[2];
		
		float[] R = new float[9];
		SensorManager.getRotationMatrixFromVector (R, rotVec);
		
		float[] angles = new float[3];
		SensorManager.getOrientation (R, angles);
		
		compassView.setAngle((float) (angles[0] + headingAngle));
		compassView.invalidate();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR ),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop the listener to save battery
        sensorManager.unregisterListener(this);
    }

}
