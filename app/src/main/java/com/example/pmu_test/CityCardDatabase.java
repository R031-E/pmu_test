package com.example.pmu_test;
import android.content.Context;

import androidx.room.*;
import com.example.pmu_test.model.CityCard;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {CityCard.class}, version = 1)
public abstract class CityCardDatabase extends RoomDatabase {
    public abstract CityCardDao cityCardDao();
    private static volatile CityCardDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static CityCardDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CityCardDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CityCardDatabase.class, "favourites_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
