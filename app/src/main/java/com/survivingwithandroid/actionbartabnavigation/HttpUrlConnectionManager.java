package com.survivingwithandroid.actionbartabnavigation;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lipov91 on 2015-07-06.
 * <p/>
 * Rozwiazanie zalecane w przypadku nowo utworzonych aplikacji
 */
public class HttpUrlConnectionManager {

    static URL url;
    static HttpURLConnection connection = null;
    static StringBuilder stringBuilder = null;
    static BufferedReader buferedReader = null;
    static String line = "";
    static String errorMsg = "";
    static byte[] loginBytes;
    static StringBuilder authConfig;

    public static String getData(String uri) {

        try {

            url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();

            stringBuilder = new StringBuilder();
            buferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = buferedReader.readLine()) != null) {

                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();

        } catch (Exception e) {

            e.printStackTrace();
            try {

                errorMsg = "" + connection.getResponseCode();

            } catch (IOException ex) {

                ex.printStackTrace();
                errorMsg = ex.getMessage();
            }
            errorMsg += "\n\n" + e.getMessage();

            return errorMsg;

        } finally {

            if (buferedReader != null) {

                try {

                    buferedReader.close();

                } catch (IOException e) {

                    e.printStackTrace();
                    errorMsg = e.getMessage();

                    return errorMsg;
                }
            }
        }
    }

    public static String getData(String uri, String userName, String password) {

        loginBytes = (userName + ":" + password).getBytes();
        authConfig = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        try {

            url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("Authentication", authConfig.toString());

            stringBuilder = new StringBuilder();
            buferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = buferedReader.readLine()) != null) {

                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();

        } catch (Exception e) {

            try {

                errorMsg = "" + connection.getResponseCode();

            } catch (IOException ex) {

                ex.printStackTrace();
                errorMsg = ex.getMessage();
            }
            e.printStackTrace();
            errorMsg += "\n\n" + e.getMessage();

            return errorMsg;

        } finally {

            if (buferedReader != null) {

                try {

                    buferedReader.close();

                } catch (IOException e) {

                    e.printStackTrace();
                    errorMsg = e.getMessage();

                    return errorMsg;
                }
            }
        }
    }
}
