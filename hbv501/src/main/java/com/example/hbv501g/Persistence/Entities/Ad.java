package com.example.hbv501g.Persistence.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ad")
public class Ad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long adId; 

    private String contents;
    private String category;
    private long amount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    public Ad() {
    }

    public Ad(String contents, String category, long amount) {
        this.contents = contents;
        this.category = category;
        this.amount = amount;
    }

    public long getAdId() {  // Changed to camel case
        return adId;
    }

    public void setAdId(long adId) {  // Changed to camel case
        this.adId = adId;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
