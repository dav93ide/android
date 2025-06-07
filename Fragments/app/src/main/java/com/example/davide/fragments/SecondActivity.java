package com.example.davide.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Fragment;
import android.view.View;

public class SecondActivity extends Activity {

    Fragment newFragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void replaceFragmentThreeClicked(View view)
    {
        newFragment = new Fragment3();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, newFragment );
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void closeActivityClicked(View view)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Closing", "Closing Activity...", true);
        CountDownTimer timer = new CountDownTimer(3000,1000) {
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

    public void replaceFragmentTwoClicked(View view)
    {
        newFragment = new Fragment2();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, newFragment );
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
