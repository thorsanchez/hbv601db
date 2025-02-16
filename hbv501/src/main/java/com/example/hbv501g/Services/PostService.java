package com.example.hbv501g.Services;

import com.example.hbv501g.Persistence.Entities.Forum;
import com.example.hbv501g.Persistence.Entities.Post;
import com.example.hbv501g.Persistence.Entities.User;

import java.util.List;

public interface PostService {
    Post findByTitle(String title);
    Post findById(long ID);
    List<Post> findAll();
    Post save(Post post);
    void delete(Post post);
    Post edit(Post post, String tag, String title, String content);
    List<Post> getPostByForum(Forum forum);
    List <Post> deletePostsByUser(User user);
    void likepost(long postID, User user);
    void disLikePost(long postID, User user);
    void removeLikesAndDislikesByUser(User user);

    
}
