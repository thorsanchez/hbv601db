package com.example.hbv501g.Persistence.Repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hbv501g.Persistence.Entities.Ad;
import com.example.hbv501g.Persistence.Entities.User;

public interface AdRepository extends JpaRepository<Ad, Long> {
    Ad save(Ad ad);
    List<Ad> findAll();
    List<Ad> findByCategory(String category);
    void deleteAdsByCreatedBy(User user);
    Ad findAdByAdId(long id);
    void delete(Ad ad);
    List<Ad> findAdByCreatedBy(User user);
}
