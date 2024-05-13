package com.example.pmu_test;
import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.pmu_test.model.CityCard;
import java.util.List;

@Dao
public interface CityCardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(CityCard cityCard);
    @Delete
    void delete(CityCard cityCard);

    @Query("DELETE FROM city_card WHERE city = :city")
    void deleteByCity(String city);
    @Update
    void update(CityCard cityCard);
    @Query("SELECT * FROM city_card")
    LiveData<List<CityCard>> getAll();

    @Query("SELECT last_requested FROM city_card WHERE city = :city")
    long getLastRequested(String city);

    @Query("SELECT * FROM city_card WHERE city = :city")
     CityCard getCity(String city);
}
