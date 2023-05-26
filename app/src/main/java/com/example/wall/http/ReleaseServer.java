package com.example.wall.http;

import androidx.annotation.NonNull;

import com.hjq.http.config.IRequestServer;

public class ReleaseServer implements IRequestServer {
    @NonNull
    @Override
    public String getHost() {
        return "http://192.168.0.124:8086";
    }
}
