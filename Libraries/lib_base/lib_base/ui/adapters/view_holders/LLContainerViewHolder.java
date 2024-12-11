package com.bodhitech.it.lib_base.lib_base.ui.adapters.view_holders;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bodhitech.it.lib_base.R;

public class LLContainerViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout mllContainer;

    public LLContainerViewHolder(@NonNull View itemView) {
        super(itemView);
        initViews(itemView);
    }

    //region [#] Protected Methods
    protected void initViews(@NonNull View itemView){
        mllContainer = itemView.findViewById(R.id.ll_container);
    }
    //endregion

}