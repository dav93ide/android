package com.example.davide.retrofit.jsonclasses;

import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("success")
    String mSuccess;

    @SerializedName("user")
    User uUser;

    @SerializedName("token")
    String mToken;

    public class User{

        @SerializedName("idUtente")
        Integer uId;

        @SerializedName("nome")
        String uName;

        @SerializedName("cognome")
        String uSurname;

        @SerializedName("email")
        String uEmail;

        public Integer getuId() {
            return uId;
        }

        public String getuName() {
            return uName;
        }

        public String getuSurname() {
            return uSurname;
        }

        public String getuEmail() {
            return uEmail;
        }
    }

    public String getmSuccess() {
        return mSuccess;
    }

    public User getuUser() {
        return uUser;
    }

    public String getmToken() {
        return mToken;
    }

}
