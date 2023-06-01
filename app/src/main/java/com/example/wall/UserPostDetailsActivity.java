package com.example.wall;

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
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

public class UserPostDetailsActivity extends BaseActivity {

    String post_id;
    public static String from_where_to_pdetail;
    ImageView Back_Image;
    TextView post_title;
    Button add_comment;

    SwipeRefreshLayout eSwipeRefreshLayout;
    RecyclerView eRecyclerView;
    List<CommentForPost> CommentsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_details);
        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        setTitle("帖子详情");
        initView();
        initEvent();
        get_post_comment(post_id);

    }

    public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_SPECIAL = 0; // 特殊Item的视图类型
        private static final int VIEW_TYPE_NORMAL = 1; // 普通Item的视图类型

        private List<CommentForPost> comments;
        private Posts inner_post;

        public CommentAdapter(List<CommentForPost> comments, Posts inner_post) {
            this.comments = comments;
            this.inner_post = inner_post;
        }

        public void setSpecialComment(Posts inner_post) {
            this.inner_post = inner_post;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 && inner_post != null) {
                return VIEW_TYPE_SPECIAL; // 返回特殊Item的视图类型
            } else {
                return VIEW_TYPE_NORMAL; // 返回普通Item的视图类型
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == VIEW_TYPE_SPECIAL) {
                // 创建特殊Item的ViewHolder
                View specialView = inflater.inflate(R.layout.item_inner_post, parent, false);
                return new SpecialCommentViewHolder(specialView);
            } else {
                // 创建普通Item的ViewHolder
                View normalView = inflater.inflate(R.layout.item_comment_user, parent, false);
                return new NormalCommentViewHolder(normalView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SpecialCommentViewHolder) {
                // 绑定特殊Item的数据到ViewHolder
                SpecialCommentViewHolder specialHolder = (SpecialCommentViewHolder) holder;
                specialHolder.bind(inner_post);
            } else if (holder instanceof NormalCommentViewHolder) {
                // 绑定普通Item的数据到ViewHolder
                int commentPosition = position;
                if (inner_post != null) {
                    // 减去特殊Item的位置偏移量
                    commentPosition--;
                }
                CommentForPost comment = comments.get(commentPosition);
                NormalCommentViewHolder normalHolder = (NormalCommentViewHolder) holder;
                normalHolder.bind(comment);
            }
        }

        @Override
        public int getItemCount() {
            int count = comments.size();
            if (inner_post != null) {
                count++; // 如果特殊Item存在，则增加一个Item数量
            }
            return count;
        }
        // 特殊Item的ViewHolder
        private class SpecialCommentViewHolder extends RecyclerView.ViewHolder {

            // 定义特殊Item的视图组件
            private final SimpleExoPlayer player;
            private final PlayerView playerView;
            private final TextView contentTextView;
            private final ImageView contentImageView;
            private final TextView ownerTextView;
            private final TextView post_time_view;
            public SpecialCommentViewHolder(View itemView) {
                super(itemView);
                // 初始化特殊Item的视图组件
                player = ExoPlayerFactory.newSimpleInstance(itemView.getContext());
                playerView = itemView.findViewById(R.id.id_m_in_post_video);
                playerView.setPlayer(player);

                contentTextView = itemView.findViewById(R.id.id_m_in_post_content);
                contentImageView = itemView.findViewById(R.id.id_m_in_post_image);
                ownerTextView = itemView.findViewById(R.id.id_m_in_post_author);
                post_time_view = itemView.findViewById(R.id.id_m_in_post_time);
            }

            public void bind(Posts inner_post) {
                // 根据特殊Item的数据设置视图组件的内容
                contentTextView.setText(inner_post.getContext());
                if(inner_post.getContent_type() == 1) {
                    String url = inner_post.getMedia_url();
                    if(url.contains("mp4")){
                        Log.d("vediourl", url);
                        playerView.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) playerView.getLayoutParams();
                        layoutParams.width = 1000; // 设置宽度
                        layoutParams.height = 2000; // 设置高度
                        playerView.setLayoutParams(layoutParams);
                        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(itemView.getContext(), "wall"));
                         //创建视频媒体源
                        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));
                         //准备视频播放器
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
                ownerTextView.setText(inner_post.getOwner());
                post_time_view.setText(inner_post.getTime());
            }
        }

        // 普通Item的ViewHolder
        private class NormalCommentViewHolder extends RecyclerView.ViewHolder {
            // 定义普通Item的视图组件

            private final SimpleExoPlayer player;
            private final PlayerView playerView;
            private final TextView contentTextView;
            private final ImageView contentImageView;
            private final TextView ownerTextView;
            private final TextView post_time_view;
            public NormalCommentViewHolder(View itemView) {
                super(itemView);
                player = ExoPlayerFactory.newSimpleInstance(itemView.getContext());
                playerView = itemView.findViewById(R.id.id_u_comment_video);
                playerView.setPlayer(player);

                contentTextView = itemView.findViewById(R.id.id_u_comment_content);
                contentImageView = itemView.findViewById(R.id.id_u_comment_image);
                ownerTextView = itemView.findViewById(R.id.id_u_comment_author);
                post_time_view = itemView.findViewById(R.id.id_u_comment_post_time);
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
//                        // 开始播放视频
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
                ownerTextView.setText(comment.getOwner());
                post_time_view.setText(comment.getDeliver_time());
                itemView.setTag(R.id.id_u_comment_id, comment.getComment_id());
            }
        }
    }

    private void get_post_comment(String post_id) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd) + "post").newBuilder();
        urlBuilder.addQueryParameter("post_id", post_id);
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
                    final PostResponse postResponse = gson.fromJson(responseBody, PostResponse.class);
                    final Posts this_post = postResponse.getData();

                    // 获取评论
                    OkHttpClient client = new OkHttpClient();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(getResources().getString(R.string.ipadd) + "comment").newBuilder();
                    urlBuilder.addQueryParameter("page_num", "1");
                    urlBuilder.addQueryParameter("page_size", String.valueOf(1000000));
                    urlBuilder.addQueryParameter("post_id", post_id);
                    String url = urlBuilder.build().toString();

                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Call call1 = client.newCall(request);
                    call1.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call1, IOException e) {
                            //请求失败
                            Log.i("请求情况：", "请求失败");
                        }

                        @Override
                        public void onResponse(Call call1, Response response) throws IOException {
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
                                            post_title.setText(this_post.getTitle());
                                            InnerCommentsData comments = commentsResponse.getData(); // 解析从后端获取的回复数据，得到评论列表
                                            // 创建适配器并设置给 RecyclerView
                                            CommentAdapter adapter = new CommentAdapter(comments.getComments(), this_post);
                                            eRecyclerView.setAdapter(adapter);
                                            // 设置布局管理器，可以选择线性布局、网格布局等
                                            eRecyclerView.setLayoutManager(new LinearLayoutManager(UserPostDetailsActivity.this)); // 使用线性布局
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
    private void initView() {
        Back_Image = findViewById(R.id.id_u_back_list);
        post_title = findViewById(R.id.id_u_detail_title);
        add_comment = findViewById(R.id.id_u_addComment);
        eSwipeRefreshLayout = findViewById(R.id.id_swiperefresh_u_inpost);
        eRecyclerView = findViewById(R.id.id_recyclerview_u_inpost);
        CommentsList = new ArrayList<>();
        eSwipeRefreshLayout.setMode(SwipeRefresh.Mode.BOTH);
        eSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.YELLOW, Color.GREEN);
        eRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    private void initEvent() {
        Back_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if(from_where_to_pdetail.equals("user_home")) {
                    intent = new Intent(UserPostDetailsActivity.this, UserHomeActivity.class);
                }
                else if(from_where_to_pdetail.equals("look_post")){
                    intent = new Intent(UserPostDetailsActivity.this, LookMypostsActivity.class);
                }
                else if(from_where_to_pdetail.equals("map")){
                    intent = new Intent(UserPostDetailsActivity.this, MapActivity.class);
                }
                else{
                    intent = new Intent(UserPostDetailsActivity.this, LookMycommentsActivity.class);
                }
                startActivity(intent);
            }
        });
        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPostDetailsActivity.this, CommentActivity.class);
                intent.putExtra("post_id", post_id);  // 替换为实际的键值对
                startActivity(intent);
            }
        });
        eSwipeRefreshLayout.setOnRefreshListener(new SwipeRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get_post_comment(post_id);
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });

        eSwipeRefreshLayout.setOnPullUpRefreshListener(new SwipeRefreshLayout.OnPullUpRefreshListener() {

            @Override
            public void onPullUpRefresh() {
                eSwipeRefreshLayout.setRefreshing(false);
                eSwipeRefreshLayout.setPullUpRefreshing(false);
            }
        });

    }
}