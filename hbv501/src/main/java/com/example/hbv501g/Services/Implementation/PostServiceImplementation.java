package com.example.hbv501g.Services.Implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hbv501g.Persistence.Entities.Forum;
import com.example.hbv501g.Persistence.Entities.Post;
import com.example.hbv501g.Persistence.Entities.User;
import com.example.hbv501g.Persistence.Repositories.PostRepository;
import com.example.hbv501g.Services.PostService;

@Service
public class PostServiceImplementation implements PostService {

    private PostRepository postRepository;
    
    @Autowired
    public PostServiceImplementation(PostRepository postRepository) {
       this.postRepository = postRepository;
    }


    @Override
    public Post findByTitle(String title) {
       return postRepository.findByTitle(title).get(0);
    }

    @Override
    public Post findById(long ID) {
        return postRepository.findById(ID);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public void delete(Post post) {
        post.getUserLikes().clear();
        post.getUserDislikes().clear();
        postRepository.save(post);
        postRepository.delete(post);
    }

    // allows to edit a post
    @Override
    public Post edit(Post post, String tag, String title, String content) {
        if (!tag.isEmpty()) {
            post.setTag(tag);
        }
        if (title.isEmpty()) {
            post.setTitle(title);
        }
        if (!content.isEmpty()) {
            post.setContent(content);
        }
        return postRepository.save(post);
    }

    // finds all the posts conected to a specific forum
    @Override
    public List<Post> getPostByForum(Forum forum) {
        return postRepository.findPostByForum(forum);
    }

    // deletes all the posts made by a specific user
    @Override
    public List <Post> deletePostsByUser(User user) {
        return postRepository.deleteAllByUser(user);
    }

    // deletes all the posts that are connected to a forum
    public void deletePostsOfForum(Forum forum) {
        postRepository.deleteAllByForum(forum);
    }

    // implementing a like system
    @Override
    public void likepost(long postID, User user){
        Post post = postRepository.findById(postID);
        boolean userAlreadyLiked = post.getUserLikes().stream()
            .anyMatch(likedUser -> likedUser.getUserId() == user.getUserId());
        if (!userAlreadyLiked) {
           post.getUserLikes().add(user);
           postRepository.save(post); 
        } else {
            post.getUserLikes().removeIf(likedUser -> likedUser.getUserId() == user.getUserId());
            postRepository.save(post); 
        }

        boolean userAlreadyDisliked = post.getUserDislikes().stream()
            .anyMatch(likedUser -> likedUser.getUserId() == user.getUserId());

         if(userAlreadyDisliked){
             post.getUserDislikes().removeIf(dislikedUser -> dislikedUser.getUserId() == user.getUserId());
             postRepository.save(post); 
        }
    }

    // Implementing a dislike system
    @Override
    public void disLikePost(long postID, User user){
        Post post = postRepository.findById(postID);
        boolean userAlreadyDisliked = post.getUserDislikes().stream()
            .anyMatch(likedUser -> likedUser.getUserId() == user.getUserId());
        if (!userAlreadyDisliked) {
           post.getUserDislikes().add(user);
           postRepository.save(post); 
        } else {
            post.getUserDislikes().removeIf(dislikedUser -> dislikedUser.getUserId() == user.getUserId());
            postRepository.save(post); 
        }

        boolean userAlreadyLiked = post.getUserLikes().stream()
            .anyMatch(likedUser -> likedUser.getUserId() == user.getUserId());
        
        if(userAlreadyLiked){
            post.getUserLikes().removeIf(likedUser -> likedUser.getUserId() == user.getUserId());
            postRepository.save(post); 
        }
    }

    // deletess all the likes and dislikes of a User
    @Override
    public void removeLikesAndDislikesByUser(User user) {
        List<Post> allPosts = postRepository.findAll(); 
        for (Post post : allPosts) {
            boolean userAlreadyDisliked = post.getUserDislikes().stream()
               .anyMatch(likedUser -> likedUser.getUserId() == user.getUserId());
          if (userAlreadyDisliked) {
              post.getUserDislikes().removeIf(dislikedUser -> dislikedUser.getUserId() == user.getUserId());
              postRepository.save(post); 
            } 

            boolean userAlreadyLiked = post.getUserLikes().stream()
                .anyMatch(likedUser -> likedUser.getUserId() == user.getUserId());
            if(userAlreadyLiked){
                post.getUserLikes().removeIf(likedUser -> likedUser.getUserId() == user.getUserId());
                postRepository.save(post); 
            }
        }
        
    }
    
}
