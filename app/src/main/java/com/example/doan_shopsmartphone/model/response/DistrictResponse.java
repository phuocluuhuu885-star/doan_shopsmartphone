package com.example.doan_shopsmartphone.model.response;

import androidx.annotation.NonNull;

import com.example.doan_shopsmartphone.model.District;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DistrictResponse {
    @SerializedName("results")
    private List<District> results;

    public DistrictResponse() {
    }

    @NonNull
    @Override
    public String toString() {
        return "DistrictResponse{" +
                "results=" + results +
                '}';
    }

    public List<District> getResults() {
        return results;
    }

    public void setResults(List<District> results) {
        this.results = results;
    }
}
