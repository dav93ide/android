package com.bodhitech.it.lib_base.lib_base.ui.adapters.view_holders;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.bodhitech.it.lib_base.R;

public class ImgLLContainerViewHolder extends LLContainerViewHolder
        implements  View.OnClickListener {

    public interface IOnClickCallback {
        void onItemClicked(View view, int position);
    }

    public ImageView mImage;

    private IOnClickCallback mCallback;

    public ImgLLContainerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public ImgLLContainerViewHolder(@NonNull View itemView, IOnClickCallback callback) {
        super(itemView);
        mCallback = callback;
    }

    //region [#] Override LLContainerViewHolder Methods
    @Override
    protected void initViews(@NonNull View itemView){
        super.initViews(itemView);
        mImage = itemView.findViewById(R.id.img_view);
    }
    //endregion

    //region [#] Override View.OnClickListener Methods
    @Override
    public void onClick(View v) {
        if(mCallback != null){
            mCallback.onItemClicked(v, getAdapterPosition());
        }
    }
    //endregion

    //region [#] Builder Methods
    public ImgLLContainerViewHolder setImageOnClickListener(){
        mImage.setOnClickListener(this);
        return this;
    }

    public ImgLLContainerViewHolder setImageSize(int height, int width){
        LinearLayout.LayoutParams lp;
        if(mImage.getLayoutParams() != null){
             lp = new LinearLayout.LayoutParams(mImage.getLayoutParams());
             lp.height = height;
             lp.width = width;
        } else {
            lp = new LinearLayout.LayoutParams(width, height);
        }
        lp.gravity = Gravity.CENTER;
        mImage.setLayoutParams(lp);
        return this;
    }
    //endregion
    
}
