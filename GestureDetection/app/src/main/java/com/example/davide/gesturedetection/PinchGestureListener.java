package com.example.davide.gesturedetection;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.TextView;

public class PinchGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    TextView setText;
    String oldText;

    public PinchGestureListener(TextView setText){
        this.setText = setText;
        this.oldText = setText.getText().toString();
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector){
        float scale = detector.getScaleFactor();
        String newText;
        if( scale > 1 ){
            newText = this.oldText + " Zooming Out";
        } else {
            newText = this.oldText + " Zooming In";
        }
        this.setText.setText(newText);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector){
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector){
        detector.getCurrentSpan();
    }

}
