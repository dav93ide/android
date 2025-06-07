package com.example.davide.dialog;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textView = (TextView) findViewById(R.id.text);
        textView.setMovementMethod(new ScrollingMovementMethod());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button setButton = this.findViewById(R.id.set);
        setButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView textView = (TextView) findViewById(R.id.text);
                EditText editText = (EditText) findViewById(R.id.inText);
                String newText = editText.getText().toString();
                String oldText = textView.getText().toString();
                String text = oldText + '\n' + newText;
                textView.setText(text);
            }
        });

        Button closeButton = this.findViewById(R.id.close);
        closeButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.exit(0);
            }
        });

        Button resetButton = this.findViewById(R.id.reset);
        resetButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView textView = (TextView) findViewById(R.id.text);
                String voidText = "";
                textView.setText(voidText);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}