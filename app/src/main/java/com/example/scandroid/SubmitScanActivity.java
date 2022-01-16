package com.example.scandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class SubmitScanActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private final static String url = "https://your.cloudfront.net/api/v1/qrcode";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_scan);

        // Get the Intent that started this activity and extract the string
        final Intent intent = getIntent();
        final String scanText = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        final TextView textView = findViewById(R.id.textView);
        textView.setText(scanText);

        final Date scanTime = new Date();
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        final TextView textViewScanTime = findViewById(R.id.textViewScanTime);
        textViewScanTime.setText(df.format(scanTime));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            final double latitude = location.getLatitude();
                            final double longitude = location.getLongitude();
                            final double accuracy = location.getAccuracy();
                            TextView textViewLocation = findViewById(R.id.textViewLocation);
                            textViewLocation.setText(Double.toString(latitude) + " " + Double.toString(longitude));
                            JSONObject jsonParam = new JSONObject();
                            try {
                                jsonParam.put("qrcode", scanText);
                                jsonParam.put("latitude", latitude);
                                jsonParam.put("longitude", longitude);
                                jsonParam.put("accuracy", accuracy);
                                jsonParam.put("user", "placeholder");
                                jsonParam.put("submitted", scanTime.getTime());
                            } catch (Exception ex) {
                                // queue for later and Toast?
                            }
                            Http.postJSON(url, jsonParam, statusCode -> {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView textViewStatusCode = findViewById(R.id.textViewStatusCode);
                                        textViewStatusCode.setText(Integer.toString(statusCode));
                                    }
                                });

                            });
                        }
                    }
                });
    }
}