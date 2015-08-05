package com.EESS.arc;

public class MotorController {

    private String btCommand;
    private int motorL;
    private int motorR;

    public MotorController(int x, int y, float scaling) {
	// Recallibrate
	y = -y;

	// The threshold at which the perpedicular axis will take effect
	int xthreshold = 3;
	int ythreshold = 3;

	float generalScaling = scaling;

	// Scaling for when car is in reverse direction
	float reverseScaling = (float) 0.5;

	// Scale -10 to 10 up to motor speeds
	// Different for each axis (fine tuning)
	float xscale = 4 * generalScaling;
	float yscale = 7 * generalScaling;

	// The scaling for when the joystick is in far left and far right only
	float xthresholdScaling = generalScaling;

	// Scale motor speeds based on y axis
	// Then change each motor based on x component
	float motorR = y * yscale;
	float motorL = y * yscale;

	// Initial speed point for pure x axis movement
	int initialPivotSpeed = 9;

	// Scale motors if in reverse
	if (y < 0) {
	    motorR = motorR * reverseScaling;
	    motorL = motorL * reverseScaling;
	}

	if (Math.abs(y) > ythreshold) {
	    // If above x threshold
	    if (Math.abs(x) > xthreshold) {
		// If on left side
		if (x < 0) {
		    x = Math.abs(x);
		    // Quadrant 1 (top left)
		    if (y > 0) {
			motorR += x * xscale;
			motorL -= x * xscale;
		    }
		    // Quadrant 2 (bottom left)
		    if (y < 0) {
			motorR -= x * xscale * reverseScaling;
			motorL += x * xscale * reverseScaling;
		    }
		} else if (x > 0) {
		    // Quadrant 3 (top right)
		    if (y > 0) {
			motorR -= x * xscale;
			motorL += x * xscale;
		    }
		    // Quadrant 4 (bottom right)
		    if (y < 0) {
			motorR += x * xscale * reverseScaling;
			motorL -= x * xscale * reverseScaling;
		    }
		}
	    }

	} else {
	    if (x < 0) {
		// x threshold 1 (left)
		motorR = Math.abs(x * xthresholdScaling) + initialPivotSpeed;
		motorL = -(Math.abs(x * xthresholdScaling) + initialPivotSpeed);
	    } else if (x > 0) {
		// x threshold 2 (right)
		motorR = -(Math.abs(x * xthresholdScaling) + initialPivotSpeed);
		motorL = Math.abs(x * xthresholdScaling) + initialPivotSpeed;
	    }
	}

	// Make sure motors dont go too fast
	if (motorL > 10 * yscale) {
	    motorL = 10 * yscale;
	}
	if (motorR > 10 * yscale) {
	    motorR = 10 * yscale;
	}

	// make sure motors dont go too fast in the reverse direction.
	if (motorR < -10 * yscale) {
	    motorR = -10 * yscale;
	}
	if (motorL < -10 * yscale) {
	    motorL = -10 * yscale;
	}

	String s_motorR = convertToString((int) motorR);
	String s_motorL = convertToString((int) motorL);

	btCommand = "BTM" + s_motorR + s_motorL;
    }

    public String getMotorCommand() {
	return btCommand;
    }

    private String convertToString(int speed) {
	String motorString;
	if (speed < 0) {
	    // Negative
	    motorString = "-";
	    speed = -speed;
	} else {
	    // Positive
	    motorString = "+";
	}

	if (speed < 100) {
	    if (speed < 10) {
		motorString += "00" + speed;
	    } else {
		motorString += "0" + speed;
	    }
	} else {
	    motorString += speed;
	}
	return motorString;
    }
}
