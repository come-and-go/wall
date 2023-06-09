package com.example.wall.ui.vo;

import com.example.wall.bean.Comment;
import com.example.wall.bean.User;

import java.io.Serializable;

/**
 * 评论评价包装类
 */

public class Appraise implements Serializable {
  User postUser;
  User doneUser;
  Comment comment;

  @Override
  public String toString() {
    return "Appraise{" +
            "postUser=" + postUser +
            ", doneUser=" + doneUser +
            ", comment=" + comment +
            '}';
  }

  public User getPostUser() {
    return postUser;
  }

  public void setPostUser(User postUser) {
    this.postUser = postUser;
  }

  public User getDoneUser() {
    return doneUser;
  }

  public void setDoneUser(User doneUser) {
    this.doneUser = doneUser;
  }

  public Comment getComment() {
    return comment;
  }

  public void setComment(Comment comment) {
    this.comment = comment;
  }

}
