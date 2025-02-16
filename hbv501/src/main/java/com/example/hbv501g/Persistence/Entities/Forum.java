package com.example.hbv501g.Persistence.Entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Arrays;

@Entity
@Table(name = "forums")
public class Forum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long forum_id;
    private String name;
    private String description;
    private String category;

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> forumPosts;

    //tengja forum við User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    public List<Post> getForumPosts() {
        return forumPosts;
    }

    public void setForumPosts(List<Post> forumPosts) {
        this.forumPosts = forumPosts;
    }

    public Forum() {
    }

    public Forum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getForum_id() {
        return forum_id;
    }

    public void setForum_id(long forum_id) {
        this.forum_id = forum_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    // Calculates whether the search string matches the forum's name, using Levenshtein distance for each word
    public boolean nameMatches(String search, double threshold) {
        String[] cleanSearch = search.toLowerCase().replaceAll("\\p{Punct}", "").split("\\s+");
        String[] cleanName = this.name.toLowerCase().replaceAll("\\p{Punct}", "").split("\\s+");
        double totalMatches = 0.0;
        for (String s: cleanSearch) {
            double maxMatch = 0.0;
            for (String t: cleanName) {
                double possibleMatch = ((double) levenshteinDistance(s, t)) / max(s.length(), t.length());
                if (possibleMatch < 0.5 && (1 - possibleMatch) > maxMatch) {
                    maxMatch = 1 - possibleMatch;
                }
                System.out.println(s + " " + t + " " + Double.toString(possibleMatch) + " " + Double.toString(maxMatch));
            }
            totalMatches += maxMatch;
        }
        System.out.println(search + " " + this.name + " " + Double.toString(totalMatches) + " " + Double.toString(threshold * cleanName.length));
        return totalMatches > threshold * cleanSearch.length;
    }

    private static int levenshteinDistance(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];
    
        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1] 
                     + (x.charAt(i - 1) == y.charAt(j - 1) ? 0 : 1), 
                      dp[i - 1][j] + 1, 
                      dp[i][j - 1] + 1);
                }
            }
        }
        return dp[x.length()][y.length()];
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
          .min().orElse(Integer.MAX_VALUE);
    }

    public static int max(int... numbers) {
        return Arrays.stream(numbers)
          .max().orElse(Integer.MIN_VALUE);
    }
}
