package com.lucass.model;

import java.util.List;
 
public class TeamTweets {
 
    private List<Tweet> statuses;

    public TeamTweets() {
        
    }
    
    public List<Tweet> getTweets() {
        return statuses;
    }

    public void setTweets(List<Tweet> tweets) {
        this.statuses = tweets;
    } 
}