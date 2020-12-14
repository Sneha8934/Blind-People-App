package com.sneha.blindpeople.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler implements UncaughtExceptionHandler {
	
	private final Context myContext;
    private final String LINE_SEPARATOR = "\n";

    public ExceptionHandler(Context context) {
        myContext = context;
    }
    
    public void uncaughtException(Thread thread, Throwable exception) {

        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();

        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());
        
        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n************ FIRMWARE ************\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);
        
        String strErrorReport = errorReport.toString();
        
        Log.e("Custom Error Log", strErrorReport);

        //Intent error = new Intent(myContext, ErrorScreen.class);
        //error.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //error.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //myContext.startActivity(error);

        System.exit(0);
    }

	
}
