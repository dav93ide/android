package com.example.davide.layouts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner layouts_spin = (Spinner) findViewById(R.id.layouts_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.layouts_array, android.R.layout.simple_spinner_dropdown_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        layouts_spin.setAdapter(adapter);
    }

    public void startLayoutActivityClicked(View view)
    {
        Spinner layouts_spin = (Spinner) findViewById(R.id.layouts_spinner);
        Integer selected = layouts_spin.getSelectedItemPosition();
        if( !selected.toString().isEmpty() && !selected.equals(0) )
        {
            Intent i = new Intent("com.example.davide.layouts.LayoutsActivity");
            i.putExtra("selection", selected);
            startActivity(i);
        } else {
            Toast.makeText( this, "E` necessario selezionare un elemento!", Toast.LENGTH_SHORT).show();
        }
    }

}
