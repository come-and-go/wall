package com.example.wall;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class LoginActivity extends AppCompatActivity {
    //声明控件
    private Button mBtnLogin;
    private Button mBtnRegister;
    private EditText mEtUsername;
    private  EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //找到控件
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);
        mEtUsername = findViewById(R.id.et_1);
        mEtPassword = findViewById(R.id.et_2);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEtUsername.getText().toString();
                String password = mEtPassword.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    OkHttpClient client = new OkHttpClient();
                    //FormBody.Builder params = new FormBody.Builder();
                    //params.add("userName", username);
                    //params.add("password", password);
                    final JSONObject jsonObject = new JSONObject();

                    try {//提交的参数
                        jsonObject.put("userName",username);
                        jsonObject.put("password",password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MediaType mMediaType = MediaType.parse("application/json; charset=utf-8");
                    final RequestBody requestBody = RequestBody.create(mMediaType, jsonObject.toString());


                    Request request = new Request.Builder()
                            //.post(params.build())
                            .post(requestBody)
                            .url("http://192.168.0.124:8086/api/user/login")

                            //.url("http://192.168.43.214:8086/api/user/login")
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
                                final LoginResponse loginresponse = gson.fromJson(responseBody, LoginResponse.class);

                                // 在主线程中更新UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (loginresponse.getCode() == 0) {
                                            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, PostActivity.class);
                                            intent.putExtra("userName", username);
                                            startActivity(intent);
                                        }

                                        else if (loginresponse.getCode() == 1) {
                                            Toast.makeText(getApplicationContext(), "管理员登录成功", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, PostActivity.class);
                                            //intent.putExtra("userName", username);
                                            startActivity(intent);
                                        }

                                        else {
                                            Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });


                }


            }
        });


        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }




}
