package com.example.wall;

import static com.example.wall.LoginActivity.all_username;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wall.biz.PostsBiz;
import com.example.wall.ui.view.SwipeRefresh;

import java.util.ArrayList;

public class ManagerCenterActivity extends AppCompatActivity {

    ImageView ManagerCenter;
    ImageView Home;
    LinearLayout change_password;
    LinearLayout log_out;
    TextView show_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managercenter);
        initView();
        show_name.setText(all_username);
        initEvent();
    }

    private void initEvent() {
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(ManagerCenterActivity.this, ManagerHomeActivity.class);
                startActivity(intent);
            }
        });


        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(ManagerCenterActivity.this, ChangePassActivity.class);

                startActivity(intent);
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(ManagerCenterActivity.this, LoginActivity.class);
                intent.putExtra("type","admin");
                startActivity(intent);
            }
        });
    }

    private void initView() {
        Home = findViewById(R.id.id_m_home);
        ManagerCenter = findViewById(R.id.id_m_usercenter);
        change_password = findViewById(R.id.m_change_my_pass);
        log_out = findViewById(R.id.m_log_out);
        show_name = findViewById(R.id.id_m_center_username);
    }
}