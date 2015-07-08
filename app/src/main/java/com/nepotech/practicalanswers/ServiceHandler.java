package com.nepotech.practicalanswers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ServiceHandler {

    public static String getText(String url) throws Exception {
        long startTime = System.currentTimeMillis();

        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        HttpURLConnection urlConnection = (HttpURLConnection) website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        urlConnection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        long endtime = System.currentTimeMillis();

        Log.d("ServiceHandler", "Time taken (ms): " + (endtime - startTime));

        return response.toString();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}