package com.example.wall.ui.vo;

import com.example.wall.bean.BaseTask;
import com.example.wall.bean.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Posts implements Serializable {

    String id;
    String title;

    int content_type;
    String text;
    String media_url;
    Double location_x;
    Double location_y;
    String ownername;
    String post_time;
    List<RepeatForPost> repeat;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title=" + title +
                ", context=" + text +
                ", user=" + ownername +
                ", repeat=" + repeat +
                '}';
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getContext(){
        return text;
    }

    public String getOwner() {
        return ownername;
    }

    public Double getLocation_x(){return location_x;}
    public Double getLocation_y(){return location_y;}

    public List<RepeatForPost> getRepeat() {
        return repeat;
    }

    public void setRepeat(List<RepeatForPost> repeat) {
        this.repeat = repeat;
    }
}
