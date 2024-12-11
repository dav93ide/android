package com.bodhitech.it.lib_base.lib_base.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.R;

public abstract class BaseRefreshableListFragment<T extends BaseRefreshableListFragment.IFragmentCommunication>
    extends BaseListFragment
    implements  SwipeRefreshLayout.OnRefreshListener {

    public interface IFragmentCommunication {
        void onRefreshRequest(String tag, boolean showProgress);
    }

    private static final String TAG = BaseRefreshableListFragment.class.getSimpleName();

    protected SwipeRefreshLayout msrlReload;

    protected T mCallback;

    /** Override Lifecycle Methods **/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mCallback = (T) context;
        } catch(ClassCastException ccE){
            BaseEnvironment.onExceptionLevelLow(TAG, ccE);
        }
    }

    @Override @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_refreshable_list, container, false);
        checkSetBaseViews(view);
        initOnCreateView();
        return view;
    }

    @Override
    public void onDestroyView() {
        msrlReload = null;
        super.onDestroyView();
    }

    /** Override BaseListFragment Methods **/
    @Override
    protected void initOnCreateView(){
        super.initOnCreateView();
        initSwipeToRefreshLayout();
        initRecyclerViewOnItemTouchListener();
    }

    @Override
    protected void checkSetBaseViews(@NonNull View view){
        super.checkSetBaseViews(view);
        if(msrlReload == null){
            msrlReload = view.findViewById(R.id.swipe_refresh);
        }
    }

    /** Override SwipeRefreshLayout Callback **/
    @Override
    public void onRefresh() {
        refresh(false);
    }

    /** Public Methods **//** SwipeRefreshLayout Methods **/
    public void setRefreshing(boolean isRefreshing){
        if(msrlReload != null) {
            msrlReload.setRefreshing(isRefreshing);
        }
    }

    public boolean isRefreshing(){
        return msrlReload.isRefreshing();
    }

    /** Protected Methods **/
    protected void callBaseOnAttach(Context context){
        super.onAttach(context);
    }

    protected void setRefreshLayoutEnabled(boolean enabled){
        msrlReload.setEnabled(enabled);
    }

    protected void initSwipeToRefreshLayout(){
        if(msrlReload != null){
            msrlReload.setColorSchemeResources(
                    android.R.color.holo_blue_dark,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_red_dark);
            msrlReload.setOnRefreshListener(this);
        }
    }

    protected void initRecyclerViewOnItemTouchListener(){
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP || (((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0x0 &&
                        e.getY() - mRecyclerView.getY() <= mRecyclerView.getHeight() / 0x4)) {
                    msrlReload.setEnabled(true);
                } else {
                    msrlReload.setEnabled(false);
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /** Refresh Methods **/
    protected void refresh(boolean showProgress){
        if(mCallback != null){
            setRefreshing(true);
            mCallback.onRefreshRequest(getTag(), showProgress);
        }
    }

}
