package com.bodhitech.it.lib_base.lib_base.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.R;
import com.bodhitech.it.lib_base.lib_base.modules.utils.FilesUtils;

public class PdfViewerDialog extends DialogFragment
    implements  OnErrorListener,
                OnPageErrorListener {

    public interface IDialogCommunication{
        void pvdStoreFile(Uri uri, String filepath);
        void pvdOnClose();
    }

    private static final String TAG = PdfViewerDialog.class.getSimpleName();
    private static final String KEY = TAG + ".key";
    // Data
    private static final String KEY_PDF_URI = KEY + ".pdf";
    private static final String KEY_TITLE = KEY + ".title";
    private static final String KEY_FILEPATH = KEY + ".filepath";
    private static final String KEY_IS_TEMP_FILE = KEY + ".filename";

    private TextView mtvTitle;
    private ImageButton mibSave;
    private Button mbExit;
    private PDFView mPDFView;

    private IDialogCommunication mCallback;
    private Uri mPdfUri;
    private String mFilepath;
    private boolean mIsTempFile;

    public static PdfViewerDialog initInstance(Uri uri, String title, String filepath, boolean isTempFile){
        PdfViewerDialog mInstance = new PdfViewerDialog();
        Bundle data = new Bundle();
        data.putParcelable(KEY_PDF_URI, uri);
        data.putString(KEY_TITLE, title);
        data.putString(KEY_FILEPATH, filepath);
        data.putBoolean(KEY_IS_TEMP_FILE, isTempFile);
        mInstance.setArguments(data);
        return mInstance;
    }

    public static PdfViewerDialog initInstance(Uri uri, String title){
        PdfViewerDialog mInstance = new PdfViewerDialog();
        Bundle data = new Bundle();
        data.putParcelable(KEY_PDF_URI, uri);
        data.putString(KEY_TITLE, title);
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
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_base_pdf, container, false);
        initViews(view);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BaseDialogStyle);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(!initData()){
            dismiss();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        initPdf();
        showPdf();
        checkSave();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if(mIsTempFile) {
            FilesUtils.removeFile(mPdfUri.getPath());
        }
        super.onDismiss(dialog);
    }

    /** Override PDFViewer Callback Methods **/
    @Override
    public void onError(Throwable t) {
        showErrorMessage();
        BaseEnvironment.onExceptionLevelLow(TAG, new Exception(t));
        dismiss();
    }

    @Override
    public void onPageError(int page, Throwable t) {
        showErrorMessage();
        BaseEnvironment.onExceptionLevelLow(TAG, new Exception(t));
        dismiss();
    }

    /** Private Methods **//** Init Methods **/
    private void initViews(View view){
        if(mtvTitle == null){
            mtvTitle = view.findViewById(R.id.tv_my_title);
        }
        if(mibSave == null){
            mibSave = view.findViewById(R.id.bttn_save);
            mibSave.setOnClickListener(v -> {
                if(mCallback != null) {
                    mCallback.pvdStoreFile(mPdfUri, mFilepath);
                } else {
                    Toast.makeText(this.getContext(), getString(R.string.error_operation), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(mbExit == null){
            mbExit = view.findViewById(R.id.bttn_exit);
            mbExit.setOnClickListener(v -> {
                if(mCallback != null){
                    mCallback.pvdOnClose();
                }
                dismiss();
            });
        }
        if(mPDFView == null){
            mPDFView = view.findViewById(R.id.pdfview);
        }
    }

    private boolean initData(){
        if(getArguments() != null) {
            initTitle();
            initFilepath();
            initIsTempFile();
            initPdf();
            return mPdfUri != null;
        } else {
            showErrorMessage();
            return false;
        }
    }

    private void initTitle(){
        mtvTitle.setText(getArguments().containsKey(KEY_TITLE) ? getArguments().getString(KEY_TITLE) : "");
    }

    private void initFilepath(){
        mFilepath = getArguments().containsKey(KEY_FILEPATH) ? getArguments().getString(KEY_FILEPATH) : null;
    }

    private void initIsTempFile(){
        mIsTempFile = getArguments().containsKey(KEY_IS_TEMP_FILE) && getArguments().getBoolean(KEY_IS_TEMP_FILE);
    }

    private void initPdf(){
        mPdfUri = getArguments().containsKey(KEY_PDF_URI) ? getArguments().getParcelable(KEY_PDF_URI) : null;
    }

    private void showPdf(){
        if(mPdfUri != null){
            mPDFView.fromUri(mPdfUri)
                .defaultPage(0)
                .onError(this)
                .onPageError(this)
                .enableDoubletap(true)
                .load();
        } else {
            showErrorMessage();
            dismiss();
        }
    }

    private void showErrorMessage(){
        Toast.makeText(this.getContext(), getString(R.string.error_generic, getString(R.string.msg_the_visualization_of_the_pdf)), Toast.LENGTH_SHORT).show();
    }

    private void checkSave(){
        if(mCallback == null || !mIsTempFile){
            mibSave.setVisibility(ImageButton.GONE);
        } else {
            mibSave.setVisibility(ImageButton.VISIBLE);
        }
    }

}
