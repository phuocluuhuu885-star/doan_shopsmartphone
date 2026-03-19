package com.example.doan_shopsmartphone.model.response;

import com.example.doan_shopsmartphone.model.Comment;

public class ListComment1Response {
    private int code;
    private Comment data;
    private String message;

    public ListComment1Response() {
    }

    public ListComment1Response(int code, Comment data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Comment getData() {
        return data;
    }

    public void setData(Comment data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
