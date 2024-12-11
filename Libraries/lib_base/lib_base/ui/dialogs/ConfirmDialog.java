package com.bodhitech.it.lib_base.lib_base.ui.dialogs;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.R;

public class ConfirmDialog extends DialogFragment {

    public interface IDialogCommunication {
        void cdOnConfirm(int requestCode);
        void cdOnAbort(int requestCode);
    }

    private static final String TAG = ConfirmDialog.class.getSimpleName();
    private static final String KEY = TAG + ".key";
    // Keys
    private static final String KEY_TEXTS = KEY + ".texts";
    private static final String KEY_IS_HTML = KEY + ".isHtml";
    private static final String KEY_BTTNS_TEXTS = KEY + ".buttonsTexts";
    private static final String KEY_REQUEST_CODE = KEY + ".requestCode";
    // Texts Indexes
    private static final int INDEX_TITLE = 0x0;
    private static final int INDEX_SUBTITLE = 0x1;
    private static final int INDEX_MSG = 0x2;
    // HTML Indexes
    private static final int INDEX_IS_SUBTITLE_HTML = 0x0;
    private static final int INDEX_IS_MSG_HTML = 0x1;
    // Bttn Texts Indexes
    private static final int INDEX_ABORT_BTTN_TEXT = 0x0;
    private static final int INDEX_CONFIRM_BTTN_TEXT = 0x1;

    private LinearLayout mllMessageContainer;
    private TextView mtvSubtitle;
    private TextView mtvMessage;
    private Button mbAbort;
    private Button mbConfirm;

    private IDialogCommunication mCallback;
    private int mRequestCode;

    public static ConfirmDialog newInstance(String title, String subTitle, String msg, boolean subIsHTML, boolean msgIsHTML, int requestCase){
        ConfirmDialog mInstance = new ConfirmDialog();
        mInstance.setArguments(initBundleArguments(title, subTitle, msg, subIsHTML, msgIsHTML, requestCase));
        return mInstance;
    }

    public static ConfirmDialog newInstance(String title, String subTitle, String msg, int requestCase){
        return newInstance(title, subTitle, msg, false, false, requestCase);
    }

    public static ConfirmDialog newInstance(String title, String subTitle, String msg, boolean subIsHTML, boolean msgIsHTML, String abortButtonText, String confirmButtonText, int requestCase){
        ConfirmDialog mInstance = new ConfirmDialog();
        Bundle data = initBundleArguments(title, subTitle, msg, subIsHTML, msgIsHTML, requestCase);
        data.putStringArray(KEY_BTTNS_TEXTS, new String[]{abortButtonText, confirmButtonText});
        mInstance.setArguments(data);
        return mInstance;
    }

    public static ConfirmDialog newInstance(String title, String subTitle, String msg, String abortButtonText, String confirmButtonText, int requestCase){
        ConfirmDialog mInstance = new ConfirmDialog();
        Bundle data = initBundleArguments(title, subTitle, msg, false, false, requestCase);
        data.putStringArray(KEY_BTTNS_TEXTS, new String[]{abortButtonText, confirmButtonText});
        mInstance.setArguments(data);
        return mInstance;
    }

