package com.example.hbv501g.Services.Implementation;

import com.example.hbv501g.Persistence.Entities.Ad;
import com.example.hbv501g.Persistence.Entities.Forum;
import com.example.hbv501g.Persistence.Entities.Post;
import com.example.hbv501g.Persistence.Entities.User;
import com.example.hbv501g.Persistence.Repositories.ForumRepository;
import com.example.hbv501g.Services.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumServiceImplementation implements ForumService {

    ForumRepository forumRepository;
    AdServiceImplementation adServiceImplementation;
    PostServiceImplementation postServiceImplementation;

    @Autowired
    public ForumServiceImplementation(ForumRepository forumRepository, 
     AdServiceImplementation adServiceImplementation,PostServiceImplementation postServiceImplementation){
        this.forumRepository = forumRepository;
        this.postServiceImplementation = postServiceImplementation;
        this.adServiceImplementation = adServiceImplementation;
    }
    @Override
    public Forum save(Forum forum) {
        return forumRepository.save(forum);
    }

    @Override
    public void delete(Forum forum) {
        postServiceImplementation.deletePostsOfForum(forum);
        forumRepository.delete(forum);
    }

    @Override
    public List<Forum> findAll() {
        return forumRepository.findAll();
    }

    @Override
    public Forum findById(long ID) {
        return forumRepository.findById(ID);
    }

    // Deletes all the forums that were created by a specific user
    @Override
    public void deleteForumsByUser(User user) {
        forumRepository.deleteForumsByCreatedBy(user);
    }

    // finds all the forums created by a specific User
    @Override
    public List<Forum> findByUserId(User user) {
        return forumRepository.findForumByCreatedBy(user);
    }

    // Finds all the posts of a forum
    @Override
    public List<Post> findPostsByForum(Forum forum){
        return postServiceImplementation.getPostByForum(forum);
    }


    // Allows to edit a forum
    @Override
    public Forum edit(Forum forum, String name, String description, String category) {

        if (!name.isEmpty()) {
            forum.setName(name);
        }
        if (!description.isEmpty()) {
            forum.setDescription(description);
        }
        if (!category.isEmpty()) {
            forum.setCategory(category);
        }
        return forumRepository.save(forum);
    }


    // finnds ads to display on the home screen
    @Override
    public Ad findAd(String category){
        Ad ad;
        if (category.equals("No categories viewed.")) {
            return adServiceImplementation.findHighest();
        } else {
            ad = adServiceImplementation.findByCategory(category);
            if (ad == null) { 
                return adServiceImplementation.findHighest(); 
            }else{
                return ad;
            }
        }
    };
    
}