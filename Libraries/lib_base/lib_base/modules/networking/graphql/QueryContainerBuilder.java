package com.bodhitech.it.lib_base.lib_base.modules.networking.graphql;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class QueryContainerBuilder {

    // Mask Types
    private static final byte MASK_REPLACE_QUERY_ARGUMENTS =    0b0001;     // Invece di inviare il json con le variabili va a inserirle nella query i valori sostituendo i tipi degli argomenti.
    private static final byte MASK_REPLACE_EXPLICIT_QUOTES =    0b0010;     // Alle stringhe non vengono automaticamente messe le virgolette ma devono essere aggiunte nei valori passati per le variabili.
    private static final byte MASK_REPLACE_WITH_PLACEHOLDERS =  0b0100;     // Va a sostituire i placeholders "[key_var_name]" presenti nella query con i valori delle variabili.

    private QueryContainer mQueryContainer;
    private byte mMask;

    public QueryContainerBuilder() {
        mQueryContainer = new QueryContainer();
    }

    //region [#] Setter Methods
    public QueryContainerBuilder setQuery(String query) {
        mQueryContainer.setQuery(query);
        return this;
    }

    public QueryContainerBuilder setReplaceQueryArguments(){
        mMask = MASK_REPLACE_QUERY_ARGUMENTS;
        return this;
    }

    public QueryContainerBuilder setReplaceExplicitQuotes(){
        mMask =  MASK_REPLACE_QUERY_ARGUMENTS | MASK_REPLACE_EXPLICIT_QUOTES;
        return this;
    }

    public QueryContainerBuilder setReplaceWithPlaceholders(){
        mMask = MASK_REPLACE_QUERY_ARGUMENTS | MASK_REPLACE_WITH_PLACEHOLDERS;
        return this;
    }
    //endregion

    //region [#] Public Methods
    public QueryContainerBuilder putVariable(String key, Object value) {
        mQueryContainer.putVariable(key, value);
        return this;
    }

    public boolean containsVariable(String key) {
        return mQueryContainer.containsVariable(key);
    }
    //endregion

    //region [#] Builder Methods
    public QueryContainer build() {
        if((mMask & MASK_REPLACE_QUERY_ARGUMENTS) != 0x0){
            if((mMask & MASK_REPLACE_WITH_PLACEHOLDERS) != 0x0){
                mQueryContainer.replaceVariablesPlaceholdersInQuery();
            } else {
                mQueryContainer.replaceVariablesInQuery(mQueryContainer.mVariables, 0x0);
            }
            mQueryContainer.mVariables = null;
        }
        return mQueryContainer;
    }
    //endregion

    //region [#] QueryContainer Public Static Class
    public class QueryContainer {

        @SerializedName("variables")
        private LinkedHashMap<String, Object> mVariables;
        @SerializedName("query")
        private String mQuery;

        QueryContainer() {
            mVariables = new LinkedHashMap<>();
        }

        /** Private Methods **/
        private void setQuery(String query) {
            mQuery = query;
        }

        private void putVariable(String key, Object value) {
            mVariables.put(key, value);
        }

        private boolean containsVariable(String key) {
            return mVariables != null && mVariables.containsKey(key);
        }

        private void replaceVariablesInQuery(LinkedHashMap<String, Object> map, int index){
            if(!TextUtils.isEmpty(mQuery) && map.size() > 0x0){
                List<String> keys = new ArrayList<>(map.keySet());
                for(String key : keys){
                    Object value = map.get(key);
                    if(value instanceof LinkedHashMap){
                        replaceVariablesInQuery((LinkedHashMap<String, Object>) value, index);
                    } else {
                        int i = mQuery.indexOf(key + ":", index) + key.length() + 0x1;
                        int z;
                        if(keys.indexOf(key) < keys.size() - 0x1){
                            z = mQuery.indexOf(",", i);
                        } else {
                            z = mQuery.indexOf(")", i);
                            int x = mQuery.substring(i, z).indexOf('}');
                            if(x != -0x1){
                                if(mQuery.substring(i, i + 0x4).contains("{")){
                                    x++;
                                }
                                z -= ((z - i) - x);
                            }
                        }

                        String replace;
                        if((mMask & MASK_REPLACE_EXPLICIT_QUOTES) != 0x0){
                            replace = String.valueOf(value);
                        } else {
                            replace = value instanceof String ?
                                    "\"" + value.toString() + "\"" : String.valueOf(value);
                        }
                        String sub = mQuery.substring(i, z)
                                .replaceAll("[\\\\]?\\[", "\\\\\\[").replaceAll("[\\\\]?\\]", "\\\\\\]")
                                .replaceAll("[\\\\]?\\{", "\\\\\\{").replaceAll("[\\\\]?\\}", "\\\\\\}");
                        mQuery = mQuery.replaceFirst(sub.contains("{}") ? sub.replace("{}", "").trim() : sub.trim(), replace);
                        index = z + 0x1;
                    }
                }
            }
        }

        private void replaceVariablesPlaceholdersInQuery(){
            if(!TextUtils.isEmpty(mQuery) && mVariables.size() > 0x0){
                for(String key : mVariables.keySet()){
                    mQuery = mQuery.replaceFirst("<" + key + ">", mVariables.get(key) != null ? mVariables.get(key).toString() : "null");
                }
                mVariables = null;
            }
        }

    }
    //endregion

}