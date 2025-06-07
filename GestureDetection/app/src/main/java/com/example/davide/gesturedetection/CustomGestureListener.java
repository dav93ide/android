package com.example.davide.gesturedetection;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomGestureListener implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary gLib;
    TextView setText;
    String oldText;

    public CustomGestureListener(Context context, TextView setText){
        this.setText = setText;
        this.oldText = setText.getText().toString();
        gestureSetup(context);
    }

    private void gestureSetup(Context context){
        this.gLib = GestureLibraries.fromRawResource(context, R.raw.gestures);
        if(!this.gLib.load()){
            String newText = this.oldText + " Errore recupero GestureLibrary! ";
        }
    }

    @Override
    public void onGesturePerformed(GestureOverlayView gOV, Gesture gesture){
        ArrayList<Prediction> predictions = gLib.recognize(gesture);
        if( predictions.size() > 0 && predictions.get(0).score > 1.00 ){
            String action = predictions.get(0).name;
            String newText = this.oldText + action;
            this.setText.setText(newText);
        }
    }

}
