package com.example.davide.progressdialog;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.ProgressDialog;
import android.os.CountDownTimer;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        TextView textConfirm = (TextView) findViewById(R.id.textConfirm);
        textConfirm.setVisibility(View.INVISIBLE);

        Button progBttn = this.findViewById(R.id.progBttn);
        progBttn.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v)
            {
                startProgDial();
            }

        });
    }

    private void startProgDial()
    {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Pls Wait", "Processing...", true);
        CountDownTimer timer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                startDial();
            }
        }.start();
    }

    private void startDial()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_test);
        dialog.setTitle("Test Dialog");

        Button agreeBttn = dialog.findViewById(R.id.dialog_ok);
        agreeBttn.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView textConfirm = (TextView) findViewById(R.id.textConfirm);
                textConfirm.setVisibility(View.VISIBLE);
                textConfirm.setText("Confermato!");
                dialog.dismiss();
            }
        });

        Button cancelBttn = dialog.findViewById(R.id.dialog_cancel);
        cancelBttn.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView textConfirm = (TextView) findViewById(R.id.textConfirm);
                textConfirm.setVisibility(View.VISIBLE);
                textConfirm.setText("Non Confermato...");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
