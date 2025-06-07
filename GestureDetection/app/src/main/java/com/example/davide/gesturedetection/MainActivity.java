package com.example.davide.gesturedetection;

import android.gesture.GestureOverlayView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    ScaleGestureDetector scaleDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioButton rB1 = (RadioButton) findViewById(R.id.radioG1_1);
        RadioButton rB2 = (RadioButton) findViewById(R.id.radioG1_2);
        RadioButton rB3 = (RadioButton) findViewById(R.id.radioG1_3);

        rB1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCommonGestureListener(isChecked);
            }
        });

        rB2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCustomGestureListener(isChecked);
            }
        });

        rB3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPinchGestureListener(isChecked);
            }
        });

    }

    private void setCommonGestureListener(Boolean isChecked){
        if( isChecked ) {
            LinearLayout ll = (LinearLayout) findViewById(R.id.gestureLinearL);
            ll.removeAllViews();
            TextView setText = (TextView) findViewById(R.id.doneGestureText);
            setText.setText(R.string.doneGesture);
            final CommonGestureListener gL = new CommonGestureListener(this, setText);
            View.OnTouchListener tListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent mE) {
                    return gL.onTouchEvent(mE);
                }
            };
            ll.setOnTouchListener(tListener);
        }
    }

    private void setCustomGestureListener(Boolean isChecked){
        if( isChecked ){
            LinearLayout ll = (LinearLayout) findViewById(R.id.gestureLinearL);
            ll.removeAllViews();
            ll.setOnTouchListener(null);
            TextView setText = (TextView) findViewById(R.id.doneGestureText);
            setText.setText(R.string.doneGesture);
            CustomGestureListener cGL = new CustomGestureListener(this, setText);
            GestureOverlayView gOV = new GestureOverlayView(this);

            ViewGroup.LayoutParams lP = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            gOV.setLayoutParams(lP);

            gOV.setId(R.id.gOverlay);
            gOV.setGestureColor(getResources().getColor(R.color.colorRed));
            gOV.setUncertainGestureColor(getResources().getColor(R.color.colorBlack));
            gOV.addOnGesturePerformedListener(cGL);
            ll.addView(gOV);
        }
    }

    private void setPinchGestureListener(Boolean isChecked){
        if(isChecked){
            LinearLayout ll = (LinearLayout) findViewById(R.id.gestureLinearL);
            ll.removeAllViews();
            ll.setOnTouchListener(null);
            TextView setText = (TextView) findViewById(R.id.doneGestureText);
            setText.setText(R.string.doneGesture);
            PinchGestureListener pGL = new PinchGestureListener(setText);
            scaleDetector = new ScaleGestureDetector(ll.getContext(),pGL);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent mE){
        return scaleDetector.onTouchEvent(mE);
    }

}
