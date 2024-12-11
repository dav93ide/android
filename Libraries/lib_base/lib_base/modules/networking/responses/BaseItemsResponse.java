package com.bodhitech.it.lib_base.lib_base.modules.networking.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseItemsResponse<T> extends BaseResponse {

    // Json Fields
    public static final String JSON_FIELD_ITEMS = "items";

    @SerializedName(value = "items", alternate = {"data", "lcl", "fasi", "dipendenti", "oreAttrMezzi", "rec", "motivi"})
    private List<T> mItems;

    /** Getter & Setter Methods **/
    public List<T> getItems() {
        return mItems;
    }

    public void setItems(List<T> items) {
        this.mItems = items;
    }

    @Override
    public String toString() {
        return "BaseItemsResponse{" +
            "mItems=" + mItems +
            ", mSuccess=" + super.getSuccess() +
            ", mMsg='" + super.getMsg()+ '\'' +
            ", mErrors='" + super.getErrors() + '\'' +
            '}';
    }

}
