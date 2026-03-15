package com.example.doan_shopsmartphone.api.DTO;

public class DistrictDTO {
    public int code;
    public String name;

    public DistrictDTO(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

