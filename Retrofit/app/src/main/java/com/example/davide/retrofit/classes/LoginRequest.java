package com.example.davide.retrofit.classes;

public class LoginRequest {

   //public final String fingerprint = "68dc8b75cc447a0dffe765a0a8ed2ad8";
   //public final String idAziendaX = "";
    public String login;
    public String password;
    public String sistema;

    public LoginRequest(String username, String password, String sistema){
        this.login = username;
        this.password = password;
        this.sistema = sistema;
    }

}
