package com.bodhitech.it.lib_base.lib_base.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.R;

public abstract class BaseListFabFragment<T extends BaseListFabFragment.IFragmentCommunication> extends BaseListFragment {

    public interface IFragmentCommunication {
        void onClickFAB(String tag, int requestCode);
    }

    private static final String TAG = BaseListFabFragment.class.getSimpleName();

    protected FloatingActionButton mFAB;

    protected T mCallback;

    //region [#] Override Lifecycle Methods
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(mCallback == null){
            try{
                mCallback = (T) context;
            } catch(ClassCastException ccE){
                BaseEnvironment.onExceptionLevelLow(TAG, ccE);
            }
        }
    }

    @Override @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list_with_fab, container, false);
        checkSetBaseViews(view);
        initOnCreateView();
        return view;
    }
    //endregion

    //region [#] Override BaseListFragment Methods
    @Override
    protected void checkSetBaseViews(@NonNull View view) {
        super.checkSetBaseViews(view);
        if(mFAB == null){
            mFAB = view.findViewById(R.id.fa_bttn);
        }
    }

    @Override
    protected void initOnCreateView() {
        super.initOnCreateView();
        initOnClickListenerFab();
    }
    //endregion

    //region [#] Protected Methods
    protected void initOnClickListenerFab(){
        if(mFAB != null){
            mFAB.setOnClickListener(this::onClickFAButton);
        }
    }

    protected void onClickFAButton(View view){
        if(mCallback != null){
            mCallback.onClickFAB(getTag(), 0x0);
        }
    }
    //endregion

}
