package com.example.hbv501g.Contollers;

import com.example.hbv501g.Persistence.Entities.Ad;
import com.example.hbv501g.Persistence.Entities.Forum;
import com.example.hbv501g.Persistence.Entities.User;
import com.example.hbv501g.Persistence.Entities.PaginateResult;
import com.example.hbv501g.Services.ForumService;
import com.example.hbv501g.Services.PostService;
import com.example.hbv501g.Services.AdService;
import com.example.hbv501g.Services.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    UserService userService;
    


    @Autowired
    public UserController(UserService userService, PostService postService, ForumService forumService, AdService adService) {
        this.userService = userService;
    }

    //Gets the form to create new user
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupGET(User user) {
        return "signup";
    }

    //Gets the data from the form and saves it to the database
    //Checks if the username or email is in the database before
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupPOST(User user, BindingResult result, Model model, String username, String email) {
        if (result.hasErrors()) {
            return "redirect:/signup";
        }
        if (userService.findByUsername(username) == null && userService.findByEmail(email) == null) {
            userService.save(user);
        } else {
            System.out.println("This username or email is taken");
        }
        return "redirect:/";
    }

    //Gets the form to sign in
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGET(User user) {
        return "login";
    }

    //Gets the data from the form and checks if the user is in the database
    //Puts the user to the session
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPOST(User user, BindingResult result, Model model, HttpSession session, String username, String email) {
        if (result.hasErrors()) {
            return "login";
        }
        User exists = userService.login(user);
        if (exists != null) {
            session.setAttribute("LoggedInUser", exists);
            model.addAttribute("LoggedInUser", exists);
            return "redirect:/";
        }
        return "redirect:/login";
    }

    //Gives you what user is loggedin
    @RequestMapping(value = "/loggedin", method = RequestMethod.GET)
    public String loggedinGET(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
            //th
            return "redirect:/loggedin"; // Redirect to a logged-in user page
        } else {
            model.addAttribute("loginError", "Invalid username or password");
            return "login"; // Return to login page with error
        }
    }

    //Logs the user from the session out.
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutGET(HttpSession session) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            // Update user logged in status
            userService.setLoggedIn(sessionUser, false);
            // loka session
            session.invalidate();
            System.out.println("User logged out: " + sessionUser.getUsername());
        }
        return "redirect:/";
    }

    //Gets the form to change the password
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public String ForgotPasswordForm(User user) {
        return "forgotPassword";
    }

    // This method handels the forgot password request
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public String forgotPassword(String username, String email, String password, User user, Model model) {
        if (userService.findByUsername(username) != null && userService.findByEmail(email) != null) {
            if (userService.findByUsername(username).getUserId() == userService.findByEmail(email).getUserId()) {
                User userByName = userService.findByUsername(username);
                userService.editPassword(userByName, username, email, password);
            }
            return "redirect:/";
        }
        System.out.println("Not right username or email");
        return "forgotPassword";
    }

    // Gets the form to verify deleting a user
    @RequestMapping(value = "/deleteUserRequest", method = RequestMethod.GET)
    public String verifyingGET(Model model) {
        model.addAttribute("user", new User());
        return "verifyPassword";
    }

    // This method checks if password is right to be able to delete a user
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public String verifyingPOST(@ModelAttribute("user") User user, HttpSession session) {
        String inputPassword = user.getPassword();
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser != null && inputPassword.equals(loggedInUser.getPassword())) {
            session.setAttribute("isVerified", true);
            return "redirect:/deleteUser";
        }
        session.setAttribute("isVerified", false);
        return "redirect:/";
    }

    // This methood deletes a User
    @RequestMapping(value = "/deleteUser", method = RequestMethod.GET)
    @Transactional
    public String deleteUser(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        Boolean isVerified = (Boolean) session.getAttribute("isVerified");
        if (loggedInUser != null && Boolean.TRUE.equals(isVerified)) {
            session.invalidate();
            userService.delete(loggedInUser);
            System.out.println("User deleted: " + loggedInUser);
            return "redirect:/";
        }
        return "redirect:/deleteUserRequest";
    }

    // This methood opens up the users profile with his forums and ads
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String viewProfile(HttpSession session, Model model, @RequestParam(required = false) Integer page) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("user", loggedInUser);
            List<Forum> myForums = userService.findForumByUserId(loggedInUser);
            model.addAttribute("myForum", myForums);
            List<Ad> myAds = userService.findAdByCreatedBy(loggedInUser);
            model.addAttribute("myAds", myAds);
            PaginateResult<Forum> result = new PaginateResult<>(page, myForums, 5);

            model.addAttribute("myForum", result.results);
            model.addAttribute("pageNumber", result.pageNumber);
            model.addAttribute("maxPage", result.maxPage);
            return "profile";
        }
        return "redirect:/";
    }

    // This methhod allows user to edit his profile
    @RequestMapping(value = "/profile/edit", method = RequestMethod.POST)
    public String editProfile(@ModelAttribute("user") User updatedUser, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        System.out.println("breyta profile user id " + updatedUser.getUserId());
        System.out.println("nytt mail " + updatedUser.getEmail());
        System.out.println("nytt lykilord " + updatedUser.getPassword());

        if (loggedInUser != null && loggedInUser.getUserId() == updatedUser.getUserId()) {
            User updatedProfile = userService.updateProfile(updatedUser);
            session.setAttribute("LoggedInUser", updatedProfile);
            model.addAttribute("user", updatedProfile);
            model.addAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/profile";
        }
        return "redirect:/";
    }
}
