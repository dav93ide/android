package com.example.davide.retrofit.classes;

public class GraphiqlRequest {

    public String token;
    public String query;

    public GraphiqlRequest(String token, String query){
        this.token = token;
        this.query = query;
    }

}
