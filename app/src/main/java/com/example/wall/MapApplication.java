package com.example.wall;


import android.app.Application;
import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps2d.MapsInitializer;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.ServiceSettings;

public class MapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = this;
        //定位隐私政策同意
        AMapLocationClient.updatePrivacyShow(context,true,true);
        AMapLocationClient.updatePrivacyAgree(context,true);

        //搜索隐私政策同意
        ServiceSettings.updatePrivacyShow(context,true,true);
        ServiceSettings.updatePrivacyAgree(context,true);
    }
}

