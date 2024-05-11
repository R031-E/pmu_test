package com.example.pmu_test;
import androidx.room.*;
import com.example.pmu_test.model.CityCard;

@Database(entities = {CityCard.class}, version = 1)
public abstract class CityCardDatabase extends RoomDatabase {
    public abstract CityCardDao cityCardDao();
}
