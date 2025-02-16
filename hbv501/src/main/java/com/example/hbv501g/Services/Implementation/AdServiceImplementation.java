package com.example.hbv501g.Services.Implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;


import com.example.hbv501g.Persistence.Entities.Ad;
import com.example.hbv501g.Persistence.Entities.User;
import com.example.hbv501g.Persistence.Repositories.AdRepository;
import com.example.hbv501g.Services.AdService;

@Service
public class AdServiceImplementation implements AdService {

    AdRepository adRepository;

    @Autowired
    public AdServiceImplementation(AdRepository adRepository){
        this.adRepository = adRepository;
    }

    @Override
    public Ad save(Ad ad){
        return adRepository.save(ad);
    }

    @Override
    public void delete(Ad ad) {
        adRepository.delete(ad);
    }

    // Deletes all the ads created by a soecific user
    @Override
    public void deleteAdsByCreatedBy(User user){
        adRepository.deleteAdsByCreatedBy(user);
    }

    // Finds an ad by Id
    @Override
    public Ad findAdByAdId(long id) {
        return adRepository.findAdByAdId(id);
    }

    // Allows to edit an ad
    @Override
    public Ad edit(Ad ad, long amount, String category, String contents) {
        ad.setAmount(amount);
    
        if (!category.isEmpty()) {
            ad.setCategory(category);
        }
        if (!contents.isEmpty()) {
            ad.setContents(contents);
        }
        return adRepository.save(ad);
    }
   
    // Finds the ads that are created by a specific user
    @Override
    public List<Ad> findAdByCreatedBy(User user) {
        return adRepository.findAdByCreatedBy(user);
    }

    // Finds the ad that has the most amount of times it can be used left
    @Override
    public Ad findHighest(){
        List<Ad> ads = adRepository.findAll();
        Ad ad=ads.stream().max(Comparator.comparingLong(Ad::getAmount)).orElse(null);
        if(ad!=null){
            ad.setAmount(ad.getAmount()-1);
            adRepository.save(ad);
         }
        return ad;
    };

    // Find ad by the category it is representing
    @Override
    public Ad findByCategory(String category) {
     List<Ad> ads = adRepository.findByCategory(category);
     Ad ad=ads.stream().max(Comparator.comparingLong(Ad::getAmount)).orElse(null);
     if(ad!=null){
        ad.setAmount(ad.getAmount()-1);
        adRepository.save(ad);
     }

     return ad;
    }

}
