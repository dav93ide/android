package com.bodhitech.it.lib_base.lib_base.ui.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bodhitech.it.lib_base.R;

public abstract class BaseListFragment extends BaseFragment {

    protected TextView mtvNoItems;
    protected RecyclerView mRecyclerView;

    //region [#] Abstract Methods
    protected abstract void initRecyclerView();
    //endregion

    //region [#] Override Lifecycle Methods
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list, container, false);
        checkSetBaseViews(view);
        initOnCreateView();
        return view;
    }

    @Override
    public void onDestroyView() {
        mtvNoItems = null;
        mRecyclerView = null;
        super.onDestroyView();
    }
    //endregion

    //region [#] Protected Methods
    protected void checkSetBaseViews(@NonNull View view){
        if (mtvNoItems == null) {
            mtvNoItems = view.findViewById(R.id.text_view_no_items);
        }
        if (mRecyclerView == null) {
            mRecyclerView = view.findViewById(R.id.recycler_view);
        }
    }

    protected void initOnCreateView(){
        initRecyclerView();
        initDecorator();
    }

    protected void showRVHideTV(){
        mRecyclerView.setVisibility(RecyclerView.VISIBLE);
        mtvNoItems.setVisibility(TextView.GONE);
    }

    protected void showTVHideRV(){
        mRecyclerView.setVisibility(RecyclerView.GONE);
        mtvNoItems.setVisibility(TextView.VISIBLE);
    }
    //endregion

    //region [#] Private Methods
    private void initDecorator(){
        mRecyclerView.addItemDecoration(BottomOffsetDecorator.initInstance());
    }
    //endregion

    //region [#] Private Class Bottom Offset ItemDecorator
    public static class BottomOffsetDecorator extends RecyclerView.ItemDecoration {

        private static final int OFFSET_BOTTOM = 250;

        private BottomOffsetDecorator(){ }

        public static BottomOffsetDecorator initInstance(){
            return new BottomOffsetDecorator();
        }

        //region [#] Override RecyclerView.ItemDecoration Methods
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int numItems = state.getItemCount();
            int position = parent.getChildAdapterPosition(view);
            if(numItems > 0x0 && position == (numItems - 0x1)){
                outRect.set(0x0, 0x0, 0x0, OFFSET_BOTTOM);
            } else {
                outRect.set(0x0, 0x0, 0x0, 0x0);
            }
        }
        //endregion
    }
    //endregion

}
