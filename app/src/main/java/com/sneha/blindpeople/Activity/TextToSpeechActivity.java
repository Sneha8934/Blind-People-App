package com.sneha.blindpeople.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.sneha.blindpeople.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class TextToSpeechActivity extends AppCompatActivity {

    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    String mostRecentUtteranceID;
    TextToSpeech t1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case RequestCameraPermissionID:
            {
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);
        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.text_view);

        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(TextToSpeechActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
        }

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if(items.size()!=0){
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder=new StringBuilder();
                            for(int i=0;i<items.size();i++){
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            textView.setText(stringBuilder.toString());
                            cameraSource.stop();
                            textToSpeech(textView.getText().toString().trim());
                        }
                    });
                }
            }
        });

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (t1.getEngines().size() == 0) {
                    Toast.makeText(TextToSpeechActivity.this, "No Engines Installed", Toast.LENGTH_LONG).show();
                } else {
                    if (status == TextToSpeech.SUCCESS) {
                        ttsInitialized();
                    }
                }
            }
        });
    }

    private void ttsInitialized() {

        // *** set UtteranceProgressListener AFTER tts is initialized ***
        t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                Log.i("XXX", "Done with the string");
            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        // set Language
        t1.setLanguage(Locale.US);

        // set unique utterance ID for each utterance
        mostRecentUtteranceID = (new Random().nextInt() % 9999999) + ""; // "" is String force

        // set params
        // *** this method will work for more devices: API 19+ ***


    }

    private void textToSpeech(String textToSpeak){
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);

        t1.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params);
    }
}
