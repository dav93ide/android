package com.example.davide.retrofit.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitMain {

    protected static RetrofitMain thisIstance;
    private static Retrofit iRetrofit;

    // Public Static Factory Method => Ritorna istanza della classe
    public static RetrofitMain getInstance(){
        if( thisIstance == null){
            thisIstance = new RetrofitMain();
        }
        return thisIstance;
    }

    public void initIstance(String baseUrl){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        iRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public <T> T createService(Class<T> service){
        return iRetrofit.create(service);
    }

}
