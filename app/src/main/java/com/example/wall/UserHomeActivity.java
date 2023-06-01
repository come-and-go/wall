package com.example.wall;


import static com.example.wall.PostActivity.PERMISSION_REQUEST_CODE;
import static com.example.wall.UserPostDetailsActivity.from_where_to_pdetail;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.LatLng;
import com.example.wall.biz.PostsBiz;
import com.example.wall.ui.view.SwipeRefresh;
import com.example.wall.ui.view.SwipeRefreshLayout;
import com.example.wall.ui.vo.Posts;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserHomeActivity extends BaseActivity {

    ImageView UserCenter;
    ImageView Home;
    ImageView change_to_map;
    ImageView refresh_list;
    ImageView do_post;
    SeekBar seekbar;
    TextView map_distance;
    SwipeRefreshLayout eSwipeRefreshLayout;
    RecyclerView eRecyclerView;
    PostsBiz postsBiz;
    LatLng l = null;
    public static int all_distance;
    List<Posts> postsList;
    private int page_numc = 1;

    private final InnerPostsData have_posts = new InnerPostsData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
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

        setTitle("帖子列表");
        initView();
        initEvent();
        if(all_distance>2000 || all_distance<100){
            all_distance = 100;
        }
        seekbar.setProgress(all_distance);
        map_distance.setText(String.valueOf(all_distance));
        get_data(1);
    }

    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
        private final List<Posts> posts;

        public PostAdapter(List<Posts> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 创建并返回一个用于显示帖子的视图 ViewHolder
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_post, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            // 在 ViewHolder 中设置帖子数据
            Posts post = posts.get(position);
            holder.bind(post);
            holder.in_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View vp = (View) v.getParent();
                    // 从视图的tag属性中获取ID
                    String this_id = (String) vp.getTag(R.id.this_post_id);
                    Intent intent = new Intent(UserHomeActivity.this, UserPostDetailsActivity.class);
                    Log.d("todetail",this_id);
                    intent.putExtra("post_id", this_id);
                    from_where_to_pdetail = "user_home";
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        public static class PostViewHolder extends RecyclerView.ViewHolder {
            private final TextView titleTextView;
            private final TextView contentTextView;
            private final TextView ownerTextView;
            private final TextView post_time_view;
            public LinearLayout in_post;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);
                // 初始化帖子视图中的 UI 元素
                titleTextView = itemView.findViewById(R.id.id_user_tv_title);
                contentTextView = itemView.findViewById(R.id.id_user_tv_content);
                ownerTextView = itemView.findViewById(R.id.id_user_tv_author);
                in_post = itemView.findViewById(R.id.id_user_in_post);
                post_time_view = itemView.findViewById(R.id.id_user_tv_post_time);
            }

            public void bind(Posts post) {
                // 在视图中设置帖子数据
                titleTextView.setText(post.getTitle());
                contentTextView.setText(post.getContext());
                ownerTextView.setText(post.getOwner());
                post_time_view.setText(post.getTime());
                itemView.setTag(R.id.this_post_id, post.getId());
            }
        }
    }

    private void initEvent() {

        refresh_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE); // 位置
                LocationListener mLocationListener = new MyLocationListener();
                if (ActivityCompat.checkSelfPermission(UserHomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserHomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                Location mlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 网络

                l = new LatLng (mlocation.getLatitude(),mlocation.getLongitude());
                have_posts.empty();
                get_data(1);
                page_numc = 1;
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });

        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                have_posts.empty();
                get_data(1);
                page_numc = 1;
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);

            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                all_distance = seekBar.getProgress();
                Log.d("distance",String.valueOf(all_distance));
                map_distance.setText(String.valueOf(all_distance));
                have_posts.empty();
                page_numc = 1;
                get_up_data(page_numc);
            }
        });


        UserCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(UserHomeActivity.this, UserCenterActivity.class);
                startActivity(intent);
            }
        });
        do_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(UserHomeActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        change_to_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(UserHomeActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        eSwipeRefreshLayout.setOnRefreshListener(new SwipeRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                have_posts.empty();
                get_up_data(1);
                page_numc = 1;
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });

        eSwipeRefreshLayout.setOnPullUpRefreshListener(new SwipeRefreshLayout.OnPullUpRefreshListener() {

            @Override
            public void onPullUpRefresh() {
                page_numc = page_numc + 1;
                get_data(page_numc);
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateList(List<Posts> response) {
        postsList.clear();
        postsList.addAll(response);
        eSwipeRefreshLayout.setRefreshing(false);
        eSwipeRefreshLayout.setPullUpRefreshing(false);
    }

    private void initView() {
        change_to_map = findViewById(R.id.btn_changeMap);
        refresh_list = findViewById(R.id.id_refresh);
        seekbar = findViewById(R.id.map_seekbar);
        do_post = findViewById(R.id.id_newPost);
        map_distance = findViewById(R.id.map_distance);
        Home = findViewById(R.id.id_userHome);
        UserCenter = findViewById(R.id.id_userCenter);
        eSwipeRefreshLayout = findViewById(R.id.id_userhome_swiperefresh);
        eRecyclerView = findViewById(R.id.id_userhome_recyclerview);
        postsBiz = new PostsBiz();
        postsList = new ArrayList<>();
        eSwipeRefreshLayout.setMode(SwipeRefresh.Mode.BOTH);
        eSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.YELLOW, Color.GREEN);
        eRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private void get_data(int page_num){
        if(page_num == 1){
            have_posts.empty();
        }
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd) + "post").newBuilder();
        urlBuilder.addQueryParameter("page_num", String.valueOf(page_num));
        urlBuilder.addQueryParameter("page_size", "10");
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
                    final ReadPostsResponse postsResponse = gson.fromJson(responseBody, ReadPostsResponse.class);
                    // 在主线程中更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (postsResponse.getCode() == 0) {
                                // 获取 RecyclerView 实例
                                // 创建帖子数据列表
                                InnerPostsData posts = postsResponse.getData(); // 解析从后端获取的回复数据，得到帖子列表
                                int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(eRecyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();

                                // 创建适配器并设置给 RecyclerView
                                PostAdapter adapter = new PostAdapter(have_posts.add(posts));
                                eRecyclerView.setAdapter(adapter);
                                // 设置布局管理器，可以选择线性布局、网格布局等
                                eRecyclerView.setLayoutManager(new LinearLayoutManager(UserHomeActivity.this)); // 使用线性布局
                                eRecyclerView.scrollToPosition(firstVisibleItemPosition);
                            }
                        }
                    });
                }
            }
        });
    }
    private void get_up_data(int page_num){
        if(page_num == 1){
            have_posts.empty();
        }
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd) + "post").newBuilder();
        urlBuilder.addQueryParameter("page_num", String.valueOf(page_num));
        urlBuilder.addQueryParameter("page_size", "10");
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
                    final ReadPostsResponse postsResponse = gson.fromJson(responseBody, ReadPostsResponse.class);
                    // 在主线程中更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (postsResponse.getCode() == 0) {
                                // 获取 RecyclerView 实例
                                // 创建帖子数据列表
                                InnerPostsData posts = postsResponse.getData(); // 解析从后端获取的回复数据，得到帖子列表
                                int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(eRecyclerView.getLayoutManager())).findFirstVisibleItemPosition();

                                // 创建适配器并设置给 RecyclerView
                                PostAdapter adapter = new PostAdapter(have_posts.add(posts));
                                eRecyclerView.setAdapter(adapter);
                                // 设置布局管理器，可以选择线性布局、网格布局等
                                eRecyclerView.setLayoutManager(new LinearLayoutManager(UserHomeActivity.this)); // 使用线性布局
                                eRecyclerView.scrollToPosition(firstVisibleItemPosition);
                            }
                        }
                    });
                }
            }
        });
    }
}