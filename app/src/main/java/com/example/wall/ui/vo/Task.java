package com.example.wall.ui.vo;

import com.example.wall.bean.BaseTask;
import com.example.wall.bean.Comment;
import com.example.wall.bean.User;

import java.io.Serializable;

public class Task implements Serializable {
    BaseTask context;
    User doneUser;
    User postedUser;
    Comment comment;
    @Override
    public String toString() {
        return "Task{" +
                "context=" + context +
                ", doneUser=" + doneUser +
                ", postedUser=" + postedUser +
                '}';
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public BaseTask getContext() {
        return context;
    }

    public void setContext(BaseTask context) {
        this.context = context;
    }

    public User getDoneUser() {
        return doneUser;
    }

    public void setDoneUser(User doneUser) {
        this.doneUser = doneUser;
    }

    public User getPostedUser() {
        return postedUser;
    }

    public void setPostedUser(User postedUser) {
        this.postedUser = postedUser;
    }
}
