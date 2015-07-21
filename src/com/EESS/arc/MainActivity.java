package com.EESS.arc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.EESS.arc.widgets.JoystickMovedListener;
import com.EESS.arc.widgets.JoystickView;

public class MainActivity extends Activity {
    public final String TAG = MainActivity.class.getSimpleName();
    final String[] speedStrings = { "High", "Med", "Low" };

    float speedModifier = (float) 0.8;

    CheckBox prefCheckBox;
    TextView prefEditText;
    TextView myListPref;
    Spinner speedSpinner;

    JoystickView joystick;
    int tiltDegrees = 500;
    int panDegrees = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	// SPINNER
	speedSpinner = (Spinner) findViewById(R.id.spinner_speed);

	Spinner spinner = (Spinner) findViewById(R.id.spinner_speed);

	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_spinner_item, speedStrings);

	dataAdapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	speedSpinner.setAdapter(dataAdapter);

	speedSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	    @Override
	    public void onItemSelected(AdapterView<?> parent, View view,
		    int position, long id) {
		switch (position) {
		case 0:
		    speedModifier = (float) 0.8;
		    break;
		case 1:
		    speedModifier = (float) 0.6;
		    break;
		case 2:
		    speedModifier = (float) 0.4;
		    break;
		}

	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	    }
	});

	myListPref = (TextView) findViewById(R.id.list_pref);

	joystick = (JoystickView) findViewById(R.id.joystickView);

	joystick.setOnJostickMovedListener(_listener);

	if (SetPreferenceActivity.theConnectedDevice != null) {
	    myListPref.setText(SetPreferenceActivity.theConnectedDevice);
	} else {
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
	 * Because it's onlt ONE option in the menu. In order to make it simple,
	 * We always start SetPreferenceActivity without checking.
	 */
	switch (item.getItemId()) {
	case R.id.action_settings:
	    Intent intent = new Intent();
	    intent.setClass(MainActivity.this, SetPreferenceActivity.class);
	    startActivityForResult(intent, 0);
	    return true;
	case R.id.action_takeoff:
	    if (SetPreferenceActivity.theConnectedDevice != null) {
		String s = "BTM+100+100";
		SetPreferenceActivity.myConnectedThread.write(s.getBytes());
	    }
	    return true;
	}
	return false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	Toast.makeText(getApplicationContext(), "Return", 0).show();
	if (SetPreferenceActivity.theConnectedDevice != null) {
	    myListPref.setText(SetPreferenceActivity.theConnectedDevice);

	} else {
	    myListPref.setText("No device");
	}
    }

    private JoystickMovedListener _listener = new JoystickMovedListener() {

	@Override
	public void OnMoved(int x, int y) {
	    Log.i(TAG, "x: " + x + " y: " + y);
	    if (y >= 0) {
		tiltDegrees = y * 50 + 500;
	    } else {
		tiltDegrees = 500 - (Math.abs(y) * 50);
	    }
	    if (x >= 0) {
		panDegrees = x * 50 + 500;
	    } else {
		panDegrees = 500 - (Math.abs(x) * 50);
	    }
	    Log.i(TAG, "panDegrees: " + panDegrees + " tiltDegrees: "
		    + tiltDegrees);

	    if (SetPreferenceActivity.theConnectedDevice != null) {
		String s_motorR, s_motorL;
		int xscale = (int) ((int) 5 * speedModifier);
		int yscale = (int) ((int) 7 * speedModifier);
		int new_y = -y * yscale;
		int motorR = new_y;
		int motorL = new_y;

		int xthreshold = 3;
		int ythreshold = 3;

		if (Math.abs(x) > xthreshold) {
		    if (-y < -ythreshold) {
			motorR += x * xscale;
			motorL -= x * xscale;
		    } else if (-y > ythreshold) {
			motorL += x * xscale;
			motorR -= x * xscale;
		    } else {
			motorL = (int) (x * xscale * 1.5);
			motorR = -(int) (x * xscale * 1.5);
		    }
		}

		if (motorL > 10 * yscale) {
		    motorL = 10 * yscale;
		}
		if (motorR > 10 * yscale) {
		    motorR = 10 * yscale;
		}
		if (motorR < -10 * yscale) {
		    motorR = -10 * yscale;
		}
		if (motorL < -10 * yscale) {
		    motorL = -10 * yscale;
		}

		if (motorR < 0) {
		    // Negative
		    s_motorR = "-";
		    motorR = -motorR;
		} else {
		    // Positive
		    s_motorR = "0";
		}

		if (motorR < 100) {
		    if (motorR < 10) {
			s_motorR += "00" + motorR;
		    } else {
			s_motorR += "0" + motorR;
		    }
		} else {
		    s_motorR += motorR;
		}

		if (motorL < 0) {
		    // Negative
		    s_motorL = "-";
		    motorL = -motorL;
		} else {
		    // Positive
		    s_motorL = "0";
		}

		if (motorL < 100) {
		    if (motorL < 10) {
			s_motorL += "00" + motorL;
		    } else {
			s_motorL += "0" + motorL;
		    }
		} else {
		    s_motorL += motorL;
		}

		String s = "BTM" + s_motorR + s_motorL;
		SetPreferenceActivity.myConnectedThread.write(s.getBytes());
	    }
	}

	@Override
	public void OnReleased() {
	}

	public void OnReturnedToCenter() {
	    if (SetPreferenceActivity.theConnectedDevice != null) {
		String s = "BTB";
		SetPreferenceActivity.myConnectedThread.write(s.getBytes());
	    }
	};
    };

}