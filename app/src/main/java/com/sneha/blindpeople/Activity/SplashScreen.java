package com.sneha.blindpeople.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.sneha.blindpeople.R;

public class SplashScreen extends AppCompatActivity {

    String TAG = SplashScreen.class.getSimpleName();
    private Context mContext;
    private Activity activity;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 200;
    private Boolean isLaunchDone;
    private Boolean isPermissionChecked;
    private Boolean isDatatored;
    private float cornerRadius = (float) 15.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mContext = this;
        activity = this;
        isLaunchDone = false;
        isDatatored = false;
        isPermissionChecked = false;
        checkPermission();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.e(TAG, "on Resume called");
//        if ((isLaunchDone == false) && (isPermissionChecked == false)){
//            checkPermission();
//        }
//        else if ((isPermissionChecked == true) && (isDatatored == true) && (isLaunchDone == false)){
//            launchApp();
//        }
//    }

    private void launchApp(){
        SharedPreferences shared = mContext.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        Boolean isLoggedIn = ((shared.getBoolean("isLoggedIn",false)));
        if (isLoggedIn == true){
            isLaunchDone = true;
            Intent i = new Intent(mContext.getApplicationContext(), HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(i);
        }
        else{
            isLaunchDone = true;
            Intent i = new Intent(mContext.getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(i);
        }
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            isPermissionChecked = true;
            launchApp();
        }
        else{
            isPermissionChecked = true;
            launchApp();
        }
    }
}
