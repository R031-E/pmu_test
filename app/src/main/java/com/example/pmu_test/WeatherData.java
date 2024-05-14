package com.example.pmu_test;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
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
import org.w3c.dom.Text;

public class WeatherData extends AppCompatActivity {
    private String apiKey = "&appid=d77b3f0e8e7ad3d429f208e537c5b5b9";
    private String url;
    TextView cityTextView, temperatureTextView,
            humidityTextView, windTextView,
            pressureTextView, visibilityTextView, weatherTextView;
    ImageView weatherImageView;

    private String iconIdField;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener sensorListener;
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 2000;
    private long mLastShakeTime;

    private GestureDetector gestureDetector;

    private Boolean isOutdated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_data);
        RelativeLayout buttonContainer = findViewById(R.id.button_container);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button addButton = findViewById(R.id.add_button);
        TextView shakeHint = findViewById(R.id.shake_to_update_hint);
        // Получаем данные из MainActivity
        int id = getIntent().getIntExtra("id", -2);
        String city = getIntent().getStringExtra("city");
        String url = getIntent().getStringExtra("url");
        String temperature = getIntent().getStringExtra("temperature");
        String humidity = getIntent().getStringExtra("humidity");
        String wind = getIntent().getStringExtra("wind");
        String pressure = getIntent().getStringExtra("pressure");
        String visibility = getIntent().getStringExtra("visibility");
        String description = getIntent().getStringExtra("description");
        long timestamp = getIntent().getLongExtra("timestamp", 0);
        String iconId = getIntent().getStringExtra("icon_id");
        int position = getIntent().getIntExtra("position", -2);
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                final int SWIPE_MIN_DISTANCE = 120;
                final int SWIPE_THRESHOLD_VELOCITY = 200;
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.d("WeatherData", "Swipe right");
                    if (isOutdated && timestamp == 0) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                    else if (isOutdated && timestamp != 0) {
                        Intent intent = new Intent();
                        intent.putExtra("id", id);
                        intent.putExtra("city", city);
                        intent.putExtra("temperature", temperatureTextView.getText().toString());
                        intent.putExtra("humidity", humidityTextView.getText().toString());
                        intent.putExtra("wind", windTextView.getText().toString());
                        intent.putExtra("pressure", pressureTextView.getText().toString());
                        intent.putExtra("visibility", visibilityTextView.getText().toString());
                        intent.putExtra("description", weatherTextView.getText().toString());
                        intent.putExtra("timestamp", System.currentTimeMillis()/1000);
                        intent.putExtra("icon_id", iconIdField);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else{
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                    return true;
                }
                Log.d("WeatherData", "Swipe left");
                return false;
            }
        });
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
                Intent intent = new Intent();
                intent.putExtra("city", city);
                intent.putExtra("temperature", temperatureTextView.getText().toString());
                intent.putExtra("humidity", humidityTextView.getText().toString());
                intent.putExtra("wind", windTextView.getText().toString());
                intent.putExtra("pressure", pressureTextView.getText().toString());
                intent.putExtra("visibility", visibilityTextView.getText().toString());
                intent.putExtra("description", weatherTextView.getText().toString());
                intent.putExtra("timestamp", System.currentTimeMillis()/1000);
                intent.putExtra("icon_id", iconIdField);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                long currentTime = System.currentTimeMillis();
                if ((currentTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    double acceleration = Math.sqrt(x * x + y * y + z * z);

                    if (acceleration > 12) {
                        mLastShakeTime = currentTime;
                        shakeHint.setText(R.string.data_updated_with_shake_hint);
                        getWeatherData(city, url);
                        isOutdated = true;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Не обязательно реализовывать
            }
        };

        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        cityTextView = findViewById(R.id.textViewCity);
        weatherImageView = findViewById(R.id.imageViewWeatherIcon);
        temperatureTextView = findViewById(R.id.textViewTemperature);
        humidityTextView = findViewById(R.id.textViewHumidity);
        windTextView = findViewById(R.id.textViewWindSpeed);
        pressureTextView = findViewById(R.id.textViewPressure);
        visibilityTextView = findViewById(R.id.textViewVisibility);
        weatherTextView = findViewById(R.id.textViewMainDescription);

        Log.d("WeatherData", "Humidity " + humidity);
        Log.d("WeatherData", "timestamp " + timestamp);

        cityTextView.setText(city);
        if ((System.currentTimeMillis()/1000 - timestamp) > 1200) {
            Log.d("WeatherData", "Weather data is outdated");
            isOutdated = true;
            getWeatherData(city, url);
        }
        else {
            Log.d("WeatherData", "Weather data is not outdated");
            isOutdated = false;
            temperatureTextView.setText(temperature);
            humidityTextView.setText(humidity);
            windTextView.setText(wind);
            pressureTextView.setText(pressure);
            visibilityTextView.setText(visibility);
            weatherTextView.setText(description);
            Picasso.get().load("https://openweathermap.org/img/w/" + iconId + ".png").into(weatherImageView);
        }
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
                    iconIdField = response.getJSONArray("weather").getJSONObject(0).getString("icon");
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
                    Log.d("WeatherData", "Weather data updated");

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

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(sensorListener);
    }
}