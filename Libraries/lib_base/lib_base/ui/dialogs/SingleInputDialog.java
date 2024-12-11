package com.bodhitech.it.lib_base.lib_base.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.R;

public class SingleInputDialog extends DialogFragment {

    public interface IDialogCommunication {
        void sidOnAbort();
        void sidOnInsert(String inputText);
    }

    private static final String TAG = SingleInputDialog.class.getSimpleName();
    private static final String KEY = TAG + ".key";
    // Keys
    private static final String KEY_TITLE = KEY + ".title";
    private static final String KEY_INPUT_HINT = KEY + ".inputHint";
    private static final String KEY_INPUT_TYPE = KEY + ".inputType";
    private static final String KEY_CONFIRM_BTTN_TEXT = KEY + ".confirmButtonText";
    private static final String KEY_REQUEST_CODE = KEY + ".requestCode";
    private static final String KEY_MASK = KEY + ".mask";
    private static final String KEY_ET_TEXT = KEY + ".etText";
    // Mask Values
    public static final short MASK_ET_MULTI_LINE = 0b1;

    private EditText metInput;
    private Button mbAbort;
    private Button mbInsert;

    private IDialogCommunication mCallback;
    private String mTitle;
    private String mHint;
    private short mMask;
    private int mInputType;
    private int mRequestCode;

