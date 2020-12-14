package com.sneha.blindpeople.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/**
 * Created by administrator on 10/3/17.
 */

public class PermissionUtils {

    //CALENDAR
    public static final String READ_CALENDAR_S = Manifest.permission.READ_CALENDAR;
    public static final String WRITE_CALENDAR_S = Manifest.permission.WRITE_CALENDAR;

    //CAMERA
    public static final String CAMERA_S = Manifest.permission.CAMERA;

    //CONTACTS
    public static final String READ_CONTACTS_S = Manifest.permission.READ_CONTACTS;
    public static final String WRITE_CONTACTS_S = Manifest.permission.WRITE_CONTACTS;
    public static final String GET_ACCOUNTS_S = Manifest.permission.GET_ACCOUNTS;

    //LOCATION
    public static final String ACCESS_FINE_LOCATION_S = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String ACCESS_COARSE_LOCATION_S = Manifest.permission.ACCESS_COARSE_LOCATION;

    //MICROPHONE
    public static final String RECORD_AUDIO_S = Manifest.permission.RECORD_AUDIO;

    //PHONE
    public static final String READ_CALL_LOG_S = Manifest.permission.READ_CALL_LOG;
    public static final String READ_PHONE_STATE_S = Manifest.permission.READ_PHONE_STATE;
    public static final String CALL_PHONE_S = Manifest.permission.CALL_PHONE;
    public static final String WRITE_CALL_LOG_S = Manifest.permission.WRITE_CALL_LOG;
    public static final String USE_SIP_S = Manifest.permission.USE_SIP;
    public static final String PROCESS_OUTGOING_CALLS_S = Manifest.permission.PROCESS_OUTGOING_CALLS;

    //SENSORS
    public static final String BODY_SENSORS_S = Manifest.permission.BODY_SENSORS;

    //SMS
    public static final String READ_SMS_S = Manifest.permission.READ_SMS;
    public static final String RECEIVE_WAP_PUSH_S = Manifest.permission.RECEIVE_WAP_PUSH;
    public static final String RECEIVE_MMS_S = Manifest.permission.RECEIVE_MMS;
    public static final String RECEIVE_SMS_S = Manifest.permission.RECEIVE_SMS;
    public static final String SEND_SMS_S = Manifest.permission.SEND_SMS;

    //STORAGE
    public static final String WRITE_EXTERNAL_STORAGE_S = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String READ_EXTERNAL_STORAGE_S = Manifest.permission.READ_EXTERNAL_STORAGE;

    public static final int MULTIPLE = 100;

    public static final int CALENDAR = 101;
    public static final int CAMERA = 102;
    public static final int CONTACTS = 103;
    public static final int LOCATION = 104;
    public static final int MICROPHONE = 105;
    public static final int PHONE = 106;
    public static final int SENSORS = 107;
    public static final int SMS = 108;
    public static final int STORAGE = 109;

    public static final String CALENDAR_M = "Calendar";
    public static final String CAMERA_M = "Camera";
    public static final String CONTACTS_M = "Contacts";
    public static final String LOCATION_M = "Location";
    public static final String MICROPHONE_M = "Microphone";
    public static final String PHONE_M = "Phone";
    public static final String SENSORS_M = "Sensors";
    public static final String SMS_M = "SMS";
    public static final String STORAGE_M = "Storage";

    //public static final String BASIC_M = APP_NAME + " Needs Information of  Your ";

    public static final String[] PERMISSIONS = {
            READ_CONTACTS_S,
            WRITE_CONTACTS_S,
            GET_ACCOUNTS_S,
            ACCESS_COARSE_LOCATION_S,
            ACCESS_FINE_LOCATION_S,
            READ_PHONE_STATE_S,
            READ_EXTERNAL_STORAGE_S,
            WRITE_EXTERNAL_STORAGE_S,
            CAMERA_S,
            READ_SMS_S,
            SEND_SMS_S,
            RECEIVE_SMS_S,
            READ_CALENDAR_S,
            WRITE_CALENDAR_S
    };

    public static final Boolean isMarshmallow() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }
        return false;
    }

    public static final boolean hasPermission(Activity activity, String[] PERMISSION_S) {
        if (PERMISSION_S != null) {
            for (int i = 0; i < PERMISSION_S.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, PERMISSION_S[i]) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }


    public static final void showMessageExit(Activity activity, String message, DialogInterface.OnClickListener pbListener, DialogInterface.OnClickListener nbListener) {
        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK", pbListener)
                .setNegativeButton("Settings", nbListener)
                .create()
                .show();
    }

    public static final void openSettings(Activity activity) {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(myAppSettings);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (isMarshmallow() && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
