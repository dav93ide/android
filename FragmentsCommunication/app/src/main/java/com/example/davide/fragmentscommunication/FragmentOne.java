package com.example.davide.fragmentscommunication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentOne extends Fragment implements FragmentValueCommunication {

    String actualValue;
    FragmentValueCommunication activityCallback;

    public FragmentOne(){

    }

    @SuppressLint("ValidFragment")
    public FragmentOne(String actualValue){
        this.actualValue = actualValue;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        Button setBttn = (Button) view.findViewById(R.id.bttnSetFrag_1);
        final EditText eT = (EditText) view.findViewById(R.id.txtSetFrag_1);
        eT.setHint("Attuale: " + this.actualValue);
        setBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String insert = eT.getText().toString();
                onSetValue(insert);
            }
        });

        Button getBttn = (Button) view.findViewById(R.id.bttnGetFrag_1);
        getBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "Valore Attuale: " + getLastValue();
                Toast.makeText(v.getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public String getLastValue(){
        return this.activityCallback.getLastValue();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            this.activityCallback = (FragmentValueCommunication) activity;
        }catch(ClassCastException cCE ){
            Log.e("onAttach_FragmentOne", "ClassCastException (no implement 'FragmentValueCommunication'): " + cCE.getMessage() );
        }
    }

    @Override
    public void onSetValue(String text){
        this.activityCallback.onSetValue(text);
    }

}
