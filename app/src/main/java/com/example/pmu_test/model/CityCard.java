package com.example.pmu_test.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "city_card")
public class CityCard {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "city")
    private String city;
    @ColumnInfo(name = "temperature")
    private String temperature;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "humidity")
    private String humidity;
    @ColumnInfo(name = "wind")
    private String wind;
    @ColumnInfo(name = "pressure")
    private String pressure;
    @ColumnInfo(name = "visibility")
    private String visibility;
    @ColumnInfo(name = "last_requested")
    private long lastRequested;

    @ColumnInfo(name = "icon_id")
    private String iconId;
    // Constructor
    public CityCard(String city, long lastRequested, String temperature, String description, String humidity, String wind, String pressure, String visibility, String iconId) {
        this.city = city;
        this.lastRequested = lastRequested;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.wind = wind;
        this.pressure = pressure;
        this.visibility = visibility;
        this.iconId = iconId;
    }

    // Getter and Setter
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWind() {
        return wind;
    }

    public String getPressure() {
        return pressure;
    }

    public String getVisibility() {
        return visibility;
    }

    public long getLastRequested() {
        return lastRequested;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
    public void setWind(String wind) {
        this.wind = wind;
    }
    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
    public void setLastRequested(long lastRequested) {
        this.lastRequested = lastRequested;
    }
    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }
}
