package com.example.wall;

import static com.example.wall.LoginActivity.all_username;
import static com.example.wall.UserPostDetailsActivity.from_where_to_pdetail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.example.wall.ui.view.SwipeRefresh;
import com.example.wall.ui.view.SwipeRefreshLayout;
import com.example.wall.ui.vo.CommentForPost;
import com.example.wall.ui.vo.Posts;
//import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
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

public class LookMycommentsActivity extends BaseActivity {

    ImageView Back_Center;

    SwipeRefreshLayout eSwipeRefreshLayout;
    RecyclerView eRecyclerView;
    List<CommentForPost> CommentsList;
    private int page_numc = 1;
    private final InnerCommentsData have_showed = new InnerCommentsData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_mycomments);
        setTitle("我的评论");
        initView();
        initEvent();
        get_my_comment(1);
    }

    public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<CommentForPost> comments;

        public CommentAdapter(List<CommentForPost> comments) {
            this.comments = comments;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                // 创建普通Item的ViewHolder
            View normalView = inflater.inflate(R.layout.item_my_comment, parent, false);
            return new NormalCommentViewHolder(normalView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                // 绑定普通Item的数据到ViewHolder
            int commentPosition = position;
            CommentForPost comment = comments.get(commentPosition);
            NormalCommentViewHolder normalHolder = (NormalCommentViewHolder) holder;
            normalHolder.bind(comment);
            normalHolder.delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View vp = (View) v.getParent();
                    View vpp = (View) vp.getParent();
                    View vppp = (View) vpp.getParent();
                    // 从视图的tag属性中获取ID
                    String comment_id = (String) vppp.getTag(R.id.id_my_comment_id);
                    Log.d("todeid",comment_id);
                    OkHttpClient client = new OkHttpClient();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd) + "comment/delete").newBuilder();
                    //urlBuilder.addQueryParameter("post_id", this_id);
                    String url = urlBuilder.build().toString();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("comment_id", comment_id)
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
                                have_showed.delete_ten();
                                get_up_my_comment(page_numc);
                            }
                        }
                    });
                }
            });
            normalHolder.find_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View vp = (View) v.getParent();
                    // 从视图的tag属性中获取ID
                    String parent_id = (String) vp.getTag(R.id.id_my_comment_parent_id);
                    Log.d("parent_id", parent_id);
                    Intent intent = new Intent(LookMycommentsActivity.this, UserPostDetailsActivity.class);
                    intent.putExtra("post_id", parent_id);
                    from_where_to_pdetail = "look_comment";
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        // 普通Item的ViewHolder
        private class NormalCommentViewHolder extends RecyclerView.ViewHolder {
            // 定义普通Item的视图组件

            private final SimpleExoPlayer player;
            private final PlayerView playerView;
            private final TextView contentTextView;
            private final ImageView contentImageView;
            private final TextView ParentTextView;
            private final TextView post_time_view;
            private final LinearLayout find_parent;
            public Button delete_button;
            public NormalCommentViewHolder(View itemView) {
                super(itemView);
                player = ExoPlayerFactory.newSimpleInstance(itemView.getContext());
                playerView = itemView.findViewById(R.id.id_my_comment_video);
                playerView.setPlayer(player);
                contentTextView = itemView.findViewById(R.id.id_my_comment_content);
                contentImageView = itemView.findViewById(R.id.id_my_comment_image);
                ParentTextView = itemView.findViewById(R.id.id_my_comment_parent);
                delete_button = itemView.findViewById(R.id.id_my_comment_delete);
                find_parent = itemView.findViewById(R.id.id_my_comment_id);
                post_time_view = itemView.findViewById(R.id.id_my_comment_post_time);
            }

            public void bind(CommentForPost comment) {
                contentTextView.setText(comment.getContext());
                if(comment.getContent_type() == 1) {
                    String url = comment.getMedia_url();
                    if(url.contains("mp4")){
                        Log.d("vediourl", url);
                        playerView.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) playerView.getLayoutParams();
                        layoutParams.width = 1000; // 设置宽度
                        layoutParams.height = 2000; // 设置高度
                        playerView.setLayoutParams(layoutParams);

                        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(itemView.getContext(), "wall"));
                        // 创建视频媒体源
                        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));
                        // 准备视频播放器
                        player.prepare(videoSource);
                        // 开始播放视频
                        player.setPlayWhenReady(true);

                    }
                    else {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(url)
                                .override(1000, 1000)//图片大小
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                                        Drawable drawable = new BitmapDrawable(getApplicationContext().getResources(), resource);
                                        contentImageView.setImageDrawable(drawable);
                                        contentImageView.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }


                                });
                    }
                }
                ParentTextView.setText(comment.getParent_post_id());
                post_time_view.setText(comment.getDeliver_time());
                itemView.setTag(R.id.id_my_comment_id, comment.getComment_id());
                itemView.setTag(R.id.id_my_comment_parent_id, comment.getParent_post_id());
            }
        }
    }


    private void initView() {
        Back_Center = findViewById(R.id.id_back_center);
        eSwipeRefreshLayout = findViewById(R.id.id_my_comment_swiperefresh);
        eRecyclerView = findViewById(R.id.id_my_comment_recyclerview);
        CommentsList = new ArrayList<>();
        eSwipeRefreshLayout.setMode(SwipeRefresh.Mode.BOTH);
        eSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.YELLOW, Color.GREEN);
        eRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    private void initEvent() {
        Back_Center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(LookMycommentsActivity.this, UserCenterActivity.class);
                startActivity(intent);
            }
        });
        eSwipeRefreshLayout.setOnRefreshListener(new SwipeRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                have_showed.empty();
                get_up_my_comment(1);
                page_numc = 1;
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });

        eSwipeRefreshLayout.setOnPullUpRefreshListener(new SwipeRefreshLayout.OnPullUpRefreshListener() {

            @Override
            public void onPullUpRefresh() {
                page_numc = page_numc + 1;
                get_my_comment(page_numc);
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });


    }
    private void get_my_comment(int page_num) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd)+ "comment/mycomment").newBuilder();
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
                    final ReadCommentsResponse commentsResponse = gson.fromJson(responseBody, ReadCommentsResponse.class);

                    // 在主线程中更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (commentsResponse.getCode() == 0) {
                                InnerCommentsData comments = commentsResponse.getData(); // 解析从后端获取的回复数据，得到评论列表
                                int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(eRecyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                                // 创建适配器并设置给 RecyclerView
                                CommentAdapter adapter = new CommentAdapter(have_showed.add(comments));
                                eRecyclerView.setAdapter(adapter);
                                // 设置布局管理器，可以选择线性布局、网格布局等
                                eRecyclerView.setLayoutManager(new LinearLayoutManager(LookMycommentsActivity.this)); // 使用线性布局
                                eRecyclerView.scrollToPosition(firstVisibleItemPosition);
                            }
                        }
                    });
                }
            }
        });
    }
    private void get_up_my_comment(int page_num) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd) + "comment/mycomment").newBuilder();
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
                    final ReadCommentsResponse commentsResponse = gson.fromJson(responseBody, ReadCommentsResponse.class);

                    // 在主线程中更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (commentsResponse.getCode() == 0) {
                                InnerCommentsData comments = commentsResponse.getData(); // 解析从后端获取的回复数据，得到评论列表
                                int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(eRecyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                                // 创建适配器并设置给 RecyclerView
                                CommentAdapter adapter = new CommentAdapter(have_showed.add(comments));
                                eRecyclerView.setAdapter(adapter);
                                // 设置布局管理器，可以选择线性布局、网格布局等
                                eRecyclerView.setLayoutManager(new LinearLayoutManager(LookMycommentsActivity.this)); // 使用线性布局
                                eRecyclerView.scrollToPosition(firstVisibleItemPosition);
                            }
                        }
                    });
                }
            }
        });
    }
}