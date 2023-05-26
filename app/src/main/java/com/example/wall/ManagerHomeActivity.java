package com.example.wall;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wall.biz.PostsBiz;
import com.example.wall.net.CommonCallback;
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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ManagerHomeActivity extends BaseActivity {
    ImageView ManagerCenter;
    ImageView Home;
    TextView tv_all;
    TextView tv_unchecked;
    TextView tv_checked;
    SwipeRefreshLayout eSwipeRefreshLayout;
    RecyclerView eRecyclerView;
    PostsBiz postsBiz;
    List<Posts> postsList;
    private int page_numc = 1;
    private int if_checked = 0;
    private int if_all = 1;

    private final InnerPostsData have_posts = new InnerPostsData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managerhome);
        setTitle("问题管理");
        initView();
        initEvent();
        loadAll();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            // 在 ViewHolder 中设置帖子数据
            Posts post = posts.get(position);
            holder.bind(post);
            holder.delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View itemView = (View) v.getParent();
                    // 从视图的tag属性中获取ID
                    String this_id = (String) itemView.getTag();
                    OkHttpClient client = new OkHttpClient();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.0.124:8086/api/post/delete").newBuilder();
                    urlBuilder.addQueryParameter("post_id", this_id);
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
                                if(if_all == 1){
                                    get_data(page_numc);
                                }
                                else{
                                    get_cuc_data(page_numc, if_checked);
                                }

                            }
                        }
                    });
                }
            });
            holder.check_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View itemView = (View) v.getParent();
                    String this_id = (String) itemView.getTag();
                    OkHttpClient client = new OkHttpClient();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.0.124:8086/api/post/check").newBuilder();
                    urlBuilder.addQueryParameter("post_id", this_id);
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
                                if(if_all == 1){
                                    get_data(page_numc);
                                }
                                else{
                                    get_cuc_data(page_numc, if_checked);
                                }

                            }
                        }
                    });
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
            public Button delete_button;
            public Button check_button;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);
                // 初始化帖子视图中的 UI 元素
                titleTextView = itemView.findViewById(R.id.id_tv_title);
                contentTextView = itemView.findViewById(R.id.id_tv_content);
                ownerTextView = itemView.findViewById(R.id.id_tv_author);
                delete_button = itemView.findViewById(R.id.id_btn_del);
                check_button = itemView.findViewById(R.id.id_btn_jug);
            }

            public void bind(Posts post) {
                // 在视图中设置帖子数据
                titleTextView.setText(post.getTitle());
                contentTextView.setText(post.getContext());
                ownerTextView.setText(post.getOwner());
                itemView.setTag(post.getId());
            }
        }
    }

    private void initEvent() {
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toHomeActivity();
            }
        });


        tv_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_checked.setTextColor(getResources().getColor(R.color.purple));
                tv_unchecked.setTextColor(getResources().getColor(R.color.black));
                tv_all.setTextColor(getResources().getColor(R.color.black));
                if_all = 0;
                have_posts.empty();
                if_checked = 1;
                get_cuc_data(1, if_checked);
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });

        tv_unchecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_unchecked.setTextColor(getResources().getColor(R.color.purple));
                tv_checked.setTextColor(getResources().getColor(R.color.black));
                tv_all.setTextColor(getResources().getColor(R.color.black));
                if_all = 0;
                have_posts.empty();
                if_checked = 0;
                get_cuc_data(1, if_checked);
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });
        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_all.setTextColor(getResources().getColor(R.color.purple));
                tv_unchecked.setTextColor(getResources().getColor(R.color.black));
                tv_checked.setTextColor(getResources().getColor(R.color.black));
                if_all = 1;
                have_posts.empty();
                get_data(1);
                page_numc = 1;
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);

            }
        });

        ManagerCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(ManagerHomeActivity.this, ManagerCenterActivity.class);
                startActivity(intent);
            }
        });

        eSwipeRefreshLayout.setOnRefreshListener(new SwipeRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                have_posts.empty();
                if(if_all == 1) {
                    get_up_data(1);
                }
                else{
                    get_up_cuc_data(1, if_checked);
                }
                page_numc = 1;
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });

        eSwipeRefreshLayout.setOnPullUpRefreshListener(new SwipeRefreshLayout.OnPullUpRefreshListener() {

            @Override
            public void onPullUpRefresh() {
                page_numc = page_numc + 1;
                if(if_all == 1) {
                    get_data(page_numc);
                }
                else{
                    get_cuc_data(page_numc, if_checked);
                }
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
        tv_all = findViewById(R.id.id_tv_all);
        tv_checked = findViewById(R.id.id_tv_checked);
        tv_unchecked = findViewById(R.id.id_tv_unchecked);
        Home = findViewById(R.id.id_Home);
        ManagerCenter = findViewById(R.id.id_userCenter);
        eSwipeRefreshLayout = findViewById(R.id.id_swiperefresh);
        eRecyclerView = findViewById(R.id.id_recyclerview);
        postsBiz = new PostsBiz();
        postsList = new ArrayList<>();
        eSwipeRefreshLayout.setMode(SwipeRefresh.Mode.BOTH);
        eSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.YELLOW, Color.GREEN);
        eRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private void get_data(int page_num){
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.0.124:8086/api/post").newBuilder();
        urlBuilder.addQueryParameter("page_num", String.valueOf(page_num));
        urlBuilder.addQueryParameter("page_size", "10");
        urlBuilder.addQueryParameter("location_x", "0.0");
        urlBuilder.addQueryParameter("location_y", "0.0");
        urlBuilder.addQueryParameter("distance", String.valueOf(1.0E20));
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
                                InnerPostsData posts = postsResponse.getData(); // 解析从后端获取的回复数据，得到帖子列表
                                int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(eRecyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();

                                // 创建适配器并设置给 RecyclerView
                                PostAdapter adapter = new PostAdapter(have_posts.add(posts));
                                eRecyclerView.setAdapter(adapter);
                                // 设置布局管理器，可以选择线性布局、网格布局等
                                eRecyclerView.setLayoutManager(new LinearLayoutManager(ManagerHomeActivity.this)); // 使用线性布局
                                eRecyclerView.scrollToPosition(firstVisibleItemPosition);
                            }
                        }
                    });
                }
            }
        });
    }
    private void get_up_data(int page_num){
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.0.124:8086/api/post").newBuilder();
        urlBuilder.addQueryParameter("page_num", String.valueOf(page_num));
        urlBuilder.addQueryParameter("page_size", "10");
        urlBuilder.addQueryParameter("location_x", "0.0");
        urlBuilder.addQueryParameter("location_y", "0.0");
        urlBuilder.addQueryParameter("distance", String.valueOf(1.0E20));
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
                                InnerPostsData posts = postsResponse.getData(); // 解析从后端获取的回复数据，得到帖子列表
                                int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(eRecyclerView.getLayoutManager())).findFirstVisibleItemPosition();

                                // 创建适配器并设置给 RecyclerView
                                PostAdapter adapter = new PostAdapter(have_posts.add(posts));
                                eRecyclerView.setAdapter(adapter);
                                // 设置布局管理器，可以选择线性布局、网格布局等
                                eRecyclerView.setLayoutManager(new LinearLayoutManager(ManagerHomeActivity.this)); // 使用线性布局
                                eRecyclerView.scrollToPosition(firstVisibleItemPosition);
                            }
                        }
                    });
                }
            }
        });
    }
    private void get_cuc_data(int page_num, int if_checked){
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.0.124:8086/api/post/check").newBuilder();
        urlBuilder.addQueryParameter("page_num", String.valueOf(page_num));
        urlBuilder.addQueryParameter("page_size", "10");
        urlBuilder.addQueryParameter("checked", String.valueOf(if_checked));
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
                                InnerPostsData posts = postsResponse.getData(); // 解析从后端获取的回复数据，得到帖子列表
                                int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(eRecyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();

                                // 创建适配器并设置给 RecyclerView
                                PostAdapter adapter = new PostAdapter(have_posts.add(posts));
                                eRecyclerView.setAdapter(adapter);
                                // 设置布局管理器，可以选择线性布局、网格布局等
                                eRecyclerView.setLayoutManager(new LinearLayoutManager(ManagerHomeActivity.this)); // 使用线性布局
                                eRecyclerView.scrollToPosition(firstVisibleItemPosition);
                            }
                        }
                    });
                }
            }
        });
    }
    private void get_up_cuc_data(int page_num, int if_checked){
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.0.124:8086/api/post/check").newBuilder();
        urlBuilder.addQueryParameter("page_num", String.valueOf(page_num));
        urlBuilder.addQueryParameter("page_size", "10");
        urlBuilder.addQueryParameter("checked", String.valueOf(if_checked));
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
                                InnerPostsData posts = postsResponse.getData(); // 解析从后端获取的回复数据，得到帖子列表
                                int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(eRecyclerView.getLayoutManager())).findFirstVisibleItemPosition();

                                // 创建适配器并设置给 RecyclerView
                                PostAdapter adapter = new PostAdapter(have_posts.add(posts));
                                eRecyclerView.setAdapter(adapter);
                                // 设置布局管理器，可以选择线性布局、网格布局等
                                eRecyclerView.setLayoutManager(new LinearLayoutManager(ManagerHomeActivity.this)); // 使用线性布局
                                eRecyclerView.scrollToPosition(firstVisibleItemPosition);
                            }
                        }
                    });
                }
            }
        });
    }
    private void loadAll() {
        startLoadingProgress();
        postsBiz.getAllOfAllPosts(new CommonCallback<List<Posts>>() {
            @Override
            public void onError(Exception e) {
                stopLoadingProgress();
                //T.showToast(e.getMessage());
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Posts> response) {
                stopLoadingProgress();
                //T.showToast("刷新成功！");
                updateList(response);
            }
        });
    }
}
