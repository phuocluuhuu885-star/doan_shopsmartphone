package com.example.doan_shopsmartphone.api.mapper;

import com.example.doan_shopsmartphone.api.DTO.DistrictDTO;
import com.example.doan_shopsmartphone.api.DTO.WardDTO;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DistrictRawResponse {

    private String name;
    private List<WardDTO> wards;

    public List<WardDTO> getWards() {
        return wards;
    }
}
