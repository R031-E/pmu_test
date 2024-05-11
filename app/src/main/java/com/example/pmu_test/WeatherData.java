package com.example.pmu_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData extends AppCompatActivity {
    private String apiKey = "&appid=d77b3f0e8e7ad3d429f208e537c5b5b9";
    private String url;
    TextView cityTextView, temperatureTextView,
            humidityTextView, windTextView,
            pressureTextView, visibilityTextView, weatherTextView;
    ImageView weatherImageView;

    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_data);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                final int SWIPE_MIN_DISTANCE = 120;
                final int SWIPE_THRESHOLD_VELOCITY = 200;
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // свайп вправо
                    finish();
                    return true;
                }
                return false;
            }
        });

        RelativeLayout buttonContainer = findViewById(R.id.button_container);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button addButton = findViewById(R.id.add_button);
        // Получаем данные из MainActivity
        String city = getIntent().getStringExtra("city");
        String url = getIntent().getStringExtra("url");
        int position = getIntent().getIntExtra("position", -2);
        if (position != -2) {
            buttonContainer.setVisibility(View.GONE);
        }
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK,
                        new Intent().putExtra("city", city));
                finish();
            }
        });
        cityTextView = findViewById(R.id.textViewCity);
        weatherImageView = findViewById(R.id.imageViewWeatherIcon);
        temperatureTextView = findViewById(R.id.textViewTemperature);
        humidityTextView = findViewById(R.id.textViewHumidity);
        windTextView = findViewById(R.id.textViewWindSpeed);
        pressureTextView = findViewById(R.id.textViewPressure);
        visibilityTextView = findViewById(R.id.textViewVisibility);
        weatherTextView = findViewById(R.id.textViewMainDescription);


        cityTextView.setText(city);
        getWeatherData(city, url);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private void getWeatherData(String city, String url) {
        String finalUrl = url + apiKey;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String iconUrl = "https://openweathermap.org/img/w/" + response.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png";
                    String weather = response.getJSONArray("weather").getJSONObject(0).getString("main");
                    double tmp = response.getJSONObject("main").getDouble("temp");
                    int humidity = response.getJSONObject("main").getInt("humidity");
                    double wind = response.getJSONObject("wind").getDouble("speed");
                    int pressure = response.getJSONObject("main").getInt("pressure");
                    int visibility = response.getInt("visibility");

                    int temperature = (int) Math.round(tmp);

                    String temperatureString = String.valueOf(temperature) + " °C";
                    String humidityString = String.valueOf(humidity) + "%";
                    String windString = String.valueOf(wind) + " m/s";
                    String pressureString = String.valueOf(pressure) + " hPa";
                    String visibilityString = String.valueOf(visibility) + " m";
                    Picasso.get().load(iconUrl).into(weatherImageView);
                    weatherTextView.setText(weather);
                    temperatureTextView.setText(temperatureString);
                    humidityTextView.setText(humidityString);
                    windTextView.setText(windString);
                    pressureTextView.setText(pressureString);
                    visibilityTextView.setText(visibilityString);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(WeatherData.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WeatherData.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);
    }
}