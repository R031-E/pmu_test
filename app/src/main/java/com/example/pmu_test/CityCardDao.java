package com.example.pmu_test;
import androidx.room.*;

import com.example.pmu_test.model.CityCard;

import java.util.ArrayList;

@Dao
public interface CityCardDao {
    @Insert
    void insert(CityCard cityCard);
    @Delete
    void delete(CityCard cityCard);
    @Update
    void update(CityCard cityCard);
    @Query("SELECT * FROM city_card")
    ArrayList<CityCard> getAll();
}
