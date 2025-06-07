package com.example.davide.retrofit.utils;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Utils {

    public static final String LOGCAT = Utils.class.getSimpleName();

    public static String buildQueryGraphql(HashMap<String, Object> params, String[] fields){
        String query = null;
        if( params.containsKey("GraphQuery") && params.size() > 1 && fields.length > 0 ) {
            query = "query {" + params.get("GraphQuery") + "(";
            params.remove("GraphQuery");
            Set<String> keys = params.keySet();
            int i = 1;
            for (String key : keys) {
                query += key.toString() + ": ";
                String type = params.get(key).getClass().getSimpleName();
                switch (type) {
                    case "String":
                        query += "'" + params.get(key) + "'";
                        break;
                    default:
                        query += params.get(key);
                        break;
                }
                if (i != keys.size()) {
                    query += ", ";
                } else {
                    query += ") {";
                }
                i++;
            }
            for (String field : fields) {
                query += field + " ";
            }
            query += "}}";
        } else {
            Log.v(LOGCAT, "buildQueryGraphql: Wrong arguments!" );
        }
        return query;
    }

}
