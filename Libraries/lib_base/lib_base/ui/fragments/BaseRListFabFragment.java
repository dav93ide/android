package com.bodhitech.it.lib_base.lib_base.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.bodhitech.it.lib_base.R;

public abstract class BaseRListFabFragment<T extends BaseRListFabFragment.IFragmentCommunication>
        extends BaseRefreshableListFragment<T> {

    public interface IFragmentCommunication extends BaseRefreshableListFragment.IFragmentCommunication {
        void onClickFAB(String tag, int requestCode);
    }

    protected FloatingActionButton mFAB;

    /** Override Lifecycle Methods **/
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_refreshable_list_with_fab, container, false);
        initOnCreateView();
        return view;
    }

    @Override
    public void onDestroyView() {
        mFAB = null;
        super.onDestroyView();
    }

    /** Override BaseRefreshableListFragment Methods **/
    @Override
    protected void checkSetBaseViews(@NonNull View view){
        super.checkSetBaseViews(view);
        if(mFAB == null){
            mFAB = view.findViewById(R.id.fa_bttn);
        }
        initFabOnClickListener(view);
    }

    /** Protected Methods **//** Init Methods **/
    protected void initFabOnClickListener(View view){
        FloatingActionButton fab = view.findViewById(R.id.fa_bttn);
        if(fab != null){
            fab.setOnClickListener(v -> {
                if(mCallback != null){
                    mCallback.onClickFAB(getTag(), 0x0);
                }
            });
        }
    }

}
