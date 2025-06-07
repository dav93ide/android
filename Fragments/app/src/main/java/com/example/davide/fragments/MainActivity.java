package com.example.davide.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
            Per sostituire il frammento in base alla modalita` dello schermo (portrati/landscape) commentare i frammenti sull'xml
            'activity_main.xml' e decommentare le righe sotto.
            Viene richiamato ogni volta che l'attivita` e` ricostruita, quindi anche nel momento in cui viene modificata la dimensione
            dello schermo da modalita` portrati/landscape. Pertanto viene sostituito il frammento in base alla modalita` dello schermo.
        */
    /*
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //---get the current display info---
        DisplayMetrics display = this.getResources().getDisplayMetrics();

        int width = display.widthPixels;
        int height = display.heightPixels;
        if (width > height)
        {
            //---landscape mode---
            Fragment1 fragment1 = new Fragment1();
            // android.R.id.content refers to the content
            // view of the activity
            fragmentTransaction.replace(android.R.id.content, fragment1);
        }
        else
        {
            //---portrait mode---
            Fragment2 fragment2 = new Fragment2();
            fragmentTransaction.replace(android.R.id.content, fragment2);
        }
        fragmentTransaction.commit();
    */
    }

    public void openSecondActivity(View view)
    {
        startActivity(new Intent("com.example.davide.fragments.SecondActivity"));
    }

}
