package com.example.davide.butterknifetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView_1) TextView txtV_1;
    @BindView(R.id.editText_1) EditText txtE_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.bttn_1)
    public void setButtonClicked(){
        String txt = txtE_1.getText().toString();
        if(!TextUtils.isEmpty(txt)){
            txtV_1.setText(txt);
        } else {
            txtE_1.setError("Inserire un valore!");
        }
    }

}
