package com.example.davide.passdatatosecondactivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SecondActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView textView = findViewById(R.id.textParams);

        Intent i = getIntent();
        Bundle bund = i.getExtras();
        String oldText = textView.getText().toString();
        String newText = oldText + "\n\nUsername: " + i.getStringExtra("username") + "\nAge: " + i.getIntExtra("age", 0);
        newText += "\n\n" + "TestOne: " +  bund.getString("testOne") + "\nTestTwo: " + bund.getInt("testTwo") + "\n\n";

        textView.setText(newText);
    }

    public void closeBttnClicked(View view)
    {
        Intent data = new Intent();
        data.putExtra("age", 666);
        data.setData(Uri.parse("Tutto Ok!"));
        setResult(RESULT_OK, data);
        finish();
    }

}
