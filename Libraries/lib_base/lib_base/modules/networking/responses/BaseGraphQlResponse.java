package com.bodhitech.it.lib_base.lib_base.modules.networking.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseGraphQlResponse<T> {

    @SerializedName("data")
    private T mData;

    @SerializedName("errors")
    private List<Error> mErrors;

    /** Getter Methods **/
    public T getData(){
        return mData;
    }

    public List<Error> getErrors(){
        return mErrors;
    }

    /** Setter Methods **/
    public void setData(T data){
        mData = data;
    }

    public void setErrors(List<Error> errors){
        mErrors = errors;
    }

    @Override
    public String toString() {
        return "BaseGraphQlResponse{" +
                "mData=" + mData +
                ", mErrors=" + mErrors +
                '}';
    }

    /** Private Classes **/
    public class Error {

        @SerializedName("message")
        private String mMessage;
        @SerializedName("locations")
        private List<Location> mLocations;
        @SerializedName("extensions")
        private Extensions mExtensions;

        /** Getter Methods **/
        public String getMessage(){
            return mMessage;
        }

        public List<Location> getLocations(){
            return mLocations;
        }

        public Extensions getExtensions(){
            return mExtensions;
        }

        /** Setter Methods **/
        public void setMessage(String message){
            mMessage = message;
        }

        public void setLocations(List<Location> locations){
            mLocations = locations;
        }

        public void setExtensions(Extensions ext){
            mExtensions = ext;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "mMessage='" + mMessage + '\'' +
                    ", mLocations=" + mLocations +
                    ", mExtensions=" + mExtensions +
                    '}';
        }

        /** Error - Private Classes **/
        public class Location {

            @SerializedName("line")
            private int mLine;
            @SerializedName("column")
            private int mColumn;

            /** Getter Methods **/
            public int getLine(){
                return mLine;
            }

            public int getColumn(){
                return mColumn;
            }

            /** Setter Methods **/
            public void setLine(int line){
                mLine = line;
            }

            public void setColumn(int column){
                mColumn = column;
            }

            @Override
            public String toString() {
                return "Location{" +
                        "mLine=" + mLine +
                        ", mColumn=" + mColumn +
                        '}';
            }
        }

        public class Extensions {

            @SerializedName("code")
            private String mCode;
            @SerializedName("exception")
            private Exception mException;

            /** Getter Methods **/
            public String getCode(){
                return mCode;
            }

            public Exception getException(){
                return mException;
            }

            /** Setter Methods **/
            public void setCode(String code){
                mCode = code;
            }

            public void setException(Exception ex){
                mException = ex;
            }

            @Override
            public String toString() {
                return "Extensions{" +
                        "mCode='" + mCode + '\'' +
                        ", mException=" + mException +
                        '}';
            }

            /** Extensions - Private Classes **/
            private class Exception {

                @SerializedName("stacktrace")
                private List<String> mStackTrace;

                /** Getter Methods **/
                public List<String> getStackTrace(){
                    return mStackTrace;
                }

                /** Setter Methods **/
                public void setStackTrace(List<String> stacktrace){
                    mStackTrace = stacktrace;
                }

                @Override
                public String toString() {
                    return "Exception{" +
                            "mStackTrace=" + mStackTrace +
                            '}';
                }
            }
        }

    }

}
