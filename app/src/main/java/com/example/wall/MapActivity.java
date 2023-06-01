package com.example.wall;

import static com.example.wall.UserPostDetailsActivity.from_where_to_pdetail;
import static com.example.wall.UserHomeActivity.all_distance;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.example.wall.ui.vo.Posts;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private SeekBar seekbar;
    private ImageView refresh;
    private ImageView change_list;
    private TextView textView;
    LatLng l = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        textView = (TextView) findViewById(R.id.map_distance1);
        seekbar = (SeekBar) findViewById(R.id.map_seekbar1);
        change_list = findViewById(R.id.btn_changeList);
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState); //此方法必须重写
        refresh = findViewById(R.id.btn_refresh1);


        // 地址定位权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE); // 位置
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationListener mLocationListener = new MyLocationListener();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        Location mlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 网络

        if (mlocation == null) {
            Toast.makeText(getApplicationContext(), "请开启地理位置权限", Toast.LENGTH_SHORT).show();
            l = new LatLng(39.906901, 116.397972);
        } else {
            l = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
        }
        if(all_distance>2000 || all_distance<100){
            all_distance = 100;
        }
        seekbar.setProgress(all_distance);
        textView.setText(String.valueOf(all_distance));

        aMap = mapView.getMap();

        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(l, 18);
        aMap.moveCamera(cu);

        show_posts();


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE); // 位置
                LocationListener mLocationListener = new MyLocationListener();
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                Location mlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 网络

                l = new LatLng (mlocation.getLatitude(),mlocation.getLongitude());
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(l, aMap.getCameraPosition().zoom);
                aMap.moveCamera(cu);
                aMap.clear();
                show_posts();
            }
        });

        change_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, UserHomeActivity.class);
                startActivity(intent);
            }
        });



        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(mContext, "触碰SeekBar", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(mContext, "放开SeekBar", Toast.LENGTH_SHORT).show();
                all_distance = seekBar.getProgress();
                Log.d("distance",String.valueOf(all_distance));
                textView.setText(String.valueOf(all_distance));



                aMap.clear();
                show_posts();
            }
        });



        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                Log.d("111111MapActivity>>", "mlocation>>:" + title);
                Intent intent = null;
                intent = new Intent(MapActivity.this, UserPostDetailsActivity.class);
                intent.putExtra("post_id", title);
                from_where_to_pdetail = "map";
                startActivity(intent);
                return false;
            }
        });


    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }



    private void show_posts(){


        CircleOptions circleOptions = new CircleOptions();
        circleOptions
                .center(l)
                .radius(all_distance)
                .fillColor(Color.argb(20, 0, 0, 100))
                .strokeColor(Color.argb(100, 0, 0, 100))
                .strokeWidth(4);

        Circle circle = aMap.addCircle(circleOptions);





        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd) + "post").newBuilder();
        urlBuilder.addQueryParameter("page_num", String.valueOf(1));
        urlBuilder.addQueryParameter("page_size", "100");
        urlBuilder.addQueryParameter("location_x", String.valueOf(l.longitude*10000));
        urlBuilder.addQueryParameter("location_y", String.valueOf(l.latitude*10000));
        urlBuilder.addQueryParameter("distance", String.valueOf(all_distance));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败
                Log.i("请求情况：", "请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("响应状态", "响应成功");
                    assert response.body() != null;
                    final String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Log.i("内容", responseBody);
                    final ReadPostsResponse postsResponse = gson.fromJson(responseBody, ReadPostsResponse.class);
                    // 在主线程中更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (postsResponse.getCode() == 0) {
                                // 获取 RecyclerView 实例
                                // 创建帖子数据列表
                                InnerPostsData postsData = postsResponse.getData(); // 解析从后端获取的回复数据，得到帖子列表
                                List<Posts> posts = postsData.getPosts();
                                for (Posts post : posts) {
                                    LatLng latlng = new LatLng(post.getLocation_y()/10000,post.getLocation_x()/10000);
                                    String id = post.getId();
                                    Log.i("内容", id);
                                    MarkerOptions markerOptions = new MarkerOptions().position(latlng)
                                            .draggable(false)
                                            .visible(true)
                                            .title(id)
                                            .anchor(0.5f, 1f);

                                    aMap.addMarker(markerOptions);


                                }

                            }
                        }
                    });
                }
            }
        });
    }



}