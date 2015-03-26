package com.lucass.crawler;

import com.google.code.morphia.Datastore;
import com.google.gson.Gson;
import com.lucass.model.Team;
import com.lucass.model.TeamTweets;
import com.lucass.model.Tweet;
import com.lucass.utils.MongoDBConnector;
import java.util.ArrayList;
import java.util.List;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class CrawlerStarterTwitter4j {
    private final String CONSUMER_KEY = "AeidfIbX8t5taGVVQ1FCJL89p";
    private final String CONSUMER_KEY_SECRET = "d07jJ0Wr062cM59DcKQ2uY5M44EhMlOUrWfrUvKrbnD8i1dxQi";
    private final String ACCESS_TOKEN = "2891460417-1WMZPDxcu7oQigR4MkUo75y3BZqcRjORRgY5XFU";
    private final String ACCESS_TOKEN_SECRET = "jl5mr736jo50zZZfGqu2m67uo3v5wOZR7BjWr6EPz0Zmp";
    
    private Gson gson;
    private Datastore ds;
    private Twitter twitter;
    private List<Tweet> tweets;
    
    public CrawlerStarterTwitter4j() {
        twitter = new TwitterFactory().getInstance();
        ds = MongoDBConnector.getDatastore();
        gson = new Gson();
    }
    
    public static void main(String[] args) throws Exception {    
        CrawlerStarterTwitter4j crawler = new CrawlerStarterTwitter4j();
        crawler.run();
    }

    public void run() {
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
        AccessToken accessToken = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
        twitter.setOAuthAccessToken(accessToken);
        
        List<Team> teams = getTeams(ds);
        tweets = getTeamsTweets(teams);
        
        System.out.println(tweets.size());        
    }
    
    private List<Tweet> getTeamsTweets(List<Team> teams) {
        List<Tweet> tweets = new ArrayList<Tweet>();
        for(Team team : teams) {
            for(String userId : team.getWords()) {     
                try {
                    for(Status status : twitter.getUserTimeline(userId)) {
                        String a = status.getText();
                        System.out.println(a);
                        /*TeamTweets userTweets = gson.fromJson(status.getText(), TeamTweets.class);
                        List<Tweet> tweetsFromUser = userTweets.getTweets();
                        tweets.addAll(tweetsFromUser);*/
                    }
                } catch (TwitterException ex) {
                    System.out.println("Twitter error for " + userId);
                    ex.printStackTrace();                    
                }
            }
        }
        return tweets;
    }

    
    private List<Team> getTeams(Datastore ds) {
        List<Team> teams = ds.createQuery(Team.class).asList();       
        return teams;
    }

}
