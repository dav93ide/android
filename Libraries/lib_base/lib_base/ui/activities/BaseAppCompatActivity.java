package com.bodhitech.it.lib_base.lib_base.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fondesa.lyra.Lyra;
import com.google.android.material.snackbar.Snackbar;
import com.bodhitech.it.lib_base.R;
import com.bodhitech.it.lib_base.lib_base.ui.dialogs.ConfirmDialog;

public class BaseAppCompatActivity extends AppCompatActivity {

    private static final String TAG = BaseViewPagerActivity.class.getSimpleName();
    private static final String TAG_DIALOG = TAG + ".dialog";
    // Tags Dialog
    private static final String TAG_DIALOG_CONFIRM = TAG_DIALOG + ".confirm";

    //region [#] Override Lifecycle Methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Lyra.instance().restoreState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Lyra.instance().saveState(this, outState);
    }
    //endregion

    //region [#] Protected Methods
    protected <T extends Fragment> void setFragment(T mFragment, int idContainer, String tag){
        if(mFragment != null){
            FragmentTransaction fT = getSupportFragmentManager().beginTransaction();
            fT.replace(idContainer, mFragment, tag);
            fT.addToBackStack(tag);
            fT.commit();
        }
    }

    //region [#] Dialog Methods
    protected <T extends DialogFragment> T getDialog(String tag){
        return (T) getSupportFragmentManager().findFragmentByTag(tag);
    }

    protected <T extends DialogFragment> T getDialog(int id){
        return (T) getSupportFragmentManager().findFragmentById(id);
    }

    protected void dismissDialog(String tag){
        DialogFragment dialog = getDialog(tag);
        if(dialog != null){
            dialog.dismiss();
        }
    }

    protected boolean isShowingDialog(String tag){
        return getDialog(tag) != null;
    }
    //endregion

    //region [#] ConfirmDialog Methods
    protected void showConfirmDialog(String title, String subTitle, String msg, int requestCase){
        showConfirmDialog(title, subTitle, msg, false, false, requestCase);
    }

    protected void showConfirmDialog(String title, String subTitle, String msg, boolean subIsHtml, boolean msgIsHtml, int requestCase){
        showConfirmDialog(TAG_DIALOG_CONFIRM, title, subTitle, msg, subIsHtml, msgIsHtml, null, null, requestCase, false);
    }

    protected void showConfirmDialog(String tag, String title, String subTitle, String msg, boolean subIsHtml, boolean msgIsHtml, int requestCase){
        showConfirmDialog(tag, title, subTitle, msg, subIsHtml, msgIsHtml, null, null, requestCase, false);
    }

    protected void showConfirmDialog(String tag, String title, String subTitle, String msg, String abortBttnTxt, String confirmBttnTxt, int requestCase){
        showConfirmDialog(tag, title, subTitle, msg, false, false, abortBttnTxt, confirmBttnTxt, requestCase, false);
    }

    protected void showConfirmDialog(String tag, String title, String subTitle, String msg, boolean subIsHtml, boolean msgIsHtml, String abortBttnTxt, String confirmBttnTxt, int requestCase, boolean cancelable){
        ConfirmDialog mDialog = ConfirmDialog.newInstance(title, subTitle, msg, subIsHtml, msgIsHtml, abortBttnTxt, confirmBttnTxt, requestCase);
        mDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BaseDialogStyle_Large);
        mDialog.setCancelable(cancelable);
        mDialog.show(getSupportFragmentManager(), tag);
    }

    protected void checkShowConfirmDialog(String tag, String title, String subTitle, String msg, String abortBttnTxt, String confirmBttnTxt, int requestCase){
        if(!isShowingDialog(tag)){
            showConfirmDialog(tag, title, subTitle, msg, false, false, abortBttnTxt, confirmBttnTxt, requestCase, false);
        }
    }

    protected void checkShowConfirmDialog(String tag, String title, String subTitle, String msg, boolean subIsHtml, boolean msgIsHtml, int requestCase){
        if(!isShowingDialog(tag)){
            showConfirmDialog(tag, title, subTitle, msg, subIsHtml, msgIsHtml, null, null, requestCase, false);
        }
    }

    protected void checkShowConfirmDialog(String tag, String title, String subTitle, String msg, boolean subIsHtml, boolean msgIsHtml, int requestCase, boolean cancelable){
        if(!isShowingDialog(tag)){
            showConfirmDialog(tag, title, subTitle, msg, subIsHtml, msgIsHtml, null, null, requestCase, cancelable);
        }
    }

    protected boolean isShowingConfirmDialog(){
        return isShowingDialog(TAG_DIALOG_CONFIRM);
    }
    //endregion

    //region [#] Toast Message Methods
    protected void showShortToastMessage(@NonNull CharSequence msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToastMessage(@NonNull CharSequence msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void showToastMessage(@StringRes int msg, int duration){
        Toast.makeText(this, msg, duration).show();
    }

    protected void showToastMessage(@NonNull CharSequence msg, int duration){
        Toast.makeText(this, msg, duration).show();
    }
    //endregion

    //region [#] Snackbar Message Methods
    protected void showRootViewShortSnackbarMessage(@StringRes int resId){
        Snackbar.make(findViewById(android.R.id.content), resId, Snackbar.LENGTH_SHORT).show();
    }

    protected void showRootViewShortSnackbarMessage(@NonNull CharSequence msg){
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void showRootViewLongSnackbarMessage(@StringRes int resId){
        Snackbar.make(findViewById(android.R.id.content), resId, Snackbar.LENGTH_LONG).show();
    }

    protected void showRootViewLongSnackbarMessage(@NonNull CharSequence msg){
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    protected void showRootViewSnackbarMessage(@StringRes int msg, int duration){
        Snackbar.make(findViewById(android.R.id.content), msg, duration).show();
    }

    protected void showRootViewSnackbarMessage(@NonNull CharSequence msg, int duration){
        Snackbar.make(findViewById(android.R.id.content), msg, duration).show();
    }

    protected void showSnackbarMessage(@NonNull View view, @NonNull String msg, int duration){
        Snackbar.make(view, msg, duration).show();
    }

    protected void showSnackbarMessage(@NonNull String msg, int duration){
        Snackbar.make(getWindow().getDecorView().getRootView(), msg, duration).show();
    }
    //endregion
    //endregion

}
