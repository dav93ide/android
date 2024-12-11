package com.bodhitech.it.lib_base.lib_base.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.R;

public abstract class BaseViewPagerActivity extends BaseAppCompatActivity {

    protected Toolbar mToolbar;
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;

    protected ProgressDialog mProgressDialog;

    /** Abstract Methods **/
    protected abstract void initViewPager();

    /** Override Lifecycle Methods **/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_viewpager);
        initOnCreate();
    }

    /** Protected Methods **/
    protected void initOnCreate(){
        initBaseViews();
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initProgressDialog();
        initViewPager();
    }

    protected void callBaseOnCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    /** Fragment & Tabs Methods **/
    protected Fragment getSelectedTabFragment(){
        return getSupportFragmentManager().findFragmentByTag(getTagFragmentAt(mTabLayout.getSelectedTabPosition()));
    }

    protected <T extends Fragment> T getFragmentAt(int position){
        return (T) getSupportFragmentManager().findFragmentByTag(getTagFragmentAt(position));
    }

    protected String getTagFragmentAt(int position){
        return String.format(BaseConstants.TAG_VIEWPAGER_FRAGMENTS, mViewPager.getId(), position);
    }

    /** Fragment Tag Methods **/
    protected String getFragmentTagFromFullTabTag(String fragTag){
        String[] splitted = fragTag.split("[:]");
        return splitted[splitted.length - 0x1];
    }

    protected int getIntegerFragmentTagFromFullTabTag(String fragTag){
        return Integer.parseInt(getFragmentTagFromFullTabTag(fragTag));
    }

    protected void dismissDialog(String tag){
        DialogFragment dialog = getDialog(tag);
        if(dialog != null){
            dialog.dismiss();
        }
    }

    /** Messages Methods **//** Success Message Methods **/
    protected void showMessageSuccessDownload(int arg){
        showMessageSuccessDownload(getString(arg));
    }

    protected void showMessageSuccessDownload(String arg){
        Toast.makeText(this, getString(R.string.success_downloaded_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageSuccessUpload(String arg){
        Toast.makeText(this, getString(R.string.success_uploaded_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageSuccessOperation(){
        Toast.makeText(this, getString(R.string.success_operation), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageSuccessDelete(String arg){
        Toast.makeText(this, getString(R.string.success_deleted_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageSuccessCheck(String arg){
        Toast.makeText(this, getString(R.string.success_checking_element, arg), Toast.LENGTH_SHORT).show();
    }

    /** Error Message Methods **/
    protected void showMessageErrorDownload(int idRes){
        showMessageErrorDownload(getString(idRes));
    }

    protected void showMessageErrorDownload(String arg){
        Toast.makeText(this, getString(R.string.error_downloading_element, arg), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorSave(String arg){
        Toast.makeText(this, getString(R.string.error_saving_element, arg), Toast.LENGTH_SHORT).show();
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

    protected void showMessageErrorOperation(){
        Toast.makeText(this, getString(R.string.error_operation), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorParams(){
        Toast.makeText(this, getString(R.string.error_inserted_data), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorNotFoundF(int idRes){
        Toast.makeText(this, getString(R.string.label_element_not_found_f, getString(idRes)), Toast.LENGTH_SHORT).show();
    }

    protected void showMessageErrorNotFoundF(String arg){
        Toast.makeText(this, getString(R.string.label_element_not_found_f, arg), Toast.LENGTH_SHORT).show();
    }

    /** Progress Dialog Methods **/
    protected void dismissProgressDialog(){
        mProgressDialog.dismiss();
    }

    protected void showProgressDialog(){
        mProgressDialog.show();
    }

    protected boolean isShowingProgressDialog(){
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    protected void setTextProgressDialog(String title, String msg){
        if(mProgressDialog != null){
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(msg);
        }
    }

    protected void setMsgProgressDialog(String msg){
        if(mProgressDialog != null){
            mProgressDialog.setMessage(msg);
        }
    }

    /** Private Methods **//** Init Methods **/
    private void initProgressDialog(){
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
    }

    private void initBaseViews(){
        if(mToolbar == null){
            mToolbar = findViewById(R.id.toolbar);
        }
        if(mTabLayout == null){
            mTabLayout = findViewById(R.id.tab_layout);
        }
        if(mViewPager == null){
            mViewPager = findViewById(R.id.view_pager);
        }
    }

}
