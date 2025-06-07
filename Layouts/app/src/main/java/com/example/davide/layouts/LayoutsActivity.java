package com.example.davide.layouts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

public class LayoutsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Integer selection = i.getIntExtra("selection", 0);
        if( !selection.toString().isEmpty() && !selection.equals(0) )
        {
            switch(selection)
            {
                case 1:
                    setContentView(R.layout.frame_layout);
                    break;
                case 2:
                    setContentView(R.layout.linear_layout);
                    break;
                case 3:
                    setContentView(R.layout.table_layout);
                    break;
                case 4:
                    setContentView(R.layout.relative_layout);
                    break;
            }
        } else {
            Toast.makeText( this, "Attenzione selezione non valida!/nChiusura dell'attivita`...", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void closeActivityClicked(View view)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Closing Activity", "Closing...", true);
        CountDownTimer timer = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                finish();
            }
        }.start();
    }

}
