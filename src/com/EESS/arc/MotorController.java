package com.EESS.arc;

public class MotorController {

    private String btCommand;
    private int motorL;
    private int motorR;

    public MotorController(int x, int y, float scaling) {
	// Recallibrate
	y = -y;

	int xthreshold = 3;
	int ythreshold = 3;

	float generalScaling = scaling;

	float xscale = 4 * generalScaling;
	float yscale = 7 * generalScaling;

	float xthresholdScaling = 2 * generalScaling;
	float ythresholdScaling = 4 * generalScaling;

	float motorR = y * yscale;
	float motorL = y * yscale;

	if (Math.abs(y) > ythreshold) {
	    // If above x threshold
	    if (Math.abs(x) > xthreshold) {
		// If on left side
		if (x < 0) {
		    x = Math.abs(x);
		    // Quadrant 1 (top left)
		    if (y > 0) {
			motorR += x * ythresholdScaling;
			motorL -= x * ythresholdScaling;
		    }
		    // Quadrant 2 (bottom left)
		    if (y < 0) {
			motorR -= x * ythresholdScaling;
			motorL += x * ythresholdScaling;
		    }
		} else if (x > 0) {
		    // Quadrant 3 (top right)
		    if (y > 0) {
			motorR -= x * ythresholdScaling;
			motorL += x * ythresholdScaling;
		    }
		    // Quadrant 4 (bottom right)
		    if (y < 0) {
			motorR += x * ythresholdScaling;
			motorL -= x * ythresholdScaling;
		    }
		}
	    }

	} else {
	    if (x < 0) {
		// x threshold 1 (left)
		motorR = Math.abs(x * xthresholdScaling);
		motorL = -Math.abs(x * xthresholdScaling);
	    } else if (x > 0) {
		// x threshold 2 (right)
		motorR = -(x * xthresholdScaling);
		motorL = (x * xthresholdScaling);
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
