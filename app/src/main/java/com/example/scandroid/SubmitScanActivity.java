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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SubmitScanActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;

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
        final DateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:MM:SS");
        final TextView textViewScanTime = findViewById(R.id.textViewScanTime);
        textViewScanTime.setText(df.format(scanTime));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            TextView textViewLocation = findViewById(R.id.textViewLocation);
                            final double latitude = location.getLatitude();
                            final double longitude = location.getLongitude();
                            textViewLocation.setText(Double.toString(latitude) + " " + Double.toString(longitude));
                        }
                    }
                });
    }
}