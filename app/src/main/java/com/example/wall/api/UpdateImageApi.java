package com.example.wall.api;

import androidx.annotation.NonNull;

import com.hjq.http.annotation.HttpRename;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.config.IRequestType;
import com.hjq.http.model.BodyType;

import java.io.File;

public final class UpdateImageApi implements IRequestApi , IRequestType {//IRequestServer,
//    @NonNull
//    @Override
//    public String getHost() {
//        return "http://192.168.0.124:8086";
//    }
//
//    @NonNull
//    @Override
//    public BodyType getBodyType() {
//        // 上传文件需要使用表单的形式提交
//        return BodyType.FORM;
//    }

    @NonNull
    @Override
    public String getApi() {
        return "/api/post";
    }


    /** 本地图片 */
    private File image;

    public UpdateImageApi(File image) {
        this.image = image;
    }

    public UpdateImageApi() {
    }

    public UpdateImageApi setImage(File image) {
        this.image = image;
        return this;
    }

    @NonNull
    @Override
    public BodyType getBodyType() {
        return BodyType.FORM;
    }
}
