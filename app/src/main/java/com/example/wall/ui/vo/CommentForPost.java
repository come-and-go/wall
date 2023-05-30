package com.example.wall.ui.vo;


import com.example.wall.bean.BaseTask;
import com.example.wall.bean.User;

import java.io.Serializable;
import java.util.List;

public class CommentForPost implements Serializable {
  String id;
  int content_type;
  String text;
  String media_url;
  String ownername;

  String post_id;
  String post_time;


  @Override
  public String toString() {
    return "RepeatForQuestion{" +
            "user=" + ownername +
            ", context=" + text +
            ", deliver_time=" + post_time +
            '}';
  }

  public String getComment_id(){return id;}

  public String getOwner() {
    return ownername;
  }
  public int getContent_type(){return content_type;}
  public String getMedia_url(){return media_url;}
  public String getContext() {
    return text;
  }
  public String getParent_post_id(){return post_id;}

  public String getDeliver_time() {
    return post_time;
  }

}
