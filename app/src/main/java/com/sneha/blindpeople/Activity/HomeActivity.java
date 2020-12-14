package com.sneha.blindpeople.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sneha.blindpeople.R;
import com.sneha.blindpeople.util.SwipeGetsure;
import com.sneha.blindpeople.R;

public class HomeActivity extends AppCompatActivity {


    private LinearLayout activity_main_layout;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        activity_main_layout = (LinearLayout) findViewById(R.id.LinearLayout1);
        activity_main_layout.setOnTouchListener(new SwipeGetsure(HomeActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(HomeActivity.this, "top", Toast.LENGTH_SHORT).show();
                Toast.makeText(HomeActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContext.getApplicationContext(), GeolocationActivity.class);
                mContext.startActivity(i);
            }

            public void onSwipeRight() {
                Toast.makeText(HomeActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContext.getApplicationContext(), TextToSpeechActivity.class);
                mContext.startActivity(i);
//                Toast.makeText(HomeActivity.this, "left", Toast.LENGTH_SHORT).show();
//                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.guna.ocrsample");
//                if (launchIntent != null) {
//                    startActivity(launchIntent);//null pointer check in case package name was not found
//                }
            }

            public void onSwipeLeft() {
                Toast.makeText(HomeActivity.this, "left", Toast.LENGTH_SHORT).show();
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.controller.app");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }

            public void onSwipeBottom() {
//                Toast.makeText(HomeActivity.this, "bottom", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(mContext.getApplicationContext(), BarcodeReaderActivity.class);
//                mContext.startActivity(i);
            }
        });

    }
}