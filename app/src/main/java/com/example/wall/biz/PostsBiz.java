package com.example.wall.biz;

import com.example.wall.bean.BaseTask;
import com.example.wall.config.Config;
import com.example.wall.net.CommonCallback;
import com.example.wall.ui.vo.Posts;
import com.example.wall.ui.vo.RepeatForPost;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

public class PostsBiz {
    public void getAll(CommonCallback<List<Posts>> commonCallback){
        OkHttpUtils
                .post()
                .url(Config.baseUrl + "post")
                .tag(this)
                .build()
                .execute(commonCallback);
    }
    public void save(Posts question, CommonCallback<String> commonCallback){
        OkHttpUtils
                .postString()
                .url(Config.baseUrl + "saveQuestion")
                .tag(this)
                .content(new Gson().toJson(question))
                .build()
                .execute(commonCallback);
    }
    public void searchByState(String q,CommonCallback<List<Posts>> commonCallback){
        OkHttpUtils
                .post()
                .url(Config.baseUrl + "post")
                .addParams("state",q)
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    public void insertRepeat(BaseTask baseTask, CommonCallback<String> commonCallback){
        OkHttpUtils
                .postString()
                .url(Config.baseUrl + "insertRepeat")
                .tag(this)
                .content(new Gson().toJson(baseTask))
                .build()
                .execute(commonCallback);
    }

    public void getRepeatByRid(Long rid,CommonCallback<List<RepeatForPost>> commonCallback){
        OkHttpUtils
                .post()
                .url(Config.baseUrl + "getQuestionRepeatByRid")
                .addParams("rid",rid+"")
                .tag(this)
                .build()
                .execute(commonCallback);
    }
    public void updateRepeat(RepeatForPost repeat, CommonCallback<List<RepeatForPost>> commonCallback){
        OkHttpUtils
                .postString()
                .tag(this)
                .url(Config.baseUrl + "saveRepeat")
                .content(new Gson().toJson(repeat))
                .build()
                .execute(commonCallback);
    }
    public void onDestory(){
        OkHttpUtils.getInstance().cancelTag(this);
    }

    public void getAllMyQuestion(Long id, CommonCallback<List<Posts>> commonCallback) {
        OkHttpUtils
                .post()
                .url(Config.baseUrl + "getAllMyQuestion")
                .addParams("id",id+"")
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    public void getAllMyQuestionAsked(Long id, CommonCallback<List<Posts>> commonCallback) {
        OkHttpUtils
                .post()
                .url(Config.baseUrl + "getAllMyQuestionAsked")
                .addParams("id",id+"")
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    public void getAllMyQuestionRepeated(Long id, CommonCallback<List<Posts>> commonCallback) {
        OkHttpUtils
                .post()
                .url(Config.baseUrl + "getAllMyQuestionRepeated")
                .addParams("id",id+"")
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    public void deleteQuestion(Long id, CommonCallback<String> listCommonCallback) {
        OkHttpUtils
                .post()
                .url(Config.baseUrl + "deleteQuestion")
                .addParams("id",id+"")
                .tag(this)
                .build()
                .execute(listCommonCallback);
    }

    public void getAllOfAllPosts(CommonCallback<List<Posts>> commonCallback){
        OkHttpUtils
                .post()
                .url(Config.baseUrl + "getAllOfAllPosts")
                .tag(this)
                .build()
                .execute(commonCallback);
    }

}
