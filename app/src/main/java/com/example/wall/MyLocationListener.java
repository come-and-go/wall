package com.example.wall;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Android之获取当前位置的经纬度
 */
public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        // 在这里处理位置更新
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // 当位置服务状态改变时调用
    }

    @Override
    public void onProviderEnabled(String provider) {
        // 当位置服务启用时调用
    }

    @Override
    public void onProviderDisabled(String provider) {
        // 当位置服务禁用时调用
    }
}
