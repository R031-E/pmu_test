package com.example.pmu_test;
import android.Manifest;
import java.util.ArrayList;
import java.util.List;

import com.example.pmu_test.model.CityCard;
import com.example.pmu_test.SwipeToDeleteCallback;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import io.reactivex.rxjava3.core.Single;

public class MainActivity extends AppCompatActivity {
    //TODO: Добавить активити с информацией о приложении
    static final int ADD_CITY_REQUEST = 0;
    static final int EDIT_CITY_REQUEST = 1;
    private CityViewModel mCityViewModel;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FusedLocationProviderClient fusedLocationClient;

    private EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        searchText = findViewById(R.id.search_text);
        TextView my_location_card = findViewById(R.id.my_location_card);
        setUpRecyclerView();


        my_location_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            };
        });

        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP && !searchText.getText().toString().isEmpty()) {
                    String city = searchText.getText().toString();
                    new RetrieveCityTask().execute(city);
                    return true;
                }
                return false;
            }

        });
    }

    private class RetrieveCityTask extends AsyncTask<String, Void, CityCard> {
        @Override
        protected CityCard doInBackground(String... strings) {
            String city = strings[0];
            return mCityViewModel.getCity(city);
        }

        @Override
        protected void onPostExecute(CityCard cityCard) {
            Intent intent;
            if (cityCard != null) {
                intent = createWeatherDataIntent(cityCard, cityCard.getCity());
            } else {
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + searchText.getText().toString() + "&units=metric";
                intent = new Intent(MainActivity.this, WeatherData.class);
                intent.putExtra("url", url);
                intent.putExtra("city", searchText.getText().toString());
                intent.putExtra("temperature", "");
                intent.putExtra("humidity", "");
                intent.putExtra("pressure", "");
                intent.putExtra("wind", "");
                intent.putExtra("visibility", "");
                intent.putExtra("description", "");
                intent.putExtra("timestamp", 0);
                intent.putExtra("icon_id", "");
            }
            startActivityForResult(intent, ADD_CITY_REQUEST);
            searchText.setText("");
        }
    }

    private Intent createWeatherDataIntent(CityCard cityCard, String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric";
        Intent intent = new Intent(MainActivity.this, WeatherData.class);
        intent.putExtra("url", url);
        intent.putExtra("city", city);
        intent.putExtra("temperature", cityCard.getTemperature());
        intent.putExtra("humidity", cityCard.getHumidity());
        intent.putExtra("pressure", cityCard.getPressure());
        intent.putExtra("wind", cityCard.getWind());
        intent.putExtra("visibility", cityCard.getVisibility());
        intent.putExtra("description", cityCard.getDescription());
        intent.putExtra("timestamp", cityCard.getLastRequested());
        intent.putExtra("icon_id", cityCard.getIconId());
        return intent;
    }


    private void setUpRecyclerView() {
        RecyclerView citiesRV = findViewById(R.id.cities_cards);
        final CityCardsAdapter adapter = new CityCardsAdapter(new CityCardsAdapter.CityDiff(), this);
        citiesRV.setAdapter(adapter);
        citiesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter) {
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        if (direction == ItemTouchHelper.LEFT) {
                            int position = viewHolder.getAdapterPosition();
                            mCityViewModel.delete(adapter.getCityAt(position));
                        }
                    }
        });
        itemTouchHelper.attachToRecyclerView(citiesRV);
        // Get a new or existing ViewModel from the ViewModelProvider.
        mCityViewModel = new ViewModelProvider(this).get(CityViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        Log.d("MainActivity", "Call to DB was made to get all cities");
        mCityViewModel.getAllCities().observe(this, cities -> {
            // Update the cached copy of the words in the adapter.
            adapter.submitList(cities);
        });

        adapter.setOnItemClickListener(new CityCardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CityCard cityCard) {
                // after clicking on item of recycler view
                // we are opening a new activity and passing
                // a data to our activity.
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityCard.getCity() + "&units=metric";
                Intent intent = new Intent(MainActivity.this, WeatherData.class);
                intent.putExtra("id", cityCard.getId());
                intent.putExtra("url", url);
                intent.putExtra("city", cityCard.getCity());
                intent.putExtra("temperature", cityCard.getTemperature());
                intent.putExtra("humidity", cityCard.getHumidity());
                intent.putExtra("pressure", cityCard.getPressure());
                intent.putExtra("wind", cityCard.getWind());
                intent.putExtra("visibility", cityCard.getVisibility());
                intent.putExtra("description", cityCard.getDescription());
                intent.putExtra("timestamp", cityCard.getLastRequested());
                intent.putExtra("icon_id", cityCard.getIconId());
                intent.putExtra("position", 1);

                // below line is to start a new activity and
                // adding a edit course constant.
                startActivityForResult(intent, EDIT_CITY_REQUEST);
            }
        });
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        else{
        fusedLocationClient.getCurrentLocation(102, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String url = "https://api.openweathermap.org/data/2.5/weather?lat=" +
                                    latitude + "&lon=" + longitude +
                                    "&units=metric";
                            Intent intent = new Intent(MainActivity.this, WeatherData.class);
                            intent.putExtra("url", url);
                            intent.putExtra("city", getString(R.string.my_location));
                            intent.putExtra("position", -1);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Не удалось получить текущую локацию", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Ошибка при получении текущей локации", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CITY_REQUEST){
            if (resultCode == RESULT_OK) {
                String city = data.getStringExtra("city");
                String temperature = data.getStringExtra("temperature");
                String description = data.getStringExtra("description");
                String humidity = data.getStringExtra("humidity");
                String wind = data.getStringExtra("wind");
                String pressure = data.getStringExtra("pressure");
                String visibility = data.getStringExtra("visibility");
                long lastRequested = data.getLongExtra("timestamp", 0);
                String iconId = data.getStringExtra("icon_id");
                CityCard newCityCard = new CityCard(city, lastRequested, temperature, description, humidity, wind, pressure, visibility, iconId);
                mCityViewModel.insert(newCityCard);
            }
        }
        if (requestCode == EDIT_CITY_REQUEST && resultCode == RESULT_OK) {
            String city = data.getStringExtra("city");
            String temperature = data.getStringExtra("temperature");
            String description = data.getStringExtra("description");
            String humidity = data.getStringExtra("humidity");
            String wind = data.getStringExtra("wind");
            String pressure = data.getStringExtra("pressure");
            String visibility = data.getStringExtra("visibility");
            long lastRequested = data.getLongExtra("timestamp", 0);
            String iconId = data.getStringExtra("icon_id");
            CityCard updateCityCard = new CityCard(city, lastRequested, temperature, description, humidity, wind, pressure, visibility, iconId);
            mCityViewModel.update(updateCityCard);
        }
    }

}