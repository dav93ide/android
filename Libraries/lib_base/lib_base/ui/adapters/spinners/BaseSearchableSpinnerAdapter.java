package com.bodhitech.it.lib_base.lib_base.ui.adapters.spinners;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bodhitech.it.lib_base.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSearchableSpinnerAdapter<T extends Object> extends ArrayAdapter<CharSequence> {

    // Empty Item Label
    protected static final String LABEL_EMPTY_ITEM = "                                                                           ";
    // Label Length
    protected static final int LABEL_LENGTH = 50;
    // Spinner Adapter Positions
    public static final int POS_ITEM_NOT_FOUND = -0x1;
    public static final int POS_EMPTY_ITEM = 0x0;       // Not always true, depends if implemented

    protected List<T> mItems;
    private int mResLayout;

    public BaseSearchableSpinnerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mItems = new ArrayList<>();
        mResLayout = resource;
    }

    /** Abstract Methods **/
    public abstract <T extends CharSequence> T getLabelView(int pos);

    /** Override ArrayAdapter Methods **/
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mResLayout, parent, false);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_default, parent, false);
            }
        }
        TextView tv = convertView.findViewById(R.id.text1);
        if(tv != null){
            tv.setText(getLabelView(position));
        }
        return convertView;
    }

    @Override
    public void clear(){
        mItems.clear();
        super.clear();
    }

    /** Public Methods **/
    public void addAll(List<T> objs){
        clear();
        ArrayList<CharSequence> labels = new ArrayList<>();
        if(objs != null && objs.size() > 0x0){
            mItems.addAll(objs);
            for(int i=0x0; i<objs.size(); i++){
                labels.add(getLabelView(i));
            }
        }
        super.addAll(labels);
    }

    public T getMyItem(int pos){
        if(mItems != null && mItems.size() > pos && pos != -0x1){
            return mItems.get(pos);
        }
        return null;
    }

    public List<T> getMyItems(){
        return mItems;
    }

}
