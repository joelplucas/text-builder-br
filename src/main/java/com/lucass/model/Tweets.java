package com.lucass.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;
import java.util.List;
import twitter4j.Status;

@Entity(noClassnameStored = true)
public class Tweets {
    
    @Id
    private String team;
    @Property
    private String text;
    @Property    
    private int numTweets = 0;
    @Property
    private long id;    
    
    public Tweets() {   
    }
    
    public Tweets(long minimumId, List<Status> tweetsStatus, String tag) {
        this.id = minimumId;
        this.team = tag;
        concatenateTweets(tweetsStatus);
    }

    private void concatenateTweets(List<Status> tweetsStatus) {
        StringBuilder textBuilder = new StringBuilder();
        for(Status tweetStatus : tweetsStatus) {
            textBuilder.append(tweetStatus.getText());
            numTweets++;
        }
        this.text = textBuilder.toString();        
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long userId) {
        this.id = userId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getNumTweets() {
        return numTweets;
    }

    public void setNumTweets(int numTweets) {
        this.numTweets = numTweets;
    }    
}
