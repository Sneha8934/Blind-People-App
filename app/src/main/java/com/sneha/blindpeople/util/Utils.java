package com.sneha.blindpeople.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

public class Utils {

    static String TAG = Utils.class.getSimpleName();
    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static Boolean isMarshmallow() {

        Boolean isMarshmallow = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isMarshmallow = true;
        } else {
            isMarshmallow = false;
        }

        return isMarshmallow;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void setBackground(Context context, int background, View view)
    {
        int sdk = Build.VERSION.SDK_INT;
        if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(getDrawable(context, background));
        } else {
            view.setBackground(getDrawable(context, background));
        }
    }

    public static final Drawable getDrawable(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            return ContextCompat.getDrawable(context, id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    /**
     public static void displayVolleyError(Context context, VolleyError error)
     {
     if(error != null)
     {

     if (error instanceof TimeoutError || error instanceof NoConnectionError) {
     displayDialog(context, "Could not connect to server.");
     } else if (error instanceof AuthFailureError) {
     displayDialog(context, "Authentication Failed.");
     } else if (error instanceof ServerError) {
     displayDialog(context, "Internal server error.");
     } else if (error instanceof NetworkError) {
     displayDialog(context, "No network connection.");
     } else if (error instanceof ParseError) {
     displayDialog(context, "Parse error.");
     }else{
     displayDialog(context, "Unable to process your request.");
     }

     }else{
     displayDialog(context, "Sorry, something went wrong.");
     }

     }
     */

//    public static void displayDialog(Context context, String title, String message) {
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
//        dialogBuilder.setView(dialogView);
//
//        final TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
//        final TextView tvMessage = (TextView) dialogView.findViewById(R.id.tvMessage);
//        tvTitle.setText(title);
//        tvMessage.setText(message);
//
//        dialogBuilder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//
//
//            }
//        });
//
//        /**
//         dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int id) {
//
//        }
//        });
//         */
//
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//
//        //Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//        //nbutton.setTextColor(getColor(context, R.color.textColorLight));
//        //nbutton.setBackgroundColor(Color.MAGENTA);
//        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//        pbutton.setTextColor(getColor(context, R.color.black));
//        //pbutton.setBackgroundColor(Color.YELLOW);
//
//    }


}
