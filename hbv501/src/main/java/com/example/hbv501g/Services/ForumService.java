package com.example.hbv501g.Services;

import com.example.hbv501g.Persistence.Entities.Forum;
import com.example.hbv501g.Persistence.Entities.Post;
import com.example.hbv501g.Persistence.Entities.User;
import com.example.hbv501g.Persistence.Entities.Ad;



import java.util.List;

public interface ForumService {
    Forum save(Forum forum);
    Forum edit(Forum forum, String name, String description, String category);
    void delete(Forum forum);
    List<Forum> findAll();
    Forum findById(long ID);
    void deleteForumsByUser(User user);
    List<Forum> findByUserId(User user);
    Ad findAd(String category);
    List<Post> findPostsByForum(Forum forum);
}