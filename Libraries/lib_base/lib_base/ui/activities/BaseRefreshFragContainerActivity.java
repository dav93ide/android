package com.bodhitech.it.lib_base.lib_base.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bodhitech.it.lib_base.R;

public abstract class BaseRefreshFragContainerActivity extends BaseFragmentContainerActivity
    implements  SwipeRefreshLayout.OnRefreshListener {

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    /** Override Lifecycle Methods **/
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.callBaseOnCreate(savedInstanceState);
        setContentView(R.layout.activity_base_refreshable_fragment_container);
        initOnCreate();
    }

    /** Override BaseFragContainerAttachAndActionsActivity Methods **/
    @Override
    protected void initOnCreate() {
        super.initOnCreate();
        initSwipeToRefreshLayout();
    }

    @Override
    protected void initBaseViews(){
        super.initBaseViews();
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    @Override
    protected void hideLoadingAndRefreshing() {
        hideRefreshing();
        dismissProgressDialog();
    }

    @Override
    protected void hideRefreshing(){
        if(isRefreshing()){
            setRefreshing(false);
        }
    }

    /** Protected Methods **/
    protected void setRefreshing(boolean isRefreshing){
        if(mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(isRefreshing);
        }
    }

    protected boolean isRefreshing(){
        return mSwipeRefreshLayout.isRefreshing();
    }

    protected void initColorSchemeSwipeRefreshLayout(@ColorRes int... ids){
        mSwipeRefreshLayout.setColorSchemeResources(ids[0x0], ids[0x1], ids[0x2], ids[0x3], ids[0x4]);
    }

    /** Private Methods **/
    private void initSwipeToRefreshLayout(){
        int size = mToolbar.getLayoutParams().height;
        mSwipeRefreshLayout.setSlingshotDistance(size / 0x3);
        mSwipeRefreshLayout.setDistanceToTriggerSync(size / 0x3);
        initColorSchemeSwipeRefreshLayout(android.R.color.holo_blue_dark, android.R.color.holo_green_dark, android.R.color.holo_orange_dark, android.R.color.holo_red_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

}
