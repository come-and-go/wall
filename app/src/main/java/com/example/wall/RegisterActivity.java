package com.example.wall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wall.utils.SHA256;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private Button mBtnConfirm;
    private Button mBtnReturn;
    private EditText mEtUsername;
    private  EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mBtnConfirm= findViewById(R.id.btn_confirm);
        mBtnReturn = findViewById(R.id.btn_return);
        mEtUsername = findViewById(R.id.et_r1);
        mEtPassword = findViewById(R.id.et_r2);


        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEtUsername.getText().toString();
                String password = mEtPassword.getText().toString();
                SHA256 sha = new SHA256();
                password = sha.getSHA256(password);
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    OkHttpClient client = new OkHttpClient();
                    //FormBody.Builder params = new FormBody.Builder();
                    //params.add("username", username);
                    //params.add("password", password);
                    final JSONObject jsonObject = new JSONObject();

                    try {//提交的参数
                        jsonObject.put("username",username);
                        jsonObject.put("password",password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MediaType mMediaType = MediaType.parse("application/json; charset=utf-8");
                    final RequestBody requestBody = RequestBody.create(mMediaType, jsonObject.toString());


                    Request request = new Request.Builder()
                            //.post(params.build())
                            .post(requestBody)
                            .url(getResources().getString(R.string.ipadd) + "user/register")
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
                                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                                            Intent intent = null;
                                            intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "账号已存在", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });


                }


            }
        });



        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}