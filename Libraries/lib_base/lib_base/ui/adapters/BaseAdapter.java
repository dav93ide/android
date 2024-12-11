package com.bodhitech.it.lib_base.lib_base.ui.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, K> extends RecyclerView.Adapter<T> {

    protected Context mContext;
    protected List<K> mItems;

    public BaseAdapter(@NonNull Context context){
        mContext = context;
        mItems = new ArrayList<>();
    }

    //region Override RecyclerView.Adapter<T> Methods
    @Override
    public int getItemCount() {
        return mItems.size();
    }
    //endregion

    //region [#] Public Methods
    public void clear(){
        mItems.clear();
        notifyDataSetChanged();
    }

    public void addAll(@NonNull List<K> items){
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public List<K> getItems(){
        return mItems;
    }

    public boolean isEmpty(){
        return mItems.isEmpty();
    }

    public void removeItem(int position){
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public K getItem(int position){
        return (K) mItems.get(position);
    }
    //endregion

}
