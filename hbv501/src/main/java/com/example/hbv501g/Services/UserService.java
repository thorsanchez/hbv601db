package com.example.hbv501g.Services;

import com.example.hbv501g.Persistence.Entities.User;
import com.example.hbv501g.Persistence.Entities.Ad;
import com.example.hbv501g.Persistence.Entities.Forum;

import java.util.List;

public interface UserService {

    User save(User user);

    void delete(User user);

    List<User> findAll();

    User findByUsername(String username);

    User findById(long ID);

    User updateProfile(User user);

    User findByEmail(String email);

    User login(User user);

    List<Ad> findAdByCreatedBy(User loggedInUser);

    List<Forum> findForumByUserId(User loggedInUser);

    String showBehaviour(User user);

    void updatebehaviour(String category,User user);

    void setLoggedIn(User user, boolean status);

    void editPassword(User user, String username, String email, String password);


}
