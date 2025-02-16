package com.example.hbv501g.Contollers;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hbv501g.Persistence.Entities.Ad;
import com.example.hbv501g.Persistence.Entities.Forum;
import com.example.hbv501g.Persistence.Entities.Post;
import com.example.hbv501g.Persistence.Entities.User;
import com.example.hbv501g.Persistence.Entities.PaginateResult;
import com.example.hbv501g.Services.ForumService;
import com.example.hbv501g.Services.UserService;
import com.example.hbv501g.exceptions.UnauthorizedException;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    ForumService forumService;
    UserService userService;

    @Autowired
    public HomeController(ForumService forumService,UserService userService) {
        this.forumService = forumService;
        this.userService= userService;

    }

    //This method gets all forums and displays them on the home page
    @RequestMapping("/")
    public String homePage(Model model, HttpSession session, @RequestParam(required = false) Integer page) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        model.addAttribute("LoggedInUser", loggedInUser);
        //create a list of all forums
        List<Forum> allForums = forumService.findAll();
        PaginateResult<Forum> result = new PaginateResult<>(page, allForums, 5);

        // Choose an ad to display
        String category;
        if(loggedInUser!=null){
            category= userService.showBehaviour(loggedInUser);
        }else{
            category="";
        }
        Ad ad = forumService.findAd(category);
        
        

        model.addAttribute("forum", result.results);
        model.addAttribute("newPosts", new Post());
        model.addAttribute("pageNumber", result.pageNumber);
        model.addAttribute("maxPage", result.maxPage);
        model.addAttribute("ad", ad);
        return "home";
    }

    //This method gets the form to create a new forum
    @RequestMapping(value = "/addforum", method = RequestMethod.GET)
    public String addForumForm(Forum forum, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser == null){
            throw new UnauthorizedException("You have to be logged in to create forum");
        }
        return "newForum";
    }

    //This method takes the data from the form and saves the forum into the database
    @RequestMapping(value = "/addforum", method = RequestMethod.POST)
    public String addForum(Forum forum, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "newForum";
        }

        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (loggedInUser != null) {
            forum.setCreatedBy(loggedInUser);
            System.out.println(loggedInUser.getUserId());
            forumService.save(forum);
            System.out.println("Forum creater: " + forum.getCreatedBy().getUsername());
            return "redirect:/";
        } else {
            return "redirect:/";
        }
    }

    //This method deletes a forum if it is the same user that created it.
    //It also deletes all the posts that were under that forum from the database.
    @RequestMapping(value = "/forum/delete/{forumId}", method = RequestMethod.GET)
    public String deleteForum(@PathVariable("forumId") long id, HttpSession session) {
        Forum forumToDelete = forumService.findById(id);
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (forumToDelete.getCreatedBy().getUserId() == loggedInUser.getUserId()) {
            forumService.delete(forumToDelete);
            System.out.println("forum deleted");
        } else {
            throw new UnauthorizedException("You can't delete this forum");
        }
        return "redirect:/";
    }

    //This method gets the form to edit the forum and the data from that forum.
    @RequestMapping(value = "/forum/editForum/{forumId}", method = RequestMethod.GET)
    public String editForumForm(@PathVariable("forumId") long id, Forum forum, Model model) {
        Forum forumToEdit = forumService.findById(id);
        model.addAttribute("forum", forumToEdit);
        return "editForum";
    }

    //This method overrides the data for the selected forum.
    @RequestMapping(value = "/forum/editForum/{forumId}", method = RequestMethod.POST)
    public String editForum(@PathVariable("forumId") long id, Model model, @RequestParam(required = false) String name,
                                                              @RequestParam(required = false) String description, 
                                                              @RequestParam(required = false) String category, HttpSession session) {
        Forum forumToEdit = forumService.findById(id);
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (forumToEdit.getCreatedBy().getUserId() == loggedInUser.getUserId()) {
            forumService.edit(forumToEdit, name, description, category);
            System.out.println("forum edited");
        } else {
            System.out.println("You can't edit forum");
        }
        return "redirect:/";
    }

    // This methhod handels opening a forum and what is in it
    @GetMapping("/forum/{forumId}")
    public String intoForum(@PathVariable("forumId") long forumId, Model model, HttpSession session, @RequestParam(required = false) Integer page) {
        Forum forum = forumService.findById(forumId);
        String category= forum.getCategory();
        List<Post> forumPosts = forumService.findPostsByForum(forum);
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        Collections.sort(forumPosts, new PostComparator().reversed());
        PaginateResult<Post> result = new PaginateResult<>(page, forumPosts, 5);

        if(loggedInUser !=null){
            userService.updatebehaviour(category, loggedInUser);
        }

        session.setAttribute("ForumData", forum);
        model.addAttribute("forum", forum);
        model.addAttribute("posts", result.results);
        model.addAttribute("newPosts", new Post());
        model.addAttribute("pageNumber", result.pageNumber);
        model.addAttribute("maxPage", result.maxPage);
        model.addAttribute("LoggedInUser", loggedInUser);

        return "forum";
    }

    //This method lets you search for forums by category or name.
    @GetMapping("/search")
    public String searchForums(Model model, @RequestParam(required = false) String category, @RequestParam(required = false) String name, @RequestParam(required = false) Integer page) {
        List<Forum> forumlist = forumService.findAll();
        if (category.isEmpty() && name.isEmpty()) {
            return "searchResults";
        }
        if (!category.isEmpty()) {
            model.addAttribute("category", category);
            forumlist.removeIf(f -> !f.getCategory().equalsIgnoreCase(category));
        }
        if (!name.isEmpty()) {
            model.addAttribute("name", name);
            forumlist.removeIf(f -> !f.nameMatches(name, 2.0/3.0));
        }

        PaginateResult<Forum> result = new PaginateResult<>(page, forumlist, 5);

        model.addAttribute("forums", result.results);
        model.addAttribute("pageNumber", result.pageNumber);
        model.addAttribute("maxPage", result.maxPage);
        return "searchResults";
    }

    //This class sorts the posts inside a forum by the most likes and dislikes.
    static class PostComparator implements java.util.Comparator<Post> {
        @Override
        public int compare(Post a, Post b) {
            return (a.getUserLikes().size() + b.getUserDislikes().size()) - (b.getUserLikes().size() + a.getUserDislikes().size());
        }
    }
}

