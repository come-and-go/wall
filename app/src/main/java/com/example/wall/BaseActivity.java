package com.example.wall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wall.R;
import com.example.wall.biz.UserBiz;
import com.example.wall.listener.ClickListener;
import com.example.wall.PostActivity;
import com.example.wall.HomeActivity;

/**
 * 所有activity的基本超类，用来抽取常用方法
 */
public class BaseActivity extends AppCompatActivity {
    private ProgressDialog eLoadingDialog;
    private Toolbar toolbar;
    private UserBiz userBiz;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(0xff000000);
        }
        eLoadingDialog = new ProgressDialog(this);
        eLoadingDialog.setMessage("加载中...");
    }
    protected void stopLoadingProgress() {
        if(eLoadingDialog != null && eLoadingDialog.isShowing()) eLoadingDialog.dismiss();
    }
    protected void startLoadingProgress() {
        eLoadingDialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLoadingProgress();
        eLoadingDialog = null;
    }
    protected void toPostActivity() {
        Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
        finish();
    }

    protected void toHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


}
