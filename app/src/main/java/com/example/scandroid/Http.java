package com.example.scandroid;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class Http {
    private static final int FAILURE = -1;
    private static Executor executor = Executors.newSingleThreadExecutor();

    private Http() {}

    public static void postJSON(@NonNull final String urlString, @NonNull final JSONObject data, @NonNull final ResponseHandler handler) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    final URL url = new URL(urlString);
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    final DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    try {
                        os.writeBytes(data.toString());
                        os.flush();
                    } finally {
                        os.close();
                    }

                    final int status = conn.getResponseCode();
                    conn.disconnect();

                    handler.onResponse(status);
                } catch (Exception e) {
                    //e.printStackTrace();
                    handler.onResponse(FAILURE);
                }

            }
        });
    }

    public static interface ResponseHandler {
        public void onResponse(int statusCode);
    }
}
