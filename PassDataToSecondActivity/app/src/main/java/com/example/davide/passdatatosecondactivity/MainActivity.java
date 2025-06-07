package com.example.davide.passdatatosecondactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int request_code = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showSecondActivity(View view)
    {
        Intent i = new Intent("com.example.davide.passdatatosecondactivity.SecondActivity");

        EditText txtUsername = findViewById(R.id.txtUsername);
        EditText txtAge = findViewById(R.id.txtAge);

        try{
            String username = txtUsername.getText().toString();
            Integer age = Integer.parseInt(txtAge.getText().toString());

            if( !username.isEmpty() && !username.trim().isEmpty() && !age.toString().isEmpty() ) {
                i.putExtra("username", username);
                i.putExtra("age", age);

                Bundle extras = new Bundle();
                extras.putString("testOne", "Test String One");
                extras.putInt("testTwo", 666);
                i.putExtras(extras);

                startActivityForResult(i, request_code);
            } else {
                Toast.makeText( this, "E` necessario inserire dei valori!", Toast.LENGTH_SHORT ).show();
            }
        } catch(NumberFormatException nfe) {
            Toast.makeText( this, "L'eta` deve essere un valore numerico!", Toast.LENGTH_SHORT ).show();
        }

    }

    public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        if( requestCode == request_code )
        {
            if( resultCode == RESULT_OK )
            {
                Toast.makeText( this, "Return One => " +  Integer.toString(data.getIntExtra("age", 0)), Toast.LENGTH_SHORT ).show();
                Toast.makeText( this, "Return Two => " + data.getData().toString(), Toast.LENGTH_SHORT ).show();
            }
        }
    }

}
