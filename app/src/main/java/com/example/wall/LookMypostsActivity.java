package com.example.wall;

import static com.example.wall.LoginActivity.all_username;
import static com.example.wall.UserPostDetailsActivity.from_where_to_pdetail;

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
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class LookMypostsActivity extends BaseActivity {
    ImageView BackCenter;
    SwipeRefreshLayout eSwipeRefreshLayout;
    RecyclerView eRecyclerView;
    PostsBiz postsBiz;
    List<Posts> postsList;
    private int page_numc = 1;

    private final InnerPostsData have_posts = new InnerPostsData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_myposts);
        setTitle("帖子列表");
        initView();
        initEvent();
        get_my_post(1);;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_post, parent, false);
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
                    Intent intent = new Intent(LookMypostsActivity.this, UserPostDetailsActivity.class);
                    Log.d("todetail",this_id);
                    intent.putExtra("post_id", this_id);
                    from_where_to_pdetail = "look_post";
                    startActivity(intent);
                }
            });
            holder.delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View vp = (View) v.getParent();
                    View vpp = (View) vp.getParent();
                    View vppp = (View) vpp.getParent();
                    // 从视图的tag属性中获取ID
                    String this_id = (String) vppp.getTag(R.id.this_post_id);
                    Log.d("todeid",this_id);
                    OkHttpClient client = new OkHttpClient();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd) + "post/delete").newBuilder();
                    //urlBuilder.addQueryParameter("post_id", this_id);
                    String url = urlBuilder.build().toString();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("post_id", this_id)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
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
                                Log.i("回复", responseBody);
                                Gson gson = new Gson();
                                have_posts.delete_ten();
                                get_up_my_post(page_numc);
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
            private final TextView post_time_view;
            public LinearLayout in_post;
            public Button delete_button;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);
                // 初始化帖子视图中的 UI 元素
                titleTextView = itemView.findViewById(R.id.id_tv_my_title);
                contentTextView = itemView.findViewById(R.id.id_tv_my_content);
                ownerTextView = itemView.findViewById(R.id.id_tv_my_author);
                delete_button = itemView.findViewById(R.id.id_btn_my_del);
                in_post = itemView.findViewById(R.id.id_in_my_post);
                post_time_view = itemView.findViewById(R.id.id_my_post_time);
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
        BackCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(LookMypostsActivity.this, UserCenterActivity.class);
                startActivity(intent);
            }
        });



        eSwipeRefreshLayout.setOnRefreshListener(new SwipeRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                have_posts.empty();
                get_up_my_post(1);
                page_numc = 1;
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });

        eSwipeRefreshLayout.setOnPullUpRefreshListener(new SwipeRefreshLayout.OnPullUpRefreshListener() {

            @Override
            public void onPullUpRefresh() {
                page_numc = page_numc + 1;
                get_my_post(page_numc);
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });


    }

    private void initView() {
        BackCenter = findViewById(R.id.id_back_usercenter);
        eSwipeRefreshLayout = findViewById(R.id.id_my_post_swiperefresh);
        eRecyclerView = findViewById(R.id.id_my_post_recyclerview);
        postsBiz = new PostsBiz();
        postsList = new ArrayList<>();
        eSwipeRefreshLayout.setMode(SwipeRefresh.Mode.BOTH);
        eSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.YELLOW, Color.GREEN);
        eRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private void get_my_post(int page_num){
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd) + "post/mypost").newBuilder();
        urlBuilder.addQueryParameter("page_num", String.valueOf(page_num));
        urlBuilder.addQueryParameter("page_size", "10");
        urlBuilder.addQueryParameter("username", all_username);
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
                                eRecyclerView.setLayoutManager(new LinearLayoutManager(LookMypostsActivity.this)); // 使用线性布局
                                eRecyclerView.scrollToPosition(firstVisibleItemPosition);
                            }
                        }
                    });
                }
            }
        });
    }
    private void get_up_my_post(int page_num){
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd) + "post/mypost").newBuilder();
        urlBuilder.addQueryParameter("page_num", String.valueOf(page_num));
        urlBuilder.addQueryParameter("page_size", "10");
        urlBuilder.addQueryParameter("username", all_username);
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
                                eRecyclerView.setLayoutManager(new LinearLayoutManager(LookMypostsActivity.this)); // 使用线性布局
                                eRecyclerView.scrollToPosition(firstVisibleItemPosition);
                            }
                        }
                    });
                }
            }
        });
    }
}
