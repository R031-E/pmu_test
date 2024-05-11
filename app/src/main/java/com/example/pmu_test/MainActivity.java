package com.example.pmu_test;
import android.Manifest;
import java.util.ArrayList;
import com.example.pmu_test.model.CityCard;
import com.example.pmu_test.SwipeToDeleteCallback;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    //TODO: добавить Room
    //TODO: реализовать CRUD операции
    //TODO: реализовать меню настроек со сменой локализации
    static final int ADD_CITY_REQUEST = 0;
    private ArrayList<CityCard> citiesArrayList = new ArrayList<CityCard>();
    private CityCardsAdapter citiesAdapter = new CityCardsAdapter(this, citiesArrayList);
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

        //citiesArrayList.add(new CityCard("Moscow"));
        //citiesAdapter.notifyItemInserted(citiesArrayList.size() - 1);
        setUpRecyclerView();

        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        citiesRV.setLayoutManager(linearLayoutManager);
        citiesRV.setAdapter(citiesAdapter);*/

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
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric";
                    Intent intent = new Intent(MainActivity.this, WeatherData.class);
                    intent.putExtra("url", url);
                    intent.putExtra("city", city);
                    searchText.setText("");
                    startActivityForResult(intent, ADD_CITY_REQUEST);
                    return true;
                }
                return false;
            }
        });
    }

    private void setUpRecyclerView() {
        RecyclerView citiesRV = findViewById(R.id.cities_cards);
        citiesRV.setAdapter(citiesAdapter);
        citiesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(citiesAdapter));
        itemTouchHelper.attachToRecyclerView(citiesRV);
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
                //CityCard newCityCard = new CityCard(city);
                citiesArrayList.add(newCityCard);
                citiesAdapter.notifyItemInserted(citiesArrayList.size() - 1);
            }
        }
    }

}