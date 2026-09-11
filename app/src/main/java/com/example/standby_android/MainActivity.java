package com.example.standby_android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView tvDate, tvTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen Hide Status & Navigation Bars
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        setContentView(R.layout.activity_main);

        tvDate = findViewById(R.id.tvDate);
        tvTemp = findViewById(R.id.tvTemp);

        // Set Current System Date (e.g., MON 5)
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d", Locale.ENGLISH);
        tvDate.setText(dateFormat.format(new Date()).toUpperCase());

        // Get Location & Temperature
        fetchLocationAndTemperature();
    }

    private void fetchLocationAndTemperature() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = null;
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            getTemperature(lat, lon);
        }
    }

    private void getTemperature(double lat, double lon) {
        new Thread(() -> {
            try {
                // Free public open endpoint (No API key required)
                String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current_weather=true";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject currentWeather = jsonObject.getJSONObject("current_weather");
                double temp = currentWeather.getDouble("temperature");

                runOnUiThread(() -> {
                    tvTemp.setVisibility(View.VISIBLE);
                    tvTemp.setText((int) Math.round(temp) + "°C");
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
