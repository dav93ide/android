package com.example.davide.databinding;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.davide.databinding.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    Person person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        person = new Person("Ravi","Rupareliya");
        binding.setPerson(person);
    }

    public void updateValue(View v)
    {
        this.person.setFirstName("Android");
        this.person.setLastName("Gig");
    }

}