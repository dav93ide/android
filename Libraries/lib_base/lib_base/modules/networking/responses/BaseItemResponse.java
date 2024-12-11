package com.bodhitech.it.lib_base.lib_base.modules.networking.responses;

import com.google.gson.annotations.SerializedName;

public class BaseItemResponse<T> extends BaseResponse {

    @SerializedName(value = "item", alternate = {"team", "idNc", "id", "info"})
    private T mItem;

    /** Getter & Setter Methods **/
    public T getItem() {
        return mItem;
    }

    public void setItem(T mItem) {
        this.mItem = mItem;
    }

    @Override
    public String toString() {
        return "BaseItemResponse{" +
            "mItem=" + mItem +
            ", BaseResponse=" + super.toString() +
            '}';
    }

}
