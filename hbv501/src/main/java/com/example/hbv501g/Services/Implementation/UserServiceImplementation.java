package com.example.hbv501g.Services.Implementation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.hbv501g.Persistence.Entities.Ad;
import com.example.hbv501g.Persistence.Entities.User;
import com.example.hbv501g.Persistence.Entities.Forum;
import com.example.hbv501g.Persistence.Repositories.UserRepository;
import com.example.hbv501g.Services.UserService;


@Service
public class UserServiceImplementation implements UserService {

    UserRepository userRepository;


    ForumServiceImplementation forumServiceImplementation;
    PostServiceImplementation postServiceImplementation;
    AdServiceImplementation adServiceImplementation;


    @Autowired
    public UserServiceImplementation(UserRepository userRepository, ForumServiceImplementation forumServiceImplementation,
    PostServiceImplementation postServiceImplementation, AdServiceImplementation adServiceImplementation) {
        this.userRepository = userRepository;
        this.forumServiceImplementation = forumServiceImplementation;
        this.postServiceImplementation = postServiceImplementation;
        this.adServiceImplementation = adServiceImplementation;

    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    // Handles deleteing all properties that are connected to a User and then the User
    @Override
    public void delete(User user) {
        forumServiceImplementation.deleteForumsByUser(user);
        postServiceImplementation.removeLikesAndDislikesByUser(user);
        postServiceImplementation.deletePostsByUser(user);
        adServiceImplementation.deleteAdsByCreatedBy(user);
        userRepository.save(user);
        userRepository.delete(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(long ID) {
        return userRepository.findById(ID);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Checks if a user trying to log in exists
    @Override
    public User login(User user) {
        User doesExist = findByUsername(user.getUsername());
        System.out.println("User fetched: " + doesExist);
        if (doesExist != null) {
            if (doesExist.getPassword().equals(user.getPassword())) {
                //Set user as logged in
                doesExist.setLoggedIn(true);
                userRepository.save(doesExist);
                return doesExist;
            }
        }
        return null;
    }

    //  Handels Useres loginstatus
    @Override
    public void setLoggedIn(User user, boolean status) {
        user.setLoggedIn(status);
        userRepository.save(user);
    }

    // Handles users trying to change passwords
    @Override
    public void editPassword(User user, String username, String email, String password) {
        if (Objects.equals(username, user.getUsername()) && Objects.equals(email, user.getEmail())) {
            if (!password.isEmpty()) {
                user.setPassword(password);
            }
        }
        userRepository.save(user);
    }

    // Handels changing the profile of a user
    @Override
    public User updateProfile(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getUserId());
        if (existingUser != null) {
            // breyta bara ef það er input
            if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
                existingUser.setUsername(updatedUser.getUsername());
            }

            if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
                existingUser.setEmail(updatedUser.getEmail());
            }

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(updatedUser.getPassword());
            }

            userRepository.save(existingUser);
        }
        return existingUser;
    }

    // Finds all the ads created by a user
    @Override
    public List<Ad> findAdByCreatedBy(User loggedInUser){
        return adServiceImplementation.findAdByCreatedBy(loggedInUser);
    };

    // Finds all the forums created by a user
    @Override
    public List<Forum> findForumByUserId(User loggedInUser){
        return forumServiceImplementation.findByUserId(loggedInUser);
    };


    // Adds to a users behaviour status what he has done
    @Override
    public void updatebehaviour(String category,User user){
        user.addLookedAtCategory(category);
        userRepository.save(user);
    };


    // Finds a users most opened forum category to be able to find appropriate ads
    @Override
    public String showBehaviour(User user) {
        List<String> lookedCategories = user.getLookedAt();

        if (lookedCategories == null || lookedCategories.isEmpty()) {
            return "No categories viewed.";
        }

        Map<String, Integer> categoryCount = new HashMap<>();
        for (String category : lookedCategories) {
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }

        String mostCommonCategory = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostCommonCategory = entry.getKey();
            }
        }

        return mostCommonCategory;
    }




}
