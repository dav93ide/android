package com.bodhitech.it.lib_base.lib_base.ui.adapters.view_holders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bodhitech.it.lib_base.R;

public class ImgViewHolder extends RecyclerView.ViewHolder
        implements  View.OnClickListener {

    public interface IOnClickCallback {
        void onClickImage(int position);
    }

    public CardView mCardView;
    public ImageView mImage;

    private IOnClickCallback mCallback;

    public ImgViewHolder(@NonNull View itemView, IOnClickCallback callback) {
        super(itemView);
        initViews(itemView);
        mCallback = callback;
        itemView.setOnClickListener(this);
    }

    //region [#] Override View.OnClickListener Methods
    @Override
    public void onClick(View v) {
        if(mCallback != null){
            mCallback.onClickImage(getAdapterPosition());
        }
    }
    //endregion

    //region [#] Private Methods
    private void initViews(View itemView){
        mCardView = itemView.findViewById(R.id.item_card_view);
        mImage = itemView.findViewById(R.id.img_view);
    }
    //endregion

}
