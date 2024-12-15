package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView mtvMain;
    private Button mSpeech;
    private Button mRead;

    private SpeechRecognizer mSpRec;
    private TextToSpeech mTxSp;
    private Intent mSRIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initOnCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpRec.destroy();
    }

    private void initOnCreate(){
        mtvMain = findViewById(R.id.tv_main);
        mSpeech = findViewById(R.id.button_speech);
        mRead = findViewById(R.id.button_read);

        mSpeech.setOnClickListener(v -> {
            mSpRec.startListening(mSRIntent);
        });

        mRead.setOnClickListener(v -> {
            try {
                mTxSp.speak(mtvMain.getText(), TextToSpeech.QUEUE_FLUSH, null, null);
            } catch (Exception e){

            }
        });
        initSpeechRecognizer();
        requestRecordAudioPermission();

        mTxSp = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    Log.d("HERE", "HERE");
                }
            }
        });
    }

    private void initSpeechRecognizer(){
        mSpRec = SpeechRecognizer.createSpeechRecognizer(this);
        mSRIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
        mSpRec.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                if(error == 8) {
                    mSpRec.cancel();
                    mSpRec.startListening(mSRIntent);
                } else if(error == 7){
                    return;
                }
            }

            @Override
            public void onResults(Bundle results) {
                String data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0x0);
                String[] splitted = TextUtils.split(data, " ");
                for(String split : splitted){
                    mtvMain.setText(mtvMain.getText() + " " + split);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }

    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = android.Manifest.permission.RECORD_AUDIO;

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{requiredPermission}, 101);
            }
        }
    }

}
