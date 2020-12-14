package com.sneha.blindpeople.util;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class ParseVolleyError {



    public static String ParseVolleyError(VolleyError error)  {
        // TODO Auto-generated constructor stub
        VolleyError customError;
        String message = "";

        customError = error;

        if(customError != null) {

            if (customError instanceof TimeoutError || customError instanceof NoConnectionError) {

                message = "Could not connect to server.";

            } else if (customError instanceof AuthFailureError) {

                message = ("Authentication Failed. Please logout and login again.");


            } else if (customError instanceof ServerError) {

                message = ("Internal server error.");

            } else if (customError instanceof NetworkError) {

                message = ("No network connection.");

            } else if (customError instanceof ParseError) {

                message = ("Parse error.");

            } else {

                message = ("Unable to process your request.");

            }
        }

        return message;
    }

}
