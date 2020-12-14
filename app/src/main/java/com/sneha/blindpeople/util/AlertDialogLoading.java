package com.sneha.blindpeople.util;

import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AlertDialogLoading {

    Context context;
    SweetAlertDialog pDialog;
    public AlertDialogLoading(Context context) {
        this.context = context;
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
    }

    public void showAlert() {
        pDialog.getProgressHelper();
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void dismissAlert(){
        //int count = viewGroup.getChildCount();
        //pDialog.dismissWithAnimation();
    }

}
