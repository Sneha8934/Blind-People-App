package com.sneha.blindpeople.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sneha.blindpeople.Api.ApiUrls;
import com.sneha.blindpeople.R;
import com.sneha.blindpeople.util.AlertDialogClassCommon;
import com.sneha.blindpeople.util.AppController;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    TextToSpeech t1;
    EditText ed1;
    Button b1;
    private final int REQ_CODE = 100;
    private ImageView micClick;
    private TextView txtViewName;
    String mostRecentUtteranceID;
    private String isValueofField;
    private String android_id;
    private ProgressBar pbLoader;
    private Context mContext;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private LinearLayout backgroundTouch;
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        micClick = (ImageView) findViewById(R.id.speak);
        txtViewName = (TextView) findViewById(R.id.txtName);
        pbLoader = (ProgressBar) findViewById(R.id.pbLoader);
        backgroundTouch = (LinearLayout) findViewById(R.id.backgroundTouch);
        mContext = this;
        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (t1.getEngines().size() == 0) {
                    Toast.makeText(LoginActivity.this, "No Engines Installed", Toast.LENGTH_LONG).show();
                } else {
                    if (status == TextToSpeech.SUCCESS) {
                        ttsInitialized();
                    }
                }
            }
        });

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        backgroundTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
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
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);

        t1.speak("Please Say your name loud and clear by tapping once on the screen.", TextToSpeech.QUEUE_FLUSH, params);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtViewName.setText(result.get(0).toString());
                    createUser();
                }
                break;
            }
        }
    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    private void createUser() {
        pbLoader.setVisibility(View.VISIBLE);
        mostRecentUtteranceID = (new Random().nextInt() % 9999999) + ""; // "" is String force
        final HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);
        t1.speak("Processing", TextToSpeech.QUEUE_FLUSH,params);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiUrls.createUser, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                try {
                    pbLoader.setVisibility(View.GONE);
                    JSONObject rootjsonobj = new JSONObject(response.toString());
                    if (rootjsonobj.has("error")) {
                        t1.speak("Process Completed.", TextToSpeech.QUEUE_FLUSH,params);
                        Boolean error = rootjsonobj.getBoolean("error");
                        if (error == false) {
                            SharedPreferences shared = mContext.getSharedPreferences("MyPreferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.commit();
                            Intent i = new Intent(mContext.getApplicationContext(), HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(i);
                            AlertDialogClassCommon.showDialogError(mContext, "Enter Full Name");
                        } else {
                            String errorMsg = rootjsonobj.getString("message");
                            AlertDialogClassCommon.showDialogError(mContext, errorMsg);
                            if(errorMsg.equals("Device ID already registered.")){
                                SharedPreferences shared = mContext.getSharedPreferences("MyPreferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.commit();
                                Intent i = new Intent(mContext.getApplicationContext(), HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mContext.startActivity(i);
                            }

                        }
                    }

                } catch (Exception ex) {
                    pbLoader.setVisibility(View.GONE);
                    AlertDialogClassCommon.showDialogError(mContext, ex.toString());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pbLoader.setVisibility(View.GONE);
                AlertDialogClassCommon.showDialogError(mContext, error.toString());
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_name", txtViewName.getText().toString().trim());
                params.put("device_id", android_id);
                params.put("user_token", getRandomString(16));
                params.put("user_id", getRandomString(6));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                return map;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, TAG);

    }



    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

}