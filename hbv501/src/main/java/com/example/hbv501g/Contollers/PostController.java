package com.example.hbv501g.Contollers;

import com.example.hbv501g.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.hbv501g.Persistence.Entities.Forum;
import com.example.hbv501g.Persistence.Entities.Post;
import com.example.hbv501g.Persistence.Entities.User;
import com.example.hbv501g.Services.PostService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostController {

   PostService postService;

    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    //This method gets the form to create a new post.
    @RequestMapping(value = "/addpost", method = RequestMethod.GET)
    public String addPostForm(Post post, HttpSession session){
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if(loggedInUser == null){
            throw new UnauthorizedException("You have to be logged in to create a post");
        }
        return "newPost";
    }

    //This method gets the data from the form and creates and saves it to a database if it is a loggedin user.
    @RequestMapping(value = "/addpost", method = RequestMethod.POST)
    public String addPost( Post post, BindingResult result, Model model, HttpSession session){
        if(result.hasErrors()){
            return "newPost";
        }

        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        System.out.println(loggedInUser.getUserId());
        Forum forumData = (Forum) session.getAttribute("ForumData");
        long forumId = forumData.getForum_id();
        post.setUser(loggedInUser);
        post.setForum(forumData);
        postService.save(post);

        return "redirect:/forum/" + forumId; 
    }

    //This method deletes a post.
    @RequestMapping(value = "/posts/delete/{postId}", method = RequestMethod.GET)
    public String deletePost(@PathVariable("postId") long id, HttpSession session, Model model){
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        Post postToDelete = postService.findById(id);
        long forumId = postToDelete.getForum().getForum_id();
        if(postToDelete.getUser().getUserId() == loggedInUser.getUserId()) {
            postService.delete(postToDelete);
            System.out.println("The post was deleted");
            return "redirect:/forum/" + forumId;
        }else {
            throw new UnauthorizedException("You can't delete this post");
        }
    }

    // this methhod gets the form to edit a post
    @RequestMapping(value = "/posts/edit/{postId}", method = RequestMethod.GET)
    public String editPostForm(@PathVariable("postId") long id, HttpSession session, Model model){
        Post postToEdit = postService.findById(id);
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (loggedInUser == null || postToEdit.getUser().getUserId() != loggedInUser.getUserId()) {
            throw new UnauthorizedException("You can't edit this post");
        } else {
            model.addAttribute("post", postToEdit);
            return "editPost";
        }
    }

    // This methhod allows you to edit a post 
    @RequestMapping(value = "/posts/edit/{postId}", method = RequestMethod.POST)
    public String editPost(@PathVariable("postId") long id, @RequestParam(required = false) String tag,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) String content, HttpSession session) {
        Post postToEdit = postService.findById(id);
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (postToEdit.getUser().getUserId() == loggedInUser.getUserId()) {
            postService.edit(postToEdit, tag, title, content);
            System.out.println("post edited");
        } else {
            System.out.println("You can't edit this post");
        }
        long forumId = postToEdit.getForum().getForum_id();
        return "redirect:/forum/" + forumId;
    }

    // This methhod allows you to like a post 
    @PostMapping("/post/{postId}/like")
    public String likePost(@PathVariable Long postId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        postService.likepost(postId, loggedInUser);
        Post post= postService.findById(postId);
        Forum forum= post.getForum();
        long forumId = forum.getForum_id(); 
        return "redirect:/forum/" + forumId; 
    }

    // This methhod allows you to dislike a post 
    @PostMapping("/post/{postId}/dislike")
    public String disLikePost(@PathVariable Long postId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        postService.disLikePost(postId, loggedInUser); 
        Post post= postService.findById(postId);
        Forum forum= post.getForum();
        long forumId = forum.getForum_id();
        return "redirect:/forum/" + forumId;
    }
    
}
