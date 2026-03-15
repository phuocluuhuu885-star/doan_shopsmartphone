package com.example.doan_shopsmartphone.api.mapper;

import com.example.doan_shopsmartphone.api.DTO.DistrictDTO;
import com.example.doan_shopsmartphone.api.DTO.ProvinceDTO;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProvinceRawResponse {

    private String name;
    private List<DistrictDTO> districts;

    public List<DistrictDTO> getDistricts() {
        return districts;
    }
}
