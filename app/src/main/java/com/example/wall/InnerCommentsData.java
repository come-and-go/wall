package com.example.wall;

import com.example.wall.ui.vo.CommentForPost;
import com.example.wall.ui.vo.Posts;

import java.util.List;

public class InnerCommentsData {
    private List<CommentForPost> comments;
    public List<CommentForPost> add(InnerCommentsData ipd){
        if(comments == null || comments.isEmpty()){
            comments = ipd.getComments();
        }
        else {
            comments.addAll(ipd.getComments());
        }
        return comments;
    }
    public void delete_ten(){
        if(comments != null ){
            for(int i = 0; i<10;i++){
                comments.remove(comments.size()-1);
            }
        }
    }

    public List<CommentForPost> getComments() {
        return comments;
    }
    public void empty(){
        if(comments != null) {
            comments.clear();
        }
    }
}
