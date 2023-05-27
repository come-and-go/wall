package com.example.wall;

import com.example.wall.ui.vo.Posts;

import java.util.List;

public class InnerPostsData {
    private List<Posts> posts;

    public List<Posts> getPosts() {
        return posts;
    }
    public List<Posts> add(InnerPostsData ipd){
        if(posts == null || posts.isEmpty()){
            posts = ipd.getPosts();
        }
        else {
            posts.addAll(ipd.getPosts());
        }
        return posts;
    }
    public void delete_ten(){
        if(posts != null ){
            for(int i = 0; i<10;i++){
                posts.remove(posts.size()-1);
            }
        }
    }
    public void empty(){
        if(posts != null) {
            posts.clear();
        }
    }
}
