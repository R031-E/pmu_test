package com.example.pmu_test;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pmu_test.model.CityCard;

import java.util.List;

public class CityViewModel extends AndroidViewModel {
    private CityRepository cityRepository;
    private LiveData<List<CityCard>> allCities;

    public CityViewModel (Application application) {
        super(application);
        cityRepository = new CityRepository(application);
        allCities = cityRepository.getAllCities();
    }

    LiveData<List<CityCard>> getAllCities() { return allCities; }

    public void insert(CityCard city) { cityRepository.insert(city); }
    public void delete(CityCard city) { cityRepository.delete(city); }

    //public void deleteItem(int position) { cityRepository.delete(allCities.getValue().get(position)); }

    public long getLastRequested(String city) { return cityRepository.getLastRequested(city); }

    void update(CityCard city) { cityRepository.update(city); }

    public CityCard getCity(String city) { return cityRepository.getCity(city); }
}
