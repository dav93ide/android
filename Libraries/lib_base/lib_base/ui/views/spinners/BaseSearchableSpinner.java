package com.bodhitech.it.lib_base.lib_base.ui.views.spinners;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.toptoche.searchablespinnerlibrary.SearchableListDialog;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.List;

public class BaseSearchableSpinner extends SearchableSpinner
    implements  SearchView.OnAttachStateChangeListener {

    private static final String TAG = BaseSearchableSpinner.class.getSimpleName();
    private static final String TAG_DIALOG = TAG + ".dialog";
    // Dialogs Tags
    private static final String TAG_DIALOG_SEARCHABLE_LIST = TAG_DIALOG + ".searchableList";
    // SearchableSpinner Fields
    private static final String FIELD_SEARCHABLE_LIST_DIALOG = "_searchableListDialog";
    private static final String FIELD_SEARCH_VIEW = "_searchView";
    private static final String FIELD_SEARCHABLE_ITEM = "_searchableItem";
    private static final String FIELD_ARRAY_ADAPTER = "_arrayAdapter";
    private static final String FIELD_ITEMS = "_items";

    private boolean mIsListDialogAdded;
    private boolean mIsListenerAdded;

    public BaseSearchableSpinner(Context context) {
        super(context);
        initListDialog();
    }

    public BaseSearchableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initListDialog();
    }

    public BaseSearchableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initListDialog();
    }

    /** Override SearchableSpinner Methods **/
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try{
            SearchableListDialog sld = (SearchableListDialog) FieldUtils.readField(this, FIELD_SEARCHABLE_LIST_DIALOG, true);
            ArrayAdapter adapter = (ArrayAdapter) FieldUtils.readField(this, FIELD_ARRAY_ADAPTER, true);
            List items = (List) FieldUtils.readField(this, FIELD_ITEMS, true);
            if (sld != null && adapter != null && items != null && event.getAction() == MotionEvent.ACTION_UP && checkIfListDialogNotAdded()) {
                if(mIsListenerAdded){
                    mIsListDialogAdded = true;
                }
                items.clear();
                for (int i = 0x0; i < adapter.getCount(); i++) {
                    items.add(adapter.getItem(i));
                }
                sld.show(scanForActivity(getContext()).getFragmentManager(), TAG_DIALOG_SEARCHABLE_LIST);
            }
        } catch (IllegalAccessException iaE){
            BaseEnvironment.onExceptionLevelLow(TAG, iaE);
        }
        return true;
    }

    /** Override SearchView.OnAttachStateChangeListener Methods **/
    @Override
    public void onViewAttachedToWindow(View view) {
        mIsListDialogAdded = true;
    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        mIsListDialogAdded = false;
    }

    /** Private Methods **/
    private void initListDialog(){
        try{
            SearchableListDialog oldD = (SearchableListDialog) FieldUtils.readField(this, FIELD_SEARCHABLE_LIST_DIALOG, true);
            if(oldD != null) {
                BaseSearchableListDialog newD = new BaseSearchableListDialog(this);
                newD.setArguments(oldD.getArguments());
                newD.setOnSearchableItemClickListener(this);
                FieldUtils.writeField(this, FIELD_SEARCHABLE_LIST_DIALOG, newD, true);
            }
        } catch (IllegalAccessException iaE){
            BaseEnvironment.onExceptionLevelLow(TAG, iaE);
        }
    }

    private void initListenerOnCloseSearchView(SearchableListDialog instance) {
        try{
            SearchView sv = (SearchView) FieldUtils.readField(instance, FIELD_SEARCH_VIEW, true);
            if(sv != null){
                sv.addOnAttachStateChangeListener(this);
                mIsListenerAdded = true;
            }
        } catch (IllegalAccessException iaE){
            BaseEnvironment.onExceptionLevelLow(TAG, iaE);
        }
    }

    private boolean checkIfListDialogNotAdded(){
        return !mIsListDialogAdded && scanForActivity(getContext()).getFragmentManager().findFragmentByTag(TAG_DIALOG_SEARCHABLE_LIST) == null;
    }

    private Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());
        return null;
    }

    /** Private Classes  **/
    @SuppressLint("ValidFragment")
    public static class BaseSearchableListDialog extends SearchableListDialog {

        private BaseSearchableSpinner mOuter;

        private BaseSearchableListDialog(BaseSearchableSpinner bss){
            super();
            mOuter = bss;
        }

        /** Override SearchableListDialog Methods **/
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog dialog = (AlertDialog) super.onCreateDialog(savedInstanceState);
            mOuter.initListenerOnCloseSearchView(this);
            return dialog;
        }

    }

}
