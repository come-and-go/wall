package com.example.wall;

import android.app.Application;

import androidx.annotation.NonNull;

import com.example.wall.http.ReleaseServer;
import com.example.wall.http.RequestHandler;
import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import com.tencent.mmkv.MMKV;

import okhttp3.OkHttpClient;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MMKV.initialize(this);

        // 网络请求框架初始化
        IRequestServer server = new ReleaseServer();


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        EasyConfig.with(okHttpClient)
                // 是否打印日志
                //.setLogEnabled(BuildConfig.DEBUG)
                // 设置服务器配置
                .setServer(server)
                // 设置请求处理策略
                .setHandler(new RequestHandler(this))
                // 设置请求参数拦截器
//                .setInterceptor(new IRequestInterceptor() {
//                    @Override
//                    public void interceptArguments(@NonNull HttpRequest<?> httpRequest,
//                                                   @NonNull HttpParams params,
//                                                   @NonNull HttpHeaders headers) {
//                        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
//                    }
//                })
                // 设置请求重试次数
                .setRetryCount(1)
                // 设置请求重试时间
                .setRetryTime(2000)
//                // 添加全局请求参数
//                .addParam("token", "6666666")
                // 添加全局请求头
                //.addHeader("date", "20191030")
                .into();
    }
}
