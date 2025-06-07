package com.example.davide.constraintlayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createUI();
    }

    private void createUI(){
        ConstraintLayout mainLayout = new ConstraintLayout(this);
        mainLayout.setId(R.id.main_layout);
        mainLayout.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));

        Button testButton = new Button(this);
        testButton.setId(R.id.bttn_01);
        testButton.setBackgroundColor(getResources().getColor(R.color.colorYellow));
        testButton.setTextColor(getResources().getColor(R.color.colorRed));
        testButton.setText(R.string.bttn_01);
        mainLayout.addView(testButton);

        TextView chainTxt = new TextView(this);
        chainTxt.setId(R.id.txtChain_01);
        chainTxt.setText(R.string.txtChain_01);
        chainTxt.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        chainTxt.setTextColor(getResources().getColor(R.color.colorYellow));
        mainLayout.addView(chainTxt);

        TextView chainVTxt = new TextView(this);
        chainVTxt.setId(R.id.txtChainV_01);
        chainVTxt.setText(R.string.txtChainV_01);
        chainVTxt.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        chainVTxt.setTextColor(getResources().getColor(R.color.colorYellow));
        mainLayout.addView(chainVTxt);

        Button chain01 = new Button(this);
        chain01.setId(R.id.bttnChain_01);
        chain01.setText(R.string.bttnChain_01);
        chain01.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        chain01.setTextColor(getResources().getColor(R.color.colorRed));
        mainLayout.addView(chain01);

        Button chain02 = new Button(this);
        chain02.setId(R.id.bttnChain_02);
        chain02.setText(R.string.bttnChain_02);
        chain02.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        chain02.setTextColor(getResources().getColor(R.color.colorRed));
        mainLayout.addView(chain02);

        Button chain03 = new Button(this);
        chain03.setId(R.id.bttnChain_03);
        chain03.setText(R.string.bttnChain_03);
        chain03.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        chain03.setTextColor(getResources().getColor(R.color.colorRed));
        mainLayout.addView(chain03);

        Button chain04 = new Button(this);
        chain04.setId(R.id.bttnChain_04);
        chain04.setText(R.string.bttnChain_04);
        chain04.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        chain04.setTextColor(getResources().getColor(R.color.colorRed));
        mainLayout.addView(chain04);

        Button chain05 = new Button(this);
        chain05.setId(R.id.bttnChain_05);
        chain05.setText(R.string.bttnChain_05);
        chain05.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        chain05.setTextColor(getResources().getColor(R.color.colorRed));
        mainLayout.addView(chain05);

        Button chain06 = new Button(this);
        chain06.setId(R.id.bttnChain_06);
        chain06.setText(R.string.bttnChain_06);
        chain06.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        chain06.setTextColor(getResources().getColor(R.color.colorRed));
        mainLayout.addView(chain06);

        ConstraintSet cSet = getConstraint();
        cSet.applyTo(mainLayout);

        setContentView(mainLayout);
    }

    private ConstraintSet getConstraint(){
        ConstraintSet cSet = new ConstraintSet();

        cSet.constrainHeight(R.id.bttn_01, ConstraintSet.WRAP_CONTENT);
        cSet.constrainWidth(R.id.bttn_01, ConstraintSet.WRAP_CONTENT);
        cSet.constrainHeight(R.id.txtChain_01, ConstraintSet.WRAP_CONTENT);
        cSet.constrainHeight(R.id.txtChainV_01, ConstraintSet.WRAP_CONTENT);
        cSet.constrainWidth(R.id.txtChainV_01, ConstraintSet.WRAP_CONTENT);
        cSet.constrainHeight(R.id.bttnChain_01, ConstraintSet.WRAP_CONTENT);
        cSet.constrainWidth(R.id.bttnChain_01, ConstraintSet.WRAP_CONTENT);
        cSet.constrainHeight(R.id.bttnChain_02, ConstraintSet.WRAP_CONTENT);
        cSet.constrainWidth(R.id.bttnChain_02, ConstraintSet.WRAP_CONTENT);
        cSet.constrainHeight(R.id.bttnChain_03, ConstraintSet.WRAP_CONTENT);
        cSet.constrainWidth(R.id.bttnChain_03, ConstraintSet.WRAP_CONTENT);
        cSet.constrainHeight(R.id.bttnChain_04, ConstraintSet.WRAP_CONTENT);
        cSet.constrainWidth(R.id.bttnChain_04, ConstraintSet.WRAP_CONTENT);
        cSet.constrainHeight(R.id.bttnChain_05, ConstraintSet.WRAP_CONTENT);
        cSet.constrainWidth(R.id.bttnChain_05, ConstraintSet.WRAP_CONTENT);
        cSet.constrainHeight(R.id.bttnChain_06, ConstraintSet.WRAP_CONTENT);
        cSet.constrainWidth(R.id.bttnChain_06, ConstraintSet.WRAP_CONTENT);

        cSet.connect(R.id.bttn_01, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        cSet.connect(R.id.bttn_01, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        cSet.connect(R.id.bttn_01, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        cSet.connect(R.id.bttn_01, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        cSet.centerHorizontally(R.id.bttn_01, ConstraintSet.PARENT_ID);
        cSet.centerVertically(R.id.bttn_01, ConstraintSet.PARENT_ID);


        int marginPx = fromDpToPx(5);
        cSet.connect(R.id.txtChain_01, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        cSet.connect(R.id.txtChain_01, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        cSet.connect(R.id.txtChain_01, ConstraintSet.BOTTOM, R.id.bttnChain_01, ConstraintSet.TOP, marginPx);
        cSet.connect(R.id.txtChain_01, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

        cSet.connect(R.id.bttnChain_01, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        cSet.connect(R.id.bttnChain_01, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        cSet.connect(R.id.bttnChain_01, ConstraintSet.TOP, R.id.txtChain_01, ConstraintSet.BOTTOM, marginPx);

        cSet.connect(R.id.bttnChain_02, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        cSet.connect(R.id.bttnChain_02, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        cSet.connect(R.id.bttnChain_02, ConstraintSet.TOP, R.id.txtChain_01, ConstraintSet.BOTTOM, marginPx);

        cSet.connect(R.id.bttnChain_03, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        cSet.connect(R.id.bttnChain_03, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        cSet.connect(R.id.bttnChain_03, ConstraintSet.TOP, R.id.txtChain_01, ConstraintSet.BOTTOM, marginPx);

        cSet.connect(R.id.txtChainV_01, ConstraintSet.LEFT, R.id.bttnChain_01, ConstraintSet.LEFT);
        cSet.connect(R.id.txtChainV_01, ConstraintSet.RIGHT, R.id.bttnChain_01, ConstraintSet.RIGHT);
        cSet.connect(R.id.txtChainV_01, ConstraintSet.TOP, R.id.bttnChain_01, ConstraintSet.BOTTOM, marginPx);
        cSet.connect(R.id.txtChainV_01, ConstraintSet.BOTTOM, R.id.bttnChain_04, ConstraintSet.TOP, marginPx);


        marginPx = fromDpToPx(10);
        cSet.connect(R.id.bttnChain_04, ConstraintSet.LEFT, R.id.bttnChain_01, ConstraintSet.LEFT);
        cSet.connect(R.id.bttnChain_04, ConstraintSet.RIGHT, R.id.bttnChain_01, ConstraintSet.RIGHT);
        cSet.connect(R.id.bttnChain_04, ConstraintSet.TOP, R.id.bttnChain_01, ConstraintSet.BOTTOM, marginPx);

        cSet.connect(R.id.bttnChain_05, ConstraintSet.LEFT, R.id.bttnChain_04, ConstraintSet.LEFT);
        cSet.connect(R.id.bttnChain_05, ConstraintSet.RIGHT, R.id.bttnChain_04, ConstraintSet.RIGHT);

        cSet.connect(R.id.bttnChain_06, ConstraintSet.LEFT, R.id.bttnChain_05, ConstraintSet.LEFT);
        cSet.connect(R.id.bttnChain_06, ConstraintSet.RIGHT, R.id.bttnChain_05, ConstraintSet.RIGHT);

        cSet.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, new int[] {R.id.bttnChain_01, R.id.bttnChain_03}, null, ConstraintSet.CHAIN_SPREAD );
        cSet.addToHorizontalChain(R.id.bttnChain_02, R.id.bttnChain_01, R.id.bttnChain_03);

        cSet.createVerticalChain(R.id.bttnChain_01, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, new int[]{R.id.bttnChain_04, R.id.bttnChain_06}, null, ConstraintSet.CHAIN_SPREAD);
        cSet.addToVerticalChain(R.id.bttnChain_05, R.id.bttnChain_04, R.id.bttnChain_06);

        return cSet;
    }

    private int fromDpToPx(int nPx){
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, nPx, r.getDisplayMetrics() );
        return px;
    }

    }