    public static SingleInputDialog newInstance(String title, String etHint, int etInputType){
        SingleInputDialog mInstance = new SingleInputDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_INPUT_HINT, etHint);
        args.putInt(KEY_INPUT_TYPE, etInputType);
        mInstance.setArguments(args);
        return mInstance;
    }

    public static SingleInputDialog newInstance(String title, String etHint, int etInputType, short mask){
        SingleInputDialog mInstance = new SingleInputDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_INPUT_HINT, etHint);
        args.putInt(KEY_INPUT_TYPE, etInputType);
        args.putShort(KEY_MASK, mask);
        mInstance.setArguments(args);
        return mInstance;
    }

    public static SingleInputDialog newInstance(String title, String etHint, int etInputType, int requestCode){
        SingleInputDialog mInstance = new SingleInputDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_INPUT_HINT, etHint);
        args.putInt(KEY_INPUT_TYPE, etInputType);
        args.putInt(KEY_REQUEST_CODE, requestCode);
        mInstance.setArguments(args);
        return mInstance;
    }

    public static SingleInputDialog newInstance(String title, String etHint, int etInputType, short mask, int requestCode){
        SingleInputDialog mInstance = new SingleInputDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_INPUT_HINT, etHint);
        args.putInt(KEY_INPUT_TYPE, etInputType);
        args.putShort(KEY_MASK, mask);
        args.putInt(KEY_REQUEST_CODE, requestCode);
        mInstance.setArguments(args);
        return mInstance;
    }

    public static SingleInputDialog newInstance(String title, String etHint, int etInputType, String confirmBttnText){
        SingleInputDialog mInstance = new SingleInputDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_INPUT_HINT, etHint);
        args.putInt(KEY_INPUT_TYPE, etInputType);
        args.putString(KEY_CONFIRM_BTTN_TEXT, confirmBttnText);
        mInstance.setArguments(args);
        return mInstance;
    }

    public static SingleInputDialog newInstance(String title, String etHint, int etInputType, short mask, String confirmBttnText){
        SingleInputDialog mInstance = new SingleInputDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_INPUT_HINT, etHint);
        args.putInt(KEY_INPUT_TYPE, etInputType);
        args.putShort(KEY_MASK, mask);
        args.putString(KEY_CONFIRM_BTTN_TEXT, confirmBttnText);
        mInstance.setArguments(args);
        return mInstance;
    }

    public static SingleInputDialog newInstance(String title, String etHint, int etInputType, String confirmBttnText, int requestCode){
        SingleInputDialog mInstance = new SingleInputDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_INPUT_HINT, etHint);
        args.putInt(KEY_INPUT_TYPE, etInputType);
        args.putString(KEY_CONFIRM_BTTN_TEXT, confirmBttnText);
        args.putInt(KEY_REQUEST_CODE, requestCode);
        mInstance.setArguments(args);
        return mInstance;
    }

    public static SingleInputDialog newInstance(String title, String etHint, int etInputType, short mask, String confirmBttnText, int requestCode){
        SingleInputDialog mInstance = new SingleInputDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_INPUT_HINT, etHint);
        args.putInt(KEY_INPUT_TYPE, etInputType);
        args.putShort(KEY_MASK, mask);
        args.putString(KEY_CONFIRM_BTTN_TEXT, confirmBttnText);
        args.putInt(KEY_REQUEST_CODE, requestCode);
        mInstance.setArguments(args);
        return mInstance;
    }

    public static SingleInputDialog newInstance(String title, String etHint, int etInputType, short mask, String confirmBttnText, int requestCode, String etText){
        SingleInputDialog mInstance = new SingleInputDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_INPUT_HINT, etHint);
        args.putInt(KEY_INPUT_TYPE, etInputType);
        args.putShort(KEY_MASK, mask);
        args.putString(KEY_CONFIRM_BTTN_TEXT, confirmBttnText);
        args.putInt(KEY_REQUEST_CODE, requestCode);
        args.putString(KEY_ET_TEXT, etText);
        mInstance.setArguments(args);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_single_input, container, false);
        initViews(view);
        initOnCreateView();
        return view;
    }

    /** Public Methods **/
    public void setInputError(String error){
        if(metInput != null){
            metInput.setError(error);
        }
    }

    public void setInputValue(String txt){
        if(metInput != null){
            metInput.setText(txt);
        }
    }

    /** Private Methods **/
    private void initOnCreate(){
        if(getArguments() != null){
            initData();
        }
    }

    private void initData(){
        initTitle();
        initHint();
        initInputType();
        initMask();
        initRequestCode();
    }

    private void initTitle(){
        if(getArguments().containsKey(KEY_TITLE)){
            mTitle = getArguments().getString(KEY_TITLE);
        }
    }

    private void initHint(){
        if(getArguments().containsKey(KEY_INPUT_HINT)){
            mHint = getArguments().getString(KEY_INPUT_HINT);
        }
    }

    private void initInputType(){
        if(getArguments().containsKey(KEY_INPUT_TYPE)){
            mInputType = getArguments().getInt(KEY_INPUT_TYPE, InputType.TYPE_NULL);
        }
    }

    private void initMask(){
        if(getArguments().containsKey(KEY_MASK)){
            mMask = getArguments().getShort(KEY_MASK);
        }
    }

    private void initRequestCode(){
        mRequestCode = getArguments().containsKey(KEY_REQUEST_CODE) ? getArguments().getInt(KEY_REQUEST_CODE, 0x0) : 0x0;
    }

    private void initOnCreateView(){
        initButtonsOnClickListeners();
        initInputET();
        initTextConfirmButton();
        if(getDialog() != null){
            getDialog().setTitle(mTitle);
        }
    }

    private void initViews(View v){
        if(metInput == null){
            metInput = v.findViewById(R.id.et_input);
        }
        if(mbAbort == null){
            mbAbort = v.findViewById(R.id.bttn_abort);
        }
        if(mbInsert == null){
            mbInsert = v.findViewById(R.id.bttn_confirm);
        }
    }

    private void initButtonsOnClickListeners(){
        mbAbort.setOnClickListener(v -> {
            if(mCallback != null){
                mCallback.sidOnAbort();
            }
            dismiss();
        });
        mbInsert.setOnClickListener(v -> {
            if(mCallback != null && metInput != null){
                mCallback.sidOnInsert(metInput.getText().toString());
            }
        });
    }

    private void initInputET(){
        if(metInput != null){
            metInput.setHint(mHint);
            metInput.setInputType(mInputType);
            if((mMask & MASK_ET_MULTI_LINE) != 0x0){
                metInput.setSingleLine(false);
                metInput.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                metInput.setVerticalScrollBarEnabled(true);
                metInput.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                metInput.setMinLines(0x5);
                metInput.setMaxLines(0x14);
            }
            if(getArguments() != null && getArguments().containsKey(KEY_ET_TEXT)){
                metInput.setText(getArguments().getString(KEY_ET_TEXT));
            }
        }
    }

    private void initTextConfirmButton(){
        if(mbInsert != null){
            if(getArguments() != null && getArguments().containsKey(KEY_CONFIRM_BTTN_TEXT)){
                mbInsert.setText(getArguments().getString(KEY_CONFIRM_BTTN_TEXT));
            } else {
                mbInsert.setText(R.string.label_insert);
            }
        }
    }

}
