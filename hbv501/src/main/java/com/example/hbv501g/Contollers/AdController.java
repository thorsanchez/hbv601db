package com.example.hbv501g.Contollers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.hbv501g.Persistence.Entities.Ad;
import com.example.hbv501g.Persistence.Entities.User;
import com.example.hbv501g.Services.AdService;
import com.example.hbv501g.exceptions.UnauthorizedException;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;


@Controller 
public class AdController {
    
    AdService adService;
    @Autowired
    public AdController(AdService adService){
        this.adService = adService;
    }


    // This methhod gets the form to create a ad
    @RequestMapping(value = "/addAd", method = RequestMethod.GET)
    public String addAdForm(Ad ad, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser == null) {
            throw new UnauthorizedException("You have to be logged in to add an ad");
        }
        return "newAd"; 
    }

    // This methood allows you to creat a ad
    @RequestMapping(value = "/addAd", method = RequestMethod.POST)
    public String addAd(Ad ad, BindingResult result, Model model, HttpSession session) {
       if (result.hasErrors()) {
           return "newAd"; 
       }

       User loggedInUser = (User) session.getAttribute("LoggedInUser");

       if (loggedInUser != null) {
          ad.setCreatedBy(loggedInUser); 
           adService.save(ad); 
          return "redirect:/"; 
      } else {
          return "redirect:/";
      }
    }

    // This methhod gets the form to edit a ad
    @RequestMapping(value = "/ad/editAd/{adId}", method = RequestMethod.GET)
    public String editAdForm(@PathVariable("adId") long id, Ad ad, Model model) {
        Ad adToEdit = adService.findAdByAdId(id);
        if (adToEdit == null) {
            throw new IllegalArgumentException("Ad not found with id: " + id);
        }
        model.addAttribute("ad", adToEdit);
        return "editAd";
    }

    // This methhod allows you to edit an ad
    @RequestMapping(value = "/ad/editAd/{adId}", method = RequestMethod.POST)
    public String editAd(@PathVariable("adId") long id, Model model, @RequestParam(required = false) long amount,
                         @RequestParam(required = false) String category,
                         @RequestParam(required = false) String contents, HttpSession session) {
        Ad adToEdit = adService.findAdByAdId(id);
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (adToEdit.getCreatedBy().getUserId() == loggedInUser.getUserId()) {
            adService.edit(adToEdit, amount, category, contents);
            System.out.println("ad edited");
        } else {
            System.out.println("You can't edit ad");
        }
        return "redirect:/";
    }

    // This methhod allows you to delete an ad 
    @RequestMapping(value = "/ad/delete/{adId}", method = RequestMethod.GET)
    public String deleteAd(@PathVariable("adId") long id, HttpSession session) {
        Ad adToDelete = adService.findAdByAdId(id);
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (adToDelete.getCreatedBy().getUserId() == loggedInUser.getUserId()) {
            adService.delete(adToDelete);
            System.out.println("ad deleted");
        } else {
            throw new UnauthorizedException("You can't delete this Ad");
        }
        return "redirect:/";
    }



}
