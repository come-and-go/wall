package com.example.wall.ui.vo;

import java.io.Serializable;
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

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title=" + title +
                ", context=" + text +
                ", user=" + ownername +
                '}';
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public int getContent_type(){return content_type;}

    public String getContext(){
        return text;
    }
    public String getMedia_url(){return media_url;}

    public String getOwner() {
        return ownername;
    }

    public Double getLocation_x(){return location_x;}
    public Double getLocation_y(){return location_y;}


    public String getTime() { return post_time;
    }
}
