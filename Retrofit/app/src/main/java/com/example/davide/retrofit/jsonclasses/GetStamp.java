package com.example.davide.retrofit.jsonclasses;

public class GetStamp {

    public Data data;

    public class Data {
        public UserStamp[] userStamps;

        public class UserStamp {
            public Integer index;
            public String ip;
            public String tipo;
            public String creation;
            public String __typename;
        }
    }

}
