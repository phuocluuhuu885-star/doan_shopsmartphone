package com.example.doan_shopsmartphone.model.response;
import androidx.annotation.NonNull;

import com.example.doan_shopsmartphone.model.City;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CityResponse {

    @SerializedName("results")
    private List<City> results;

    public CityResponse() {
    }

    @NonNull
    @Override
    public String toString() {
        return "CityResponse{" +
                "results=" + results +
                '}';
    }

    public List<City> getResults() {
        return results;
    }

    public void setResults(List<City> results) {
        this.results = results;
    }
}
