package com.bodhitech.it.lib_base.lib_base.modules.networking.responses;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    // Json Fields Names
    public static final String JSON_FIELD_SUCCESS = "success";
    public static final String JSON_FIELD_MSG = "msg";

    @SerializedName("success")
    private Boolean mSuccess;
    @SerializedName(value = "msg", alternate = {"message"})
    private String mMsg;
    @SerializedName("errors")
    private String mErrors;

    /** Getter & Setter Methods **/
    public Boolean getSuccess() {
        if(mSuccess == null){
            mSuccess = false;
        }
        return mSuccess;
    }

    public void setSuccess(Boolean success) {
        this.mSuccess = success;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        this.mMsg = msg;
    }

    public String getErrors() {
        return mErrors;
    }

    public void setErrors(String errors) {
        this.mErrors = errors;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
            "mSuccess=" + mSuccess +
            ", mMsg='" + mMsg + '\'' +
            ", mErrors='" + mErrors + '\'' +
            '}';
    }

}
