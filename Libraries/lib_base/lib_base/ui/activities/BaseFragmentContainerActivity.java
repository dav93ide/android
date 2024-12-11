package com.bodhitech.it.lib_base.lib_base.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.R;
import com.bodhitech.it.lib_base.lib_base.modules.utils.DataUtils;
import com.bodhitech.it.lib_base.lib_base.ui.fragments.BaseRefreshableListFragment;

public abstract class BaseFragmentContainerActivity extends BaseAppCompatActivity {

    private static final String TAG = BaseFragmentContainerActivity.class.getSimpleName();
    private static final String TAG_FRAGMENT = TAG + ".fragment";
    // Fragments Tags
    private static final String TAG_FRAGMENT_LIST = TAG_FRAGMENT + ".list";

    protected Toolbar mToolbar;
    protected FrameLayout mflFragmentContainer;

    protected ProgressDialog mProgressDialog;

    //region [#] Abstract Methods
    protected abstract <T extends Fragment> T getInstanceListFragment();            // TODO: rinominare in getInstanceFragment()
    //endregion

    //region [#] Override Lifecycle Methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_fragment_container);
        initOnCreate();
    }

    @Override
    public void onBackPressed() {
        if(isShowingListFragment()){
            finish();
        } else {
            super.onBackPressed();
        }
    }
    //endregion

    //region [#] Override Actionbar Listener Methods
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region [#] Protected Methods
    protected void initOnCreate(){
        initBaseViews();
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initProgressDialog();
        setListFragment();
    }

    protected void callBaseOnCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    //region [#] Init Methods
    protected void initProgressDialog(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
    }

    protected void initBaseViews(){
        mToolbar = findViewById(R.id.toolbar);
        mflFragmentContainer = findViewById(R.id.fl_fragment_container);
    }
    //endregion

    //region [#] Fragment Methods
    protected <T extends Fragment> T getFragmentOnContainer(){
        return getFragment(mflFragmentContainer.getId());
    }

    protected <T extends Fragment> T getFragmentOnContainer(String tag){
        T ret = null;
        Fragment mFragment = getFragment(mflFragmentContainer.getId());
        if(mFragment != null && !TextUtils.isEmpty(mFragment.getTag()) && mFragment.getTag().equals(tag)){
            ret = (T) mFragment;
        }
        return ret;
    }

    protected <T extends Fragment> T getFragment(String tag){
        return (T) getSupportFragmentManager().findFragmentByTag(tag);
    }

    protected <T extends Fragment> T getFragment(int id){
        return (T) getSupportFragmentManager().findFragmentById(id);
    }

    protected void restoreFragment(String tag){
        getSupportFragmentManager().popBackStackImmediate(tag, 0x0);
    }
    //endregion

    //region [#] List Fragment Methods
    protected <T extends Fragment> void setListFragment(){
        setFragment(getInstanceListFragment(), mflFragmentContainer.getId(), TAG_FRAGMENT_LIST);
    }

    protected <T extends Fragment> T getListFragment(){
        return getFragmentOnContainer(TAG_FRAGMENT_LIST);
    }

    protected void restoreListFragment(){
        restoreFragment(TAG_FRAGMENT_LIST);
    }

    protected boolean isShowingListFragment(){
        return getListFragment() != null;
    }
    //endregion

    //region [#] Toast Success Message Methods
    protected void showMessageSuccessOperation(){
        Toast.makeText(this, getString(R.string.success_operation), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageSuccessDownload(int arg){
        showMessageSuccessDownload(getString(arg));
    }

    protected void showMessageSuccessDownload(String arg){
        Toast.makeText(this, getString(R.string.success_downloaded_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageSuccessUpload(String arg){
        Toast.makeText(this, getString(R.string.success_uploaded_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageSuccessDelete(String arg){
        Toast.makeText(this, getString(R.string.success_deleted_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageSuccessCheck(String arg){
        Toast.makeText(this, getString(R.string.success_checking_element, arg), Toast.LENGTH_SHORT).show();
    }
    //endregion

    //region [#] Toast Error Message Methods
    protected void showMessageErrorOperation(){
        Toast.makeText(this, getString(R.string.error_operation), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorParams(){
        Toast.makeText(this, getString(R.string.error_inserted_data), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorDownload(int idRes){
        showMessageErrorDownload(getString(idRes));
    }

    protected void showMessageErrorDownload(String arg){
        Toast.makeText(this, getString(R.string.error_downloading_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorUpload(int idRes){
        showMessageErrorUpload(getString(idRes));
    }

    protected void showMessageErrorUpload(String arg){
        Toast.makeText(this, getString(R.string.error_cannot_upload_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorDelete(int idRes){
        showMessageErrorDelete(getString(idRes));
    }

    protected void showMessageErrorDelete(String arg){
        Toast.makeText(this, getString(R.string.error_delete_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorCheck(int idRes){
        showMessageErrorCheck(getString(idRes));
    }

    protected void showMessageErrorCheck(String arg){
        Toast.makeText(this, getString(R.string.error_checking_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorInsert(String arg){
        Toast.makeText(this, getString(R.string.error_insert_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorNotFoundF(int idRes){
        Toast.makeText(this, getString(R.string.label_element_not_found_f, getString(idRes)), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorNotFoundF(String arg){
        Toast.makeText(this, getString(R.string.label_element_not_found_f, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorSendF(String arg){
        Toast.makeText(this, getString(R.string.error_send_element_f, arg), Toast.LENGTH_SHORT).show();
    }
    //endregion
    //endregion

    //region [#] Snackbar Messages Methods
    //region [#] Snackbar Success Messages Methods
    protected void showSnackbarSuccessDownload(@StringRes int id){
        showRootViewShortSnackbarMessage(getString(R.string.success_downloaded_element, getString(id)));
    }

    protected void showSnackbarSuccessDownload(@NonNull String arg){
        showRootViewShortSnackbarMessage(getString(R.string.success_downloaded_element, arg));
    }

    protected void showSnackbarSuccessOperation(){
        showRootViewShortSnackbarMessage(getString(R.string.success_operation));
    }
    //endregion

    //region [#] Snackbar Error Messages Methods
    protected void showSnackbarErrorDownload(@StringRes int id){
        showRootViewShortSnackbarMessage(getString(R.string.error_download_element, getString(id)));
    }

    protected void showSnackbarErrorDownload(@NonNull String arg){
        showRootViewShortSnackbarMessage(getString(R.string.error_download_element, arg));
    }

    protected void showSnackbarErrorOperation(){
        showRootViewShortSnackbarMessage(getString(R.string.error_operation));
    }
    //endregion
    //endregion

    //region [#] ProgressDialog Methods
    protected void dismissProgressDialog(){
        if (isShowingProgressDialog()) {
            try {
                mProgressDialog.dismiss();
            } catch (IllegalArgumentException iaE) {
                BaseEnvironment.onExceptionLevelLow(TAG, iaE);
            } catch (RuntimeException rE) {
                BaseEnvironment.onExceptionLevelLow(TAG, rE);
            } catch (Exception e) {
                BaseEnvironment.onExceptionLevelLow(TAG, e);
            }
        }
    }

    protected void showProgressDialog(){
        if(mProgressDialog == null){
            initProgressDialog();
        }
        if(!mProgressDialog.isShowing()){
            try {
                mProgressDialog.show();
            } catch (IllegalArgumentException iaE) {
                BaseEnvironment.onExceptionLevelLow(TAG, iaE);
            } catch (RuntimeException rE) {
                BaseEnvironment.onExceptionLevelLow(TAG, rE);
            } catch (Exception e) {
                BaseEnvironment.onExceptionLevelLow(TAG, e);
            }
        }
    }

    protected boolean isShowingProgressDialog(){
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    protected void setTextProgressDialog(int titleRes, int msgRes, @NonNull int... msgResArgs){
        mProgressDialog.setTitle(titleRes);
        String[] args = new String[msgResArgs.length];
        for(int i = 0x0; i < msgResArgs.length; i++){
            args[i] = getString(msgResArgs[i]);
        }
        mProgressDialog.setMessage(getString(msgRes, args));
    }

    protected void setTextProgressDialog(int titleRes, int msgRes){
        mProgressDialog.setTitle(titleRes);
        mProgressDialog.setMessage(getString(msgRes));
    }

    protected void setTextProgressDialog(String title, String msg){
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(msg);
    }

    @NonNull
    protected String getMessageProgressDialog(){
        TextView pd = (TextView) DataUtils.getValueOfField(mProgressDialog, BaseConstants.FIELD_PROGRESS_DIALOG_TV_MESSAGEVIEW);
        return pd != null ? pd.getText().toString() : "";
    }

    protected void setCancelableProgressDialog(boolean isCancelable){
        if(mProgressDialog != null){
            mProgressDialog.setCancelable(isCancelable);
        }
    }

    protected void hideLoadingAndRefreshing(){
        hideRefreshing();
        dismissProgressDialog();
    }

    protected void hideRefreshing(){
        if(getFragment(mflFragmentContainer.getId()) instanceof BaseRefreshableListFragment){
            BaseRefreshableListFragment mFragment = getFragment(mflFragmentContainer.getId());
            if (mFragment.isRefreshing()) {
                mFragment.setRefreshing(false);
            }
        }
    }
    //endregion
    //endregion

}
