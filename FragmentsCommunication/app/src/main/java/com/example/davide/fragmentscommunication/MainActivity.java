package com.example.davide.fragmentscommunication;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements FragmentValueCommunication {

    private String lastValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.lastValue = "None!";
        Button bttnFrag1 = (Button) findViewById(R.id.bttnFragment_1);
        Button bttnFrag2 = (Button) findViewById(R.id.bttnFragment_2);
        Button bttnSet = (Button) findViewById(R.id.bttnGetValue_1);


        bttnFrag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragOne();
            }
        });

        bttnFrag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragTwo();
            }
        });

        bttnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText();
            }
        });

    }

    private void setFragOne(){
        FragmentOne fO = new FragmentOne(this.lastValue);
        FragmentManager fM = getFragmentManager();
        FragmentTransaction fT = fM.beginTransaction();
        fT.replace(R.id.frameLayout, fO);
        fT.commit();
    }

    private void setFragTwo(){
        FragmentTwo fTwo = new FragmentTwo(this.lastValue);
        FragmentManager fM = getFragmentManager();
        FragmentTransaction fT = fM.beginTransaction();
        fT.replace(R.id.frameLayout, fTwo);
        fT.commit();
    }

    private void setText(){
        TextView txtV = (TextView) findViewById(R.id.mainActivityText);
        txtV.setText(new String(getResources().getString(R.string.mainActText) + "   " + this.lastValue));
    }

    @Override
    public void onSetValue(String value){
        this.lastValue = value;
    }

    public String getLastValue(){
        return this.lastValue;
    }

}
