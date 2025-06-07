package com.example.davide.gesturedetection;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

public class CommonGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    GestureDetectorCompat gDC;
    TextView setText;
    String originalText;

    public CommonGestureListener(Context context, TextView setText){
        this.gDC = new GestureDetectorCompat(context, this);
        this.setText = setText;
        this.originalText = setText.getText().toString();
    }

    public boolean onTouchEvent(MotionEvent event){
        return this.gDC.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        String newText = this.originalText + " onSingleTapConfirmed";
        this.setText.setText(newText);
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        String newText = this.originalText + " onDoubleTap";
        this.setText.setText(newText);
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        String newText = this.originalText + " onDoubleTapEvent";
        this.setText.setText(newText);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        String newText = this.originalText + " onDown";
        this.setText.setText(newText);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        String newText = this.originalText + " onShowPress";
        this.setText.setText(newText);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        String newText = this.originalText + " onSingleTapUp";
        this.setText.setText(newText);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        String newText = this.originalText + " onScroll";
        this.setText.setText(newText);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        String newText = this.originalText + " onLongPress";
        this.setText.setText(newText);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        String newText = this.originalText + " onFling";
        this.setText.setText(newText);
        return true;
    }
}
