package com.example.wall;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;

import java.math.BigDecimal;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {

    private Button mBtnReturn;
    private Button mBtnPost;
    private EditText mEtTitle;
    private EditText mEtText;

    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mBtnReturn = findViewById(R.id.btn_return);
        mBtnPost = findViewById(R.id.btn_post);
        mEtTitle = findViewById(R.id.et_title);
        mEtText = findViewById(R.id.et_text);


        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(PostActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // 地址定位权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE); // 位置
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationListener mLocationListener =  new MyLocationListener();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        Location mlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 网络

        Log.d("111111LoctianActivity>>", "mLocationManager>>:" + mLocationManager);
        Log.d("111111LoctianActivity>>", "mlocation>>:" + mlocation);


        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getIntent().getStringExtra("userName");
                String title = mEtTitle.getText().toString();
                String text = mEtText.getText().toString();
                double location_x = mlocation.getLongitude()*10000;//jingdu
                //location_x = new BigDecimal(location_x).setScale(-1, BigDecimal.ROUND_HALF_UP).doubleValue();
                double location_y = mlocation.getLatitude()*10000;//weidu
                //location_y = new BigDecimal(location_y).setScale(-1, BigDecimal.ROUND_HALF_UP).doubleValue();

                Log.d("111111LoctianActivity>>", "mlocation>>:" + location_x);
                Log.d("111111LoctianActivity>>", "mlocation>>:" + location_y);


                int content_type = 0;
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(text)) {
                    Toast.makeText(getApplicationContext(), "请输入标题或文本", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    OkHttpClient client = new OkHttpClient();

                    final JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("title", title);
                        jsonObject.put("content_type", content_type);
                        jsonObject.put("text", text);
                        jsonObject.put("location_x", location_x);
                        jsonObject.put("location_y", location_y);
                        jsonObject.put("ownername", username);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

// Convert the JSONObject to a String
                    String jsonString = jsonObject.toString();

                    RequestBody jsonRequestBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonString
                    );

// Create a RequestBody instance from the file
                    MediaType mediaType = MediaType.parse("image/*"); // replace "image/*" with the actual file type
                    //RequestBody fileRequestBody = RequestBody.create(file, mediaType);

                    MultipartBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            //.addFormDataPart("file", file.getName(), fileRequestBody)
                            .addFormDataPart("post", jsonString, jsonRequestBody)
                            .build();

                    Request request = new Request.Builder()
                            //.url("http://192.168.43.214:8086/api/post")
                            .url("http://192.168.0.124:8086/api/post")

                            .post(requestBody)
                            .build();


                    okhttp3.Call call = client.newCall(request);
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
                                final String responseBody = response.body().string();
                                Gson gson = new Gson();
                                final PostResponse postresponse = gson.fromJson(responseBody, PostResponse.class);

                                // 在主线程中更新UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                            Intent intent = new Intent(PostActivity.this, PostActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(getApplicationContext(), "已发贴", Toast.LENGTH_SHORT).show();
                                        }

                                    });

                            }
                        }
                    });
                }

            }

        });
        // 更新当前位置
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, new LocationListener() {

            //在用户禁用具有定位功能的硬件时被调用
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            // 位置服务可用
            // 在用户启动具有定位功能的硬件是被调用
            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            //在提供定位功能的硬件状态改变是被调用
            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            // 位置改变
            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
            }
        });





    }
}