    /** Override Lifecycle Methods **/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mCallback = (IDialogCommunication) context;
        } catch(ClassCastException ccE){
            BaseEnvironment.onExceptionLevelLow(TAG, ccE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_base_confirm_txt_bttns, container, false);
        initViews(view);
        initOnCreateView(view);
        return view;
    }

    /** Private Methods **//** Static Methods **/
    private static Bundle initBundleArguments(String title, String subTitle, String msg, boolean subIsHTML, boolean msgIsHTML, int requestCase){
        Bundle data = new Bundle();
        data.putStringArray(KEY_TEXTS, new String[]{title, subTitle, msg});
        data.putBooleanArray(KEY_IS_HTML, new boolean[]{subIsHTML, msgIsHTML});
        data.putInt(KEY_REQUEST_CODE, requestCase);
        return data;
    }

    /** Init Methods **/
    private void initViews(View v){
        if(mllMessageContainer == null){
            mllMessageContainer = v.findViewById(R.id.ll_message_container);
        }
        if(mtvSubtitle == null){
            mtvSubtitle = v.findViewById(R.id.tv_subtitle);
        }
        if(mtvMessage == null){
            mtvMessage = v.findViewById(R.id.tv_message);
        }
        if(mbAbort == null){
            mbAbort = v.findViewById(R.id.bttn_abort);
        }
        if(mbConfirm == null){
            mbConfirm = v.findViewById(R.id.bttn_confirm);
        }
    }

    private void initOnCreateView(View view){
        initAll();
        initTexts();
        initBttns(view);
    }

    private void initButtonsOnClickListeners(){
        mbAbort.setOnClickListener(v -> {
            if(mCallback != null) {
                mCallback.cdOnAbort(mRequestCode);
            }
            dismiss();
        });
        mbConfirm.setOnClickListener(v -> {
            if(mCallback != null) {
                mCallback.cdOnConfirm(mRequestCode);
            }
            dismiss();
        });
    }

    private void initAll(){
        if(getArguments() != null) {
            mRequestCode = getArguments().containsKey(KEY_REQUEST_CODE) ? getArguments().getInt(KEY_REQUEST_CODE, 0x0) : 0x0;
        }
    }

    private void initTexts(){
        String[] text = getArguments().containsKey(KEY_TEXTS) ? getArguments().getStringArray(KEY_TEXTS) : null;
        if(text != null){
            boolean[] isHTML = getArguments().containsKey(KEY_IS_HTML) ? getArguments().getBooleanArray(KEY_IS_HTML) : new boolean[]{false, false};
            if(!TextUtils.isEmpty(text[INDEX_TITLE])) {
                getDialog().setTitle(text[INDEX_TITLE]);
            }
            if(!TextUtils.isEmpty(text[INDEX_SUBTITLE])) {
                if(isHTML[INDEX_IS_SUBTITLE_HTML]){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mtvSubtitle.setText(Html.fromHtml(text[INDEX_SUBTITLE], Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        mtvSubtitle.setText(Html.fromHtml(text[INDEX_SUBTITLE]));
                    }
                } else {
                    mtvSubtitle.setText(text[INDEX_SUBTITLE]);
                }
            }
            if(!TextUtils.isEmpty(text[INDEX_MSG])) {
                if(isHTML[INDEX_IS_MSG_HTML]){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mtvMessage.setText(Html.fromHtml(text[INDEX_MSG], Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        mtvMessage.setText(Html.fromHtml(text[INDEX_MSG]));
                    }
                } else {
                    mtvMessage.setText(text[INDEX_MSG]);
                }
                mtvMessage.setMovementMethod(new ScrollingMovementMethod());
                setStrokeColor();
            } else {
                mllMessageContainer.setVisibility(View.GONE);
            }
        }
    }

    private void initBttns(View view){
        String[] bttnsText = getArguments().containsKey(KEY_BTTNS_TEXTS) ? getArguments().getStringArray(KEY_BTTNS_TEXTS) : null;
        if(bttnsText != null){
            if(!TextUtils.isEmpty(bttnsText[INDEX_ABORT_BTTN_TEXT])){
                mbAbort.setText(bttnsText[INDEX_ABORT_BTTN_TEXT]);
            }
            if(!TextUtils.isEmpty(bttnsText[INDEX_CONFIRM_BTTN_TEXT])){
                mbConfirm.setText(bttnsText[INDEX_CONFIRM_BTTN_TEXT]);
            }
        }
        initButtonsOnClickListeners();
    }

    private void setStrokeColor(){
        ((GradientDrawable) mllMessageContainer.getBackground().getCurrent()).setStroke(getResources().getDimensionPixelSize(R.dimen.stroke_3dp),
            getResources().getColor(R.color.colorAccent));
    }

}
