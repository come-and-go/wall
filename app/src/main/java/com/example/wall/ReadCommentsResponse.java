package com.example.wall;

public class ReadCommentsResponse {
    private int code;
    private String error_msg;
    private InnerCommentsData data;


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

    public InnerCommentsData getData() {
        return data;
    }

    public void setData(InnerCommentsData data) {
        this.data = data;
    }
}
