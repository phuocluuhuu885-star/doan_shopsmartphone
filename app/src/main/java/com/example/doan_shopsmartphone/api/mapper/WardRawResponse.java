package com.example.doan_shopsmartphone.api.mapper;

import com.example.doan_shopsmartphone.api.DTO.WardDTO;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WardRawResponse {

    @SerializedName("data")
    public WardData data;

    public static class WardData {
        @SerializedName("data")
        public List<WardDTO> data;
    }
}
