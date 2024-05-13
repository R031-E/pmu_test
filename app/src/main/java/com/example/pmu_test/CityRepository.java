package com.example.pmu_test;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.pmu_test.model.CityCard;

import java.util.List;

public class CityRepository {

    private CityCardDao cityCardDao;
    private LiveData<List<CityCard>> allCities;

    CityRepository(Application application) {
        CityCardDatabase db = CityCardDatabase.getDatabase(application);
        cityCardDao = db.cityCardDao();
        allCities = cityCardDao.getAll();
    }

    LiveData<List<CityCard>> getAllCities() {
        return allCities;
    }

    void insert(CityCard cityCard) {
        CityCardDatabase.databaseWriteExecutor.execute(() -> {
            cityCardDao.insert(cityCard);
        });
    }

    void delete(CityCard cityCard) {
        CityCardDatabase.databaseWriteExecutor.execute(() -> {
            cityCardDao.delete(cityCard);
        });
    }

    long getLastRequested(String city) {
        return cityCardDao.getLastRequested(city);
    }

    CityCard getCity(String city) {
        return cityCardDao.getCity(city);
    }

    void update(CityCard cityCard) {
        CityCardDatabase.databaseWriteExecutor.execute(() -> {
            cityCardDao.update(cityCard);
        });
    }

    void deleteByCity(String city) {
        CityCardDatabase.databaseWriteExecutor.execute(() -> {
            cityCardDao.deleteByCity(city);
        });
    }
}
