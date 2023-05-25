package com.example.wall;

import com.google.gson.JsonObject;
import com.example.wall.ui.vo.Posts;

import java.util.List;

public class ReadPostsResponse {
    private int code;
    private String error_msg;
    private InnerPostsData data;


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

    public InnerPostsData getData() {
        return data;
    }

    public void setData(InnerPostsData data) {
        this.data = data;
    }

}
