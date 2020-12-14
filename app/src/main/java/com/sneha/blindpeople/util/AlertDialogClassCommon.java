package com.sneha.blindpeople.util;


import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AlertDialogClassCommon {


    public static void showDialogWarning(final Context context, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Error")
                .setContentText(message)
                .setConfirmText("OKAY")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public static void showDialogError(final Context context, String message) {


        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText(message)
                .setConfirmText("OKAY")
                .setConfirmButtonBackgroundColor(Color.parseColor("#1a4783"))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}
