package com.example.davide.retrofit.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.davide.retrofit.R;
import com.example.davide.retrofit.api.MyAPI;
import com.example.davide.retrofit.classes.GraphiqlRequest;
import com.example.davide.retrofit.classes.LoginRequest;
import com.example.davide.retrofit.jsonclasses.GetStamp;
import com.example.davide.retrofit.jsonclasses.Login;
import com.example.davide.retrofit.rest.RetrofitMain;
import com.example.davide.retrofit.utils.Commons;
import com.example.davide.retrofit.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_username) EditText etUsername;
    @BindView(R.id.et_password) EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bttn_login)
    public void loginClicked(View view){
        LoginRequest logReq = makeLoginRequest();
        if( logReq != null ) {
            final View v = view;
            RetrofitMain retM = RetrofitMain.getInstance();
            retM.initIstance(Commons.URL_EMAX_PROFILO);
            Call<Login> login;
            MyAPI logApi = retM.createService(MyAPI.class);
            login = logApi.doLogin(logReq);
            login.enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    Login log = response.body();
                    if( log.getmSuccess().equals("true") ){
                        String token = log.getmToken();
                        Integer idUtente = log.getuUser().getuId();
                        Toast.makeText(v.getContext(), "Login Effettuato Con Successo: " + token, Toast.LENGTH_SHORT).show();
                        getStamps(token, idUtente);
                    } else {
                        Toast.makeText(v.getContext(), "Login Fallito", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    Log.e("Fail", "aaa");
                }
            });
        }
    }

    private LoginRequest makeLoginRequest(){
        LoginRequest logReq = null;
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password))
        {
            logReq = new LoginRequest(username, password, Commons.SYSTEM_LOGIN);
        } else {
            if(TextUtils.isEmpty(username)){
                etUsername.setError("Inserire Valore!");
            }
            if(TextUtils.isEmpty(password)){
                etPassword.setError("Inserire Valore!");
            }
            Toast.makeText(this, "Valori Inseriti Errati!", Toast.LENGTH_SHORT).show();
        }
        return logReq;
    }

    private void getStamps(String token, int idUtente){
        RetrofitMain retM = RetrofitMain.getInstance();
        retM.initIstance(Commons.URL_EMAX_PROFILO);
        Call<GetStamp> stamps;
        MyAPI stampsApi = retM.createService(MyAPI.class);
        HashMap<String, Object> params = new HashMap<>();
        params.put("GraphQuery", "userStamps");
        params.put("idUtente", idUtente);

        String query = Utils.buildQueryGraphql(params, Commons.GRAPHIQL_USERSTAMPS_FIELDS);
        GraphiqlRequest gSReq = new GraphiqlRequest(token, query);
        stamps = stampsApi.getStamps(gSReq);
        stamps.enqueue(new Callback<GetStamp>() {
            @Override
            public void onResponse(Call<GetStamp> call, Response<GetStamp> response) {
                GetStamp stamps = response.body();
                Log.e("Stamps", "HERE!");
            }

            @Override
            public void onFailure(Call<GetStamp> call, Throwable t) {
                Log.e("Fail", "HERE!");
            }
        });
    }

}
