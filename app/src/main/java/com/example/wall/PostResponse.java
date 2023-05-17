package com.example.wall;

import com.google.gson.JsonObject;


public class PostResponse {
    private int code;
    private String error_msg;
    private PostData data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

}