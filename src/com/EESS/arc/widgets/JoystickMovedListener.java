package com.EESS.arc.widgets;

public interface JoystickMovedListener {
    public void OnMoved(int pan, int tilt);
    public void OnReleased();
    public void OnReturnedToCenter();
}
