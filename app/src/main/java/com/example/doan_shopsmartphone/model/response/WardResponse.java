package com.example.doan_shopsmartphone.model.response;


import androidx.annotation.NonNull;

import com.example.doan_shopsmartphone.model.Ward;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WardResponse {
    @SerializedName("results")
    private List<Ward> results;


    public WardResponse() {
    }

    @NonNull
    @Override
    public String toString() {
        return "WardResponse{" +
                "results=" + results +
                '}';
    }

    public List<Ward> getResults() {
        return results;
    }

    public void setResults(List<Ward> results) {
        this.results = results;
    }
}
