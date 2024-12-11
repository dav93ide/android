package com.bodhitech.it.lib_base.lib_base.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.R;
import com.bodhitech.it.lib_base.lib_base.modules.utils.DateUtils;

import java.text.MessageFormat;
import java.util.Date;

public class DateTimeDialog extends DialogFragment
    implements DateUtils.IDatePickerCallback,
                DateUtils.ITimePickerCallback {

    public interface IDialogCommunication {
        void onConfirmDateTimeDialog(Date date);
        void onAbortDateTimeDialog();
    }

    private static final String TAG = DateTimeDialog.class.getSimpleName();
    private static final String KEY = TAG + ".key";
    // Argument Keys
    private static final String KEY_MASK = KEY + ".mask";
    private static final String KEY_DATETIME = KEY + ".datetime";
    private static final String KEY_MAX_DATE = KEY + ".maxDate";
    // Mask
    private static final byte MASK_DATE = 0b1;
    private static final byte MASK_TIME = MASK_DATE << 0b1;
    private static final byte MASK_DATETIME = MASK_TIME << 0b1;
    private static final byte MASK_DISMISS_ON_CONFIRM = MASK_DATETIME << 0b1;

    private LinearLayout mllDate;
    private LinearLayout mllTime;
    private LinearLayout mllDatetime;
    private EditText metDate;
    private EditText metTime;
    private EditText metDatetime;
    private Button mbConfirm;
    private Button mbAbort;

    private IDialogCommunication mCallback;
    private Date mDatetime;
    private Date mMaxDate;
    private byte mMask;

    /** Public Static Factory Methods **/
    public static DateTimeDialog newInstanceDateDialog(){
        return newInstance(MASK_DATE);
    }

    public static DateTimeDialog newInstanceTimeDialog(){
        return newInstance(MASK_TIME);
    }

    public static DateTimeDialog newInstanceDateAndTimeDialog(){
        return newInstance((byte) (MASK_DATE | MASK_TIME));
    }

    public static DateTimeDialog newInstanceDateTimeDialog(){
        return newInstance(MASK_DATETIME);
    }

    /** Private Static Factory Methods **/
    private static DateTimeDialog newInstance(byte mask){
        DateTimeDialog mInstance = new DateTimeDialog();
        Bundle args = new Bundle();
        args.putByte(KEY_MASK, mask);
        mInstance.setArguments(args);
        return mInstance;
    }

    /** Override Lifecycle Methods **/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mCallback = (IDialogCommunication) context;
        } catch (ClassCastException ccE){
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
        View view = inflater.inflate(R.layout.dialog_datetime, container, false);
        initViews(view);
        initOnCreateView();
        return view;
    }

    /** Override DateUtils.IDataPickerCallback Methods **/
    @Override
    public void onDateSelected(int requestCode, String date, EditText editText) {
        editText.setText(date);
        if((mMask & MASK_DATETIME) != 0x0){
            DateUtils.showTimePicker(getContext(), metDatetime, mDatetime, this);
        }
    }

    /** Override DateUtils.ITimePickerCallback Methods **/
    @Override
    public void onTimeSelected(int requestCode, String time, EditText editText) {
        editText.setText(MessageFormat.format("{0} {1}", editText.getText(), time));
    }

    /** Public Methods **//** Builder Methods **/
    public DateTimeDialog setDismissOnConfirm(){
        mMask |= MASK_DISMISS_ON_CONFIRM;
        return this;
    }

    public DateTimeDialog setDatetime(Date datetime){
        Bundle args = getArguments() != null ? getArguments() : new Bundle();
        args.putSerializable(KEY_DATETIME, datetime);
        setArguments(args);
        return this;
    }

    public DateTimeDialog setMaxDate(Date date){
        Bundle args = getArguments() != null ? getArguments() : new Bundle();
        args.putSerializable(KEY_MAX_DATE, date);
        setArguments(args);
        return this;
    }

    /** Private Methods **/
    private void initOnCreate(){
        if(getArguments() != null) {
            mMask = getArguments().getByte(KEY_MASK, MASK_DATETIME);
            mDatetime = getArguments().containsKey(KEY_DATETIME) ? (Date) getArguments().getSerializable(KEY_DATETIME) : new Date();
            mMaxDate = getArguments().containsKey(KEY_MAX_DATE) ? (Date) getArguments().getSerializable(KEY_MAX_DATE) : null;
        } else {
            mMask = MASK_DATETIME;
            mDatetime = new Date();
        }
    }

    private void initViews(@NonNull View view){
        mllDate = view.findViewById(R.id.ll_date);
        mllTime = view.findViewById(R.id.ll_time);
        mllDatetime = view.findViewById(R.id.ll_datetime);
        metDate = view.findViewById(R.id.et_date);
        metTime = view.findViewById(R.id.et_time);
        metDatetime = view.findViewById(R.id.et_datetime);
        mbConfirm = view.findViewById(R.id.bttn_confirm);
        mbAbort = view.findViewById(R.id.bttn_abort);
    }

    private void initOnCreateView(){
        initBttnsListeners();
        initDateView();
        initTimeView();
        initDatetimeView();
        initConfirmButtonText();
        initDialogTitle();
    }

    private void initBttnsListeners(){
        mbConfirm.setOnClickListener(this::onClickInsert);
        mbAbort.setOnClickListener(this::onClickAbort);
    }

    protected void onClickInsert(View v){
        if(mCallback != null){
            Date date = getDate();
            if(date != null && (mMaxDate == null || date.getTime() <= mMaxDate.getTime())){
                mCallback.onConfirmDateTimeDialog(date);
                if((mMask & MASK_DISMISS_ON_CONFIRM) != 0x0){
                    dismiss();
                }
            } else if(getContext() != null){
                Toast.makeText(getContext(), R.string.error_wrong_date_or_bigger_than_max_date, Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onClickAbort(View v){
        if(mCallback != null){
            mCallback.onAbortDateTimeDialog();
        }
        dismiss();
    }

    private void initDateView(){
        if((mMask & MASK_DATE) != 0x0){
            mllDate.setVisibility(LinearLayout.VISIBLE);
            metDate.setText(DateUtils.getStringFromDate(mDatetime, BaseConstants.DATE_FORMAT));
            metDate.setOnClickListener(v -> showDatePicker(metDate));
            metDate.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){
                    showDatePicker(metDate);
                }
            });
        } else {
            mllDate.setVisibility(LinearLayout.GONE);
        }
    }

    private void initTimeView(){
        if((mMask & MASK_TIME) != 0x0){
            mllTime.setVisibility(LinearLayout.VISIBLE);
            metTime.setText(DateUtils.getStringFromDate(mDatetime, BaseConstants.TIME_FORMAT_NO_SECONDS));
            metTime.setOnClickListener(v -> showTimePicker(metTime));
            metTime.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){
                    showTimePicker(metTime);
                }
            });
        } else {
            mllTime.setVisibility(LinearLayout.GONE);
        }
    }

    private void initDatetimeView(){
        if((mMask & MASK_DATETIME) != 0x0){
            mllDatetime.setVisibility(LinearLayout.VISIBLE);
            metDatetime.setText(DateUtils.getStringFromDate(mDatetime, BaseConstants.DATETIME_FORMAT_NO_SECONDS));
            metDatetime.setOnClickListener(v -> showDatePicker(metDatetime));
            metDatetime.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){
                    showDatePicker(metDatetime);
                }
            });
        } else {
            mllDatetime.setVisibility(LinearLayout.GONE);
        }
    }

    private void initDialogTitle(){
        if(getDialog() != null){
            if((mMask & MASK_DATETIME) != 0x0 || ((mMask & MASK_DATE) != 0x0 && (mMask & MASK_TIME) != 0x0)){
                getDialog().setTitle(R.string.title_set_date_and_time);
            } else if((mMask & MASK_DATE) != 0x0){
                getDialog().setTitle(R.string.title_set_date);
            } else {
                getDialog().setTitle(R.string.title_set_time);
            }
        }
    }

    private void initConfirmButtonText(){
        mbConfirm.setText(getString(R.string.label_insert));
    }

    /** Date & Time Pickers Methods **/
    private void showDatePicker(EditText et){
        et.setText("");
        et.setEnabled(false);
        DateUtils.showDatePicker(getContext(), et, mDatetime, this);
        et.setEnabled(true);
    }

    private void showTimePicker(EditText et){
        et.setText("");
        et.setEnabled(false);
        DateUtils.showTimePicker(getContext(), et, mDatetime, this);
        et.setEnabled(true);
    }

    /** Datetime Methods **/
    private @Nullable Date getDate(){
        String ret;
        if((mMask & MASK_DATETIME) != 0x0){
            ret = metDatetime.getText().toString();
            return !TextUtils.isEmpty(ret) ? DateUtils.getDateFromString(ret, BaseConstants.DATETIME_FORMAT_NO_SECONDS) : null;
        } else if((mMask & MASK_DATE) != 0x0 && (mMask & MASK_TIME) != 0x0){
            ret = String.format("%1$s %2$s", metDate.getText().toString(), metTime.getText().toString());
            return !TextUtils.isEmpty(metDate.getText().toString()) && !TextUtils.isEmpty(metTime.getText().toString()) ?
                    DateUtils.getDateFromString(ret, BaseConstants.DATETIME_FORMAT_NO_SECONDS) : null;
        } else if((mMask & MASK_DATE) != 0x0){
            ret = metDate.getText().toString();
            return !TextUtils.isEmpty(ret) ? DateUtils.getDateFromString(ret, BaseConstants.DATE_FORMAT) : null;
        } else {
            ret = metTime.getText().toString();
            return !TextUtils.isEmpty(ret) ? DateUtils.getDateFromString(ret, BaseConstants.TIME_FORMAT_NO_SECONDS) : null;
        }
    }

}
