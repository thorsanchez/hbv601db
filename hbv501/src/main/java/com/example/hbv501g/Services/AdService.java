package com.example.hbv501g.Services;

import java.util.List;

import com.example.hbv501g.Persistence.Entities.Ad;
import com.example.hbv501g.Persistence.Entities.User;

public interface AdService {
    Ad save(Ad ad);
    Ad findHighest();
    Ad findByCategory(String category);
    void deleteAdsByCreatedBy(User user);
    Ad findAdByAdId(long id);
    Ad edit(Ad ad, long amount, String category, String contents);
    void delete(Ad ad);
    List<Ad> findAdByCreatedBy(User user);
}