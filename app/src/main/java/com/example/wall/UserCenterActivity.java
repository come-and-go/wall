package com.example.wall;

import static com.example.wall.LoginActivity.all_username;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
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

public class UserCenterActivity extends AppCompatActivity {

    ImageView UserCenter;
    ImageView Home;
    LinearLayout myPosts;
    LinearLayout myComments;
    LinearLayout change_password;
    LinearLayout log_out;
    TextView show_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        initView();
        show_name.setText(all_username);
        initEvent();
    }

    private void initEvent() {
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(UserCenterActivity.this, UserHomeActivity.class);
                startActivity(intent);
            }
        });


        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(UserCenterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(UserCenterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(UserCenterActivity.this, LookMypostsActivity.class);
                startActivity(intent);
            }
        });

        myComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(UserCenterActivity.this, LookMycommentsActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        Home = findViewById(R.id.id_u_userhome);
        UserCenter = findViewById(R.id.id_u_usercenter);
        change_password = findViewById(R.id.u_change_my_pass);
        log_out = findViewById(R.id.u_log_out);
        show_name = findViewById(R.id.id_u_center_username);
        myPosts = findViewById(R.id.tv_my_posts);
        myComments = findViewById(R.id.tv_my_comments);
    }
}