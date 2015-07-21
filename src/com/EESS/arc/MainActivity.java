package com.EESS.arc;

import com.EESS.arc.widgets.JoystickMovedListener;
import com.EESS.arc.widgets.JoystickView;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {
	public final String TAG = MainActivity.class.getSimpleName();
	 CheckBox prefCheckBox;
	 TextView prefEditText;
	 TextView myListPref;
	 
		TextView txtX, txtY;
		JoystickView joystick;
		int tiltDegrees = 500;
		int panDegrees = 500;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myListPref = (TextView) findViewById(R.id.list_pref);
		
		txtX = (TextView) findViewById(R.id.TextViewX);
		txtY = (TextView) findViewById(R.id.TextViewY);
		joystick = (JoystickView) findViewById(R.id.joystickView);
		
		joystick.setOnJostickMovedListener(_listener);
		
		 if(SetPreferenceActivity.theConnectedDevice!=null){
			 myListPref.setText(SetPreferenceActivity.theConnectedDevice);
		 }else{
			 myListPref.setText("No device");
		 }
		
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

 @Override
 public boolean onOptionsItemSelected(MenuItem item) {

  /*
   * Because it's onlt ONE option in the menu.
   * In order to make it simple, We always start SetPreferenceActivity
   * without checking.
   */
  
  Intent intent = new Intent();
        intent.setClass(MainActivity.this, SetPreferenceActivity.class);
        startActivityForResult(intent, 0); 
        return true;
 }
 
 @Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 Toast.makeText(getApplicationContext(), "Return", 0).show();
	 if(SetPreferenceActivity.theConnectedDevice!=null){
		 myListPref.setText(SetPreferenceActivity.theConnectedDevice); 
		 
	 }else{
		 myListPref.setText("No device");
	 }
 }
 
 private JoystickMovedListener _listener = new JoystickMovedListener() {

		@Override
		public void OnMoved(int x, int y) {
			Log.i(TAG, "x: " + x + " y: " + y);
			txtX.setText(Integer.toString(x));
			txtY.setText(Integer.toString(y));
			if (y >= 0) { 
				tiltDegrees = y * 50 + 500;
			} else {
				tiltDegrees = 500 - (Math.abs(y) *50); 
			}
			if (x >= 0) { 
				panDegrees = x * 50 + 500;
			} else {
				panDegrees = 500 - (Math.abs(x) *50); 
			}
			Log.i(TAG, "panDegrees: " + panDegrees + " tiltDegrees: " + tiltDegrees);

			if(SetPreferenceActivity.theConnectedDevice!=null){
					String s_motorR,s_motorL;
					int xscale = 5;
					int yscale = 7;
					int new_y = -y*yscale;
					int motorR = new_y;
					int motorL = new_y;
					
					int xthreshold = 3;
					int ythreshold = 3;
					
					if(Math.abs(x)>xthreshold){
						if(-y<-ythreshold){
							motorR += x*xscale;
							motorL -= x*xscale;
						}else if(-y>ythreshold){
							motorL += x*xscale;
							motorR -= x*xscale;
						}else{
							motorL = (int)(x*xscale*1.5);
							motorR = -(int)(x*xscale*1.5);
						}
					}
					
					if(motorL > 10*yscale){
						motorL = 10*yscale;
					}
					if(motorR > 10*yscale){
						motorR = 10*yscale;
					}
					if(motorR < -10*yscale){
						motorR = -10*yscale;
					}
					if(motorL < -10*yscale){
						motorL = -10*yscale;
					}
					
					if(motorR<0){
						//Negative
						s_motorR = "-";
						motorR = -motorR;
					}else{
						//Positive
						s_motorR = "0";
					}

					if(motorR<100){
						if(motorR<10){
							s_motorR += "00" + motorR;
						}else{
							s_motorR += "0" + motorR;
						}
					}else{
						s_motorR += motorR;
					}
					
					if(motorL<0){
						//Negative
						s_motorL = "-";
						motorL = -motorL;
					}else{
						//Positive
						s_motorL = "0";
					}

					if(motorL<100){
						if(motorL<10){
							s_motorL += "00" + motorL;
						}else{
							s_motorL += "0" + motorL;
						}
					}else{
						s_motorL += motorL;
					}
					
					
					String s = "BTM" + s_motorR + s_motorL;
					SetPreferenceActivity.myConnectedThread.write(s.getBytes());
			}
		}

		@Override
		public void OnReleased() {
			txtX.setText("released");
			txtY.setText("released");
		}

		public void OnReturnedToCenter() {
			if(SetPreferenceActivity.theConnectedDevice!=null){
				String s = "BTB";
				SetPreferenceActivity.myConnectedThread.write(s.getBytes());
			}
			txtX.setText("stopped");
			txtY.setText("stopped");
		};
	};

 
}