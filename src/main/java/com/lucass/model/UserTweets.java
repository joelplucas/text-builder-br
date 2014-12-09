package com.lucass.model;

import com.lucass.model.Tweet;
import java.io.Serializable;
import java.util.List;
 
public class UserTweets {
 
    private List<Tweet> tweets;

    public UserTweets() {
        
    }
    
    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    } 
}