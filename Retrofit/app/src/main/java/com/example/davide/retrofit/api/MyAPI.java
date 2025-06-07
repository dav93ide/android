package com.example.davide.retrofit.api;

import com.example.davide.retrofit.classes.GraphiqlRequest;
import com.example.davide.retrofit.classes.LoginRequest;
import com.example.davide.retrofit.jsonclasses.GetStamp;
import com.example.davide.retrofit.jsonclasses.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MyAPI {

    @POST("/cas2/auth/login/")
    Call<Login> doLogin(@Body LoginRequest loginRequest);

    @POST("/cas2/gql/")
    @Headers("Content-type: application/json")
    Call<GetStamp> getStamps(@Body GraphiqlRequest gSReq);

}
