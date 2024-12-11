package com.bodhitech.it.lib_base.lib_base.ui.adapters.view_holders;

import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.bodhitech.it.lib_base.R;

public class ActionsImgLLContainerViewHolder extends ImgLLContainerViewHolder {

    public ImageButton mibActions;

    public ActionsImgLLContainerViewHolder(@NonNull View itemView, IOnClickCallback callback) {
        super(itemView, callback);
        mibActions.setOnClickListener(this);
    }

    public ActionsImgLLContainerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    //region [#] Override ImgLLContainerViewHolder Methods
    @Override
    protected void initViews(@NonNull View itemView){
        super.initViews(itemView);
        mibActions = itemView.findViewById(R.id.ib_actions);
    }
    //endregion

}
