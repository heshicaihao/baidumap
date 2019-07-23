package com.heshicaihao.baidumap;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Objects;

public class Spot {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Spot(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Spot() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spot spot = (Spot) o;
        return Double.compare(spot.latitude, latitude) == 0 &&
                Double.compare(spot.longitude, longitude) == 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "Spot{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